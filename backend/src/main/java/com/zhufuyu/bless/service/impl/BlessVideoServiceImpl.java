package com.zhufuyu.bless.service.impl;

import com.zhufuyu.bless.entity.BlessVideoEntity;
import com.zhufuyu.bless.entity.VideoPublishRecordEntity;
import com.zhufuyu.bless.exception.BizException;
import com.zhufuyu.bless.model.request.video.BlessVideoListReq;
import com.zhufuyu.bless.model.request.video.VideoPublishInfoUpdateReq;
import com.zhufuyu.bless.model.request.video.VideoPublishRecordReq;
import com.zhufuyu.bless.model.response.common.PageResponse;
import com.zhufuyu.bless.model.response.video.BlessVideoResp;
import com.zhufuyu.bless.model.response.video.VideoPublishRecordResp;
import com.zhufuyu.bless.repository.BlessVideoRepository;
import com.zhufuyu.bless.repository.VideoPublishRecordRepository;
import com.zhufuyu.bless.service.BlessVideoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BlessVideoServiceImpl implements BlessVideoService {

    private final BlessVideoRepository blessVideoRepository;
    private final VideoPublishRecordRepository publishRecordRepository;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public BlessVideoServiceImpl(BlessVideoRepository blessVideoRepository,
                                  VideoPublishRecordRepository publishRecordRepository) {
        this.blessVideoRepository = blessVideoRepository;
        this.publishRecordRepository = publishRecordRepository;
    }

    @Override
    public PageResponse<BlessVideoResp> listVideos(BlessVideoListReq request) {
        // 确定排序字段和方向
        String sortField = StringUtils.hasText(request.getSortField()) ? request.getSortField() : "createdTime";
        Sort.Direction sortDirection = "asc".equalsIgnoreCase(request.getSortOrder()) 
            ? Sort.Direction.ASC 
            : Sort.Direction.DESC;

        // 分页查询
        PageRequest pageRequest = PageRequest.of(
                request.getPageNo() - 1,
                request.getPageSize(),
                Sort.by(sortDirection, sortField)
        );

        Page<BlessVideoEntity> page = blessVideoRepository.findByConditions(
                request.getTitle(),
                pageRequest
        );

        // 批量查询发布记录
        List<Long> videoIds = page.getContent().stream()
                .map(BlessVideoEntity::getId)
                .collect(Collectors.toList());

        Map<Long, List<VideoPublishRecordEntity>> publishRecordsMap = new HashMap<>();
        if (!videoIds.isEmpty()) {
            List<VideoPublishRecordEntity> allRecords = publishRecordRepository.findByVideoIdIn(videoIds);
            publishRecordsMap = allRecords.stream()
                    .collect(Collectors.groupingBy(VideoPublishRecordEntity::getVideoId));
        }

        // 转换为响应对象
        Map<Long, List<VideoPublishRecordEntity>> finalPublishRecordsMap = publishRecordsMap;
        List<BlessVideoResp> list = page.getContent().stream()
                .map(entity -> toResponse(entity, finalPublishRecordsMap.get(entity.getId())))
                .collect(Collectors.toList());

        return new PageResponse<>(
                request.getPageNo(),
                request.getPageSize(),
                page.getTotalElements(),
                list
        );
    }

    @Override
    public BlessVideoResp getVideoById(Long id) {
        BlessVideoEntity entity = blessVideoRepository.findById(id)
                .orElseThrow(() -> new BizException(20001, "视频不存在"));

        List<VideoPublishRecordEntity> records = publishRecordRepository.findByVideoIdOrderByPublishTimeDesc(id);
        return toResponse(entity, records);
    }

    @Override
    @Transactional
    public void updateVideoPublishInfo(VideoPublishInfoUpdateReq request) {
        if (request.getVideoId() == null) {
            throw new BizException(20001, "视频ID不能为空");
        }

        // 检查视频是否存在
        BlessVideoEntity video = blessVideoRepository.findById(request.getVideoId())
                .orElseThrow(() -> new BizException(20001, "视频不存在"));

        if (request.getRecords() == null || request.getRecords().isEmpty()) {
            return;
        }

        // 处理发布记录
        for (VideoPublishRecordReq recordReq : request.getRecords()) {
            if (!StringUtils.hasText(recordReq.getChannelName())) {
                throw new BizException(20001, "视频号名称不能为空");
            }
            if (recordReq.getPublishTime() == null) {
                throw new BizException(20001, "发布时间不能为空");
            }

            if (recordReq.getId() != null) {
                // 更新现有记录
                VideoPublishRecordEntity entity = publishRecordRepository.findById(recordReq.getId())
                        .orElseThrow(() -> new BizException(20001, "发布记录不存在"));
                entity.setPublishTime(recordReq.getPublishTime());
                publishRecordRepository.save(entity);
            } else {
                // 检查是否已存在同一视频号的记录
                if (publishRecordRepository.existsByVideoIdAndChannelName(
                        request.getVideoId(), recordReq.getChannelName())) {
                    throw new BizException(20001, "该视频号已有发布记录，不能重复添加");
                }

                // 新增记录
                VideoPublishRecordEntity entity = new VideoPublishRecordEntity();
                entity.setVideoId(request.getVideoId());
                entity.setChannelName(recordReq.getChannelName());
                entity.setPublishTime(recordReq.getPublishTime());
                publishRecordRepository.save(entity);
            }
        }

        // 更新视频发布状态
        if (!request.getRecords().isEmpty()) {
            video.setPublishStatus(1);
            blessVideoRepository.save(video);
        }
    }

    /**
     * 实体转响应对象
     */
    private BlessVideoResp toResponse(BlessVideoEntity entity, List<VideoPublishRecordEntity> publishRecords) {
        BlessVideoResp resp = new BlessVideoResp();
        resp.setId(entity.getId());
        resp.setTitle(entity.getTitle());
        resp.setVideoPath(entity.getVideoPath());
        resp.setCoverPath(entity.getCoverPath());
        resp.setTextMaterialName(entity.getTextMaterialName());
        resp.setDuration(entity.getDuration());
        resp.setCreatedTime(entity.getCreatedTime().format(DATE_TIME_FORMATTER));
        resp.setGeneratedTime(entity.getGeneratedTime() != null 
            ? entity.getGeneratedTime().format(DATE_TIME_FORMATTER) 
            : null);
        resp.setPublishStatus(entity.getPublishStatus());
        resp.setPublishStatusText(entity.getPublishStatus() == 1 ? "已发布" : "未发布");

        // 转换发布记录
        if (publishRecords != null && !publishRecords.isEmpty()) {
            List<VideoPublishRecordResp> recordResps = publishRecords.stream()
                    .map(record -> {
                        VideoPublishRecordResp recordResp = new VideoPublishRecordResp();
                        recordResp.setId(record.getId());
                        recordResp.setChannelName(record.getChannelName());
                        recordResp.setPublishTime(record.getPublishTime().format(DATE_TIME_FORMATTER));
                        return recordResp;
                    })
                    .collect(Collectors.toList());
            resp.setPublishRecords(recordResps);
        } else {
            resp.setPublishRecords(new ArrayList<>());
        }

        return resp;
    }
}
