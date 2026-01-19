package com.zhufuyu.bless.service.impl;

import com.zhufuyu.bless.entity.BlessVideoEntity;
import com.zhufuyu.bless.entity.ImageMaterialEntity;
import com.zhufuyu.bless.entity.MusicMaterialEntity;
import com.zhufuyu.bless.entity.VideoTaskEntity;
import com.zhufuyu.bless.exception.BizException;
import com.zhufuyu.bless.model.request.task.VideoTaskCreateReq;
import com.zhufuyu.bless.model.request.task.VideoTaskListReq;
import com.zhufuyu.bless.model.response.common.PageResponse;
import com.zhufuyu.bless.model.response.task.VideoTaskResp;
import com.zhufuyu.bless.repository.BlessVideoRepository;
import com.zhufuyu.bless.repository.ImageMaterialRepository;
import com.zhufuyu.bless.repository.MusicMaterialRepository;
import com.zhufuyu.bless.repository.VideoTaskRepository;
import com.zhufuyu.bless.service.VideoTaskService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VideoTaskServiceImpl implements VideoTaskService {

    private final VideoTaskRepository videoTaskRepository;
    private final ImageMaterialRepository imageMaterialRepository;
    private final MusicMaterialRepository musicMaterialRepository;
    private final BlessVideoRepository blessVideoRepository;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public VideoTaskServiceImpl(VideoTaskRepository videoTaskRepository,
                                 ImageMaterialRepository imageMaterialRepository,
                                 MusicMaterialRepository musicMaterialRepository,
                                 BlessVideoRepository blessVideoRepository) {
        this.videoTaskRepository = videoTaskRepository;
        this.imageMaterialRepository = imageMaterialRepository;
        this.musicMaterialRepository = musicMaterialRepository;
        this.blessVideoRepository = blessVideoRepository;
    }

    @Override
    @Transactional
    public Long createTask(VideoTaskCreateReq request) {
        // 校验必填字段
        if (!StringUtils.hasText(request.getTaskName())) {
            throw new BizException(20001, "任务名称不能为空");
        }
        if (!StringUtils.hasText(request.getBatchName())) {
            throw new BizException(20001, "任务批次不能为空");
        }
        if (!StringUtils.hasText(request.getVideoTitle())) {
            throw new BizException(20001, "视频标题不能为空");
        }
        if (request.getBackgroundImageId() == null) {
            throw new BizException(20001, "背景图片不能为空");
        }
        if (!StringUtils.hasText(request.getVoiceAudioPath())) {
            throw new BizException(20001, "祝福语音频不能为空");
        }

        // 校验背景图片是否存在
        ImageMaterialEntity image = imageMaterialRepository.findById(request.getBackgroundImageId())
                .orElseThrow(() -> new BizException(20001, "背景图片不存在"));

        // 校验背景音乐(如果指定)
        if (request.getBackgroundMusicId() != null) {
            MusicMaterialEntity music = musicMaterialRepository.findById(request.getBackgroundMusicId())
                    .orElse(null);
            if (music == null) {
                throw new BizException(20001, "背景音乐不存在");
            }
        }

        // 创建任务
        VideoTaskEntity entity = new VideoTaskEntity();
        entity.setTaskName(request.getTaskName());
        entity.setBatchName(request.getBatchName());
        entity.setVideoTitle(request.getVideoTitle());
        entity.setTaskType("AUDIO_TO_VIDEO");
        entity.setBackgroundImageId(request.getBackgroundImageId());
        entity.setBackgroundMusicId(request.getBackgroundMusicId());
        entity.setVoiceAudioPath(request.getVoiceAudioPath());
        entity.setTextMaterialName(request.getTextMaterialName());
        entity.setTaskStatus("PENDING");
        entity.setConfirmedToLibrary(false);

        VideoTaskEntity saved = videoTaskRepository.save(entity);
        return saved.getId();
    }

    @Override
    public PageResponse<VideoTaskResp> listTasks(VideoTaskListReq request) {
        // 分页查询
        PageRequest pageRequest = PageRequest.of(
                request.getPageNo() - 1,
                request.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdTime")
        );

        Page<VideoTaskEntity> page = videoTaskRepository.findByConditions(
                request.getTaskName(),
                request.getBatchName(),
                request.getTextMaterialName(),
                pageRequest
        );

        // 转换为响应对象
        List<VideoTaskResp> list = page.getContent().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return new PageResponse<>(
                request.getPageNo(),
                request.getPageSize(),
                page.getTotalElements(),
                list
        );
    }

    @Override
    public VideoTaskResp getTaskById(Long id) {
        VideoTaskEntity entity = videoTaskRepository.findById(id)
                .orElseThrow(() -> new BizException(20001, "任务不存在"));
        return toResponse(entity);
    }

    @Override
    @Transactional
    public void executeTask(Long id) {
        VideoTaskEntity task = videoTaskRepository.findById(id)
                .orElseThrow(() -> new BizException(20001, "任务不存在"));

        if (!"PENDING".equals(task.getTaskStatus()) && !"FAILED".equals(task.getTaskStatus())) {
            throw new BizException(20001, "只能执行等待中或失败的任务");
        }

        // 更新状态为创作中
        task.setTaskStatus("PROCESSING");
        task.setGeneratedTime(LocalDateTime.now());
        task.setErrorMessage(null);
        videoTaskRepository.save(task);

        // 模拟视频合成过程(实际应该是异步任务)
        // 这里简化处理,直接生成模拟数据
        try {
            simulateVideoGeneration(task);
        } catch (Exception e) {
            task.setTaskStatus("FAILED");
            task.setErrorMessage("视频生成失败: " + e.getMessage());
            videoTaskRepository.save(task);
        }
    }

    @Override
    @Transactional
    public void executeTasks(List<Long> ids) {
        for (Long id : ids) {
            try {
                executeTask(id);
            } catch (Exception e) {
                // 记录错误但继续处理其他任务
                System.err.println("执行任务" + id + "失败: " + e.getMessage());
            }
        }
    }

    @Override
    @Transactional
    public void confirmToLibrary(Long id) {
        VideoTaskEntity task = videoTaskRepository.findById(id)
                .orElseThrow(() -> new BizException(20001, "任务不存在"));

        if (!"SUCCESS".equals(task.getTaskStatus())) {
            throw new BizException(20001, "只能确认成功的任务入库");
        }

        if (task.getConfirmedToLibrary()) {
            throw new BizException(20001, "该任务已确认入库");
        }

        // 创建视频记录
        BlessVideoEntity video = new BlessVideoEntity();
        video.setTitle(task.getVideoTitle());
        video.setVideoPath(task.getGeneratedVideoPath());
        video.setCoverPath(task.getGeneratedCoverPath());
        video.setTextMaterialName(task.getTextMaterialName());
        video.setDuration(task.getVideoDuration());
        video.setGeneratedTime(task.getGeneratedTime());
        video.setPublishStatus(0);

        BlessVideoEntity savedVideo = blessVideoRepository.save(video);

        // 更新任务状态
        task.setConfirmedToLibrary(true);
        task.setBlessVideoId(savedVideo.getId());
        videoTaskRepository.save(task);
    }

    /**
     * 模拟视频生成过程
     * 实际应该调用FFmpeg或视频处理服务
     */
    private void simulateVideoGeneration(VideoTaskEntity task) {
        try {
            // 模拟处理耗时
            Thread.sleep(1000);

            // 生成模拟的视频路径和封面路径
            String timestamp = System.currentTimeMillis() + "";
            String videoPath = "uploads/videos/" + timestamp + "/" + task.getVideoTitle() + ".mp4";
            String coverPath = "uploads/videos/" + timestamp + "/cover.jpg";

            // 模拟计算视频时长
            // 根据需求: 有BGM时 = 6s + VOICE_LEN, 无BGM时 = 2s + VOICE_LEN
            // 这里假设音频时长为10秒
            int voiceLen = 10;
            int duration = task.getBackgroundMusicId() != null ? (6 + voiceLen) : (2 + voiceLen);

            // 更新任务为成功
            task.setTaskStatus("SUCCESS");
            task.setGeneratedVideoPath(videoPath);
            task.setGeneratedCoverPath(coverPath);
            task.setVideoDuration(duration);
            videoTaskRepository.save(task);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("视频生成被中断", e);
        }
    }

    /**
     * 实体转响应对象
     */
    private VideoTaskResp toResponse(VideoTaskEntity entity) {
        VideoTaskResp resp = new VideoTaskResp();
        resp.setId(entity.getId());
        resp.setTaskName(entity.getTaskName());
        resp.setBatchName(entity.getBatchName());
        resp.setVideoTitle(entity.getVideoTitle());
        resp.setTaskType(entity.getTaskType());
        resp.setTaskStatus(entity.getTaskStatus());
        resp.setTaskStatusText(getStatusText(entity.getTaskStatus()));
        resp.setErrorMessage(entity.getErrorMessage());
        resp.setGeneratedVideoPath(entity.getGeneratedVideoPath());
        resp.setGeneratedCoverPath(entity.getGeneratedCoverPath());
        resp.setVideoDuration(entity.getVideoDuration());
        resp.setConfirmedToLibrary(entity.getConfirmedToLibrary());
        resp.setBlessVideoId(entity.getBlessVideoId());
        resp.setCreatedTime(entity.getCreatedTime().format(DATE_TIME_FORMATTER));
        resp.setGeneratedTime(entity.getGeneratedTime() != null 
            ? entity.getGeneratedTime().format(DATE_TIME_FORMATTER) 
            : null);
        return resp;
    }

    private String getStatusText(String status) {
        Map<String, String> statusMap = new HashMap<>();
        statusMap.put("PENDING", "等待中");
        statusMap.put("PROCESSING", "创作中");
        statusMap.put("SUCCESS", "成功");
        statusMap.put("FAILED", "失败");
        return statusMap.getOrDefault(status, status);
    }
}
