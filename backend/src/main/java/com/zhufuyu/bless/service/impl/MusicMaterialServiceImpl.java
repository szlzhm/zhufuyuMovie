package com.zhufuyu.bless.service.impl;

import com.zhufuyu.bless.entity.SysEmotionEntity;
import com.zhufuyu.bless.entity.MusicMaterialEntity;
import com.zhufuyu.bless.exception.BizException;
import com.zhufuyu.bless.model.request.material.MusicMaterialCreateReq;
import com.zhufuyu.bless.model.request.material.MusicMaterialListReq;
import com.zhufuyu.bless.model.request.material.MusicMaterialUpdateReq;
import com.zhufuyu.bless.model.response.common.PageResponse;
import com.zhufuyu.bless.model.response.material.MusicMaterialResp;
import com.zhufuyu.bless.repository.SysEmotionRepository;
import com.zhufuyu.bless.repository.MusicMaterialRepository;
import com.zhufuyu.bless.service.MusicMaterialService;
import com.zhufuyu.bless.service.FileConfigService;
import com.zhufuyu.bless.util.FileUploadUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MusicMaterialServiceImpl implements MusicMaterialService {

    private final MusicMaterialRepository musicMaterialRepository;
    private final SysEmotionRepository emotionRepository;
    private final FileConfigService fileConfigService;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public MusicMaterialServiceImpl(MusicMaterialRepository musicMaterialRepository,
                                    SysEmotionRepository emotionRepository,
                                    FileConfigService fileConfigService) {
        this.musicMaterialRepository = musicMaterialRepository;
        this.emotionRepository = emotionRepository;
        this.fileConfigService = fileConfigService;
    }

    @Override
    public Long uploadMusicMaterial(MusicMaterialCreateReq request, MultipartFile file) {
        // 校验必填字段
        if (!StringUtils.hasText(request.getName())) {
            throw new BizException(20001, "背景音乐名称不能为空");
        }

        // 如果指定了情绪,校验情绪是否存在
        if (request.getEmotion() != null) {
            SysEmotionEntity emotion = emotionRepository.findById(request.getEmotion()).orElse(null);
            if (emotion == null) {
                throw new BizException(20001, "指定的情绪不存在");
            }
        }

        // 上传文件
        String rootPath = fileConfigService.getFileRootPath().getRootPath();
        String relativePath = FileUploadUtil.uploadAudio(file, rootPath);

        // 保存记录
        MusicMaterialEntity entity = new MusicMaterialEntity();
        entity.setName(request.getName());
        entity.setFilePath(relativePath);
        entity.setDescription(request.getDescription());
        entity.setEmotion(request.getEmotion());

        MusicMaterialEntity saved = musicMaterialRepository.save(entity);
        return saved.getId();
    }

    @Override
    public Long createMusicMaterial(MusicMaterialCreateReq request) {
        // 校验必填字段
        if (!StringUtils.hasText(request.getName())) {
            throw new BizException(20001, "背景音乐名称不能为空");
        }
        if (!StringUtils.hasText(request.getFilePath())) {
            throw new BizException(20001, "文件路径不能为空");
        }

        // 如果指定了情绪,校验情绪是否存在
        if (request.getEmotion() != null) {
            SysEmotionEntity emotion = emotionRepository.findById(request.getEmotion()).orElse(null);
            if (emotion == null) {
                throw new BizException(20001, "指定的情绪不存在");
            }
        }

        // 保存记录
        MusicMaterialEntity entity = new MusicMaterialEntity();
        entity.setName(request.getName());
        entity.setFilePath(request.getFilePath());
        entity.setDescription(request.getDescription());
        entity.setEmotion(request.getEmotion());

        MusicMaterialEntity saved = musicMaterialRepository.save(entity);
        return saved.getId();
    }

    @Override
    public PageResponse<MusicMaterialResp> listMusicMaterials(MusicMaterialListReq request) {
        // 构建排序对象
        Sort sort;
        if (StringUtils.hasText(request.getSortField())) {
            Sort.Direction direction = "asc".equalsIgnoreCase(request.getSortOrder()) 
                ? Sort.Direction.ASC 
                : Sort.Direction.DESC;
            sort = Sort.by(direction, request.getSortField());
        } else {
            // 默认按创建时间倒序
            sort = Sort.by(Sort.Direction.DESC, "createdTime");
        }
        
        // 分页查询
        PageRequest pageRequest = PageRequest.of(
                request.getPageNo() - 1,
                request.getPageSize(),
                sort
        );

        Page<MusicMaterialEntity> page = musicMaterialRepository.findByConditions(
                request.getName(),
                request.getEmotion(),
                pageRequest
        );

        // 批量查询情绪名称
        List<Long> emotionIds = page.getContent().stream()
                .map(MusicMaterialEntity::getEmotion)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, String> emotionNameMap = new HashMap<>();
        if (!emotionIds.isEmpty()) {
            List<SysEmotionEntity> emotions = emotionRepository.findAllById(emotionIds);
            emotionNameMap = emotions.stream()
                    .collect(Collectors.toMap(SysEmotionEntity::getId, SysEmotionEntity::getEmotionName));
        }

        // 转换为响应对象
        Map<Long, String> finalEmotionNameMap = emotionNameMap;
        List<MusicMaterialResp> list = page.getContent().stream()
                .map(entity -> toResponse(entity, finalEmotionNameMap))
                .collect(Collectors.toList());

        return new PageResponse<>(
                request.getPageNo(),
                request.getPageSize(),
                page.getTotalElements(),
                list
        );
    }

    @Override
    public MusicMaterialResp getMusicMaterialById(Long id) {
        MusicMaterialEntity entity = musicMaterialRepository.findById(id)
                .orElseThrow(() -> new BizException(20001, "背景音乐不存在"));

        // 查询情绪名称
        Map<Long, String> emotionNameMap = new HashMap<>();
        if (entity.getEmotion() != null) {
            SysEmotionEntity emotion = emotionRepository.findById(entity.getEmotion()).orElse(null);
            if (emotion != null) {
                emotionNameMap.put(emotion.getId(), emotion.getEmotionName());
            }
        }

        return toResponse(entity, emotionNameMap);
    }

    @Override
    public void updateMusicMaterial(MusicMaterialUpdateReq request) {
        // 校验必填字段
        if (request.getId() == null) {
            throw new BizException(20001, "音乐ID不能为空");
        }
        if (!StringUtils.hasText(request.getName())) {
            throw new BizException(20001, "背景音乐名称不能为空");
        }
        if (!StringUtils.hasText(request.getFilePath())) {
            throw new BizException(20001, "文件路径不能为空");
        }

        // 查询记录
        MusicMaterialEntity entity = musicMaterialRepository.findById(request.getId())
                .orElseThrow(() -> new BizException(20001, "背景音乐不存在"));

        // 如果指定了情绪,校验情绪是否存在
        if (request.getEmotion() != null) {
            SysEmotionEntity emotion = emotionRepository.findById(request.getEmotion()).orElse(null);
            if (emotion == null) {
                throw new BizException(20001, "指定的情绪不存在");
            }
        }

        // 更新字段
        entity.setName(request.getName());
        entity.setFilePath(request.getFilePath());
        entity.setDescription(request.getDescription());
        entity.setEmotion(request.getEmotion());

        musicMaterialRepository.save(entity);
    }

    @Override
    public void deleteMusicMaterial(Long id) {
        if (id == null) {
            throw new BizException(20001, "音乐ID不能为空");
        }

        MusicMaterialEntity entity = musicMaterialRepository.findById(id)
                .orElseThrow(() -> new BizException(20001, "背景音乐不存在"));

        musicMaterialRepository.delete(entity);
    }

    /**
     * 实体转响应对象
     */
    private MusicMaterialResp toResponse(MusicMaterialEntity entity, Map<Long, String> emotionNameMap) {
        MusicMaterialResp resp = new MusicMaterialResp();
        resp.setId(entity.getId());
        resp.setName(entity.getName());
        resp.setFilePath(entity.getFilePath());
        resp.setDescription(entity.getDescription());
        resp.setEmotion(entity.getEmotion());
        resp.setEmotionName(entity.getEmotion() != null ? emotionNameMap.get(entity.getEmotion()) : null);
        resp.setCreatedTime(entity.getCreatedTime().format(DATE_TIME_FORMATTER));
        return resp;
    }
}
