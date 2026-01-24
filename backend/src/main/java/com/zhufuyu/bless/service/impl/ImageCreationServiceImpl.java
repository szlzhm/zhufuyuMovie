package com.zhufuyu.bless.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhufuyu.bless.entity.ImageGenerationResultEntity;
import com.zhufuyu.bless.entity.ImageGenerationTaskEntity;
import com.zhufuyu.bless.entity.ImagePromptEntity;
import com.zhufuyu.bless.exception.BizException;
import com.zhufuyu.bless.model.request.*;
import com.zhufuyu.bless.model.response.ImageGenerationTaskResp;
import com.zhufuyu.bless.model.response.ImageResultResp;
import com.zhufuyu.bless.model.response.common.PageResponse;
import com.zhufuyu.bless.repository.ImageGenerationResultRepository;
import com.zhufuyu.bless.repository.ImageGenerationTaskRepository;
import com.zhufuyu.bless.repository.ImagePromptRepository;
import com.zhufuyu.bless.service.ImageCreationService;
import com.zhufuyu.bless.util.SnowflakeIdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 图片创作核心服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImageCreationServiceImpl implements ImageCreationService {

    private final ImagePromptRepository promptRepository;
    private final ImageGenerationTaskRepository taskRepository;
    private final ImageGenerationResultRepository resultRepository;
    private final SnowflakeIdGenerator idGenerator;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public Long submitImageTask(ImagePromptSubmitReq request) {
        // 1. 保存提示语
        ImagePromptEntity promptEntity = new ImagePromptEntity();
        promptEntity.setTemplateId(request.getTemplateId());
        promptEntity.setPromptContent(request.getPromptContent());
        promptEntity.setNegativePrompt(request.getNegativePrompt());
        final ImagePromptEntity savedPrompt = promptRepository.save(promptEntity);

        // 2. 创建任务
        ImageGenerationTaskEntity taskEntity = new ImageGenerationTaskEntity();
        taskEntity.setPromptId(savedPrompt.getId());
        taskEntity.setResolution(request.getResolution());
        taskEntity.setNumImages(request.getNumImages());
        taskEntity.setSeed(request.getSeed());
        taskEntity.setSmartOptimization(request.getSmartOptimization());
        taskEntity.setInferenceSteps(request.getInferenceSteps());
        taskEntity.setCfgScale(request.getCfgScale());
        taskEntity.setNegativePrompt(request.getNegativePrompt());
        taskEntity.setEnableCustomParams(request.getEnableCustomParams());
        if (request.getEnableCustomParams() != null && request.getEnableCustomParams() && request.getCustomParams() != null) {
            try {
                taskEntity.setCustomParams(objectMapper.writeValueAsString(request.getCustomParams()));
            } catch (JsonProcessingException e) {
                log.error("Failed to serialize custom params", e);
            }
        }
        taskEntity.setTaskStatus("WAITING");
        taskEntity.setStatusChangedTime(LocalDateTime.now());
        final ImageGenerationTaskEntity savedTask = taskRepository.save(taskEntity);

        return savedTask.getId();
    }

    @Override
    public PageResponse<ImageGenerationTaskResp> queryTasks(ImageTaskQueryReq request) {
        Pageable pageable = PageRequest.of(request.getPageNo() - 1, request.getPageSize(), Sort.by(Sort.Direction.DESC, "createdTime"));
        Page<ImageGenerationTaskEntity> page;

        if ("STATUS_CHANGED".equals(request.getFilterTimeType())) {
            page = taskRepository.searchTasksByStatusUpdateTime(request.getTaskStatus(), request.getPrompt(), request.getStartTime(), request.getEndTime(), pageable);
        } else {
            page = taskRepository.searchTasks(request.getTaskStatus(), request.getPrompt(), request.getStartTime(), request.getEndTime(), pageable);
        }

        List<ImageGenerationTaskResp> list = page.getContent().stream().map(this::convertToTaskResp).collect(Collectors.toList());
        return new PageResponse<>(page.getNumber() + 1, page.getSize(), page.getTotalElements(), list);
    }

    @Override
    @Transactional
    public void updateTaskStatus(ImageTaskStatusUpdateReq request) {
        ImageGenerationTaskEntity entity = taskRepository.findById(request.getId())
                .orElseThrow(() -> new BizException(500, "任务不存在"));
        
        if (!entity.getTaskStatus().equals(request.getTaskStatus())) {
            entity.setTaskStatus(request.getTaskStatus());
            entity.setStatusChangedTime(LocalDateTime.now());
            taskRepository.save(entity);
        }
    }

    @Override
    public PageResponse<ImageResultResp> queryResults(ImageResultQueryReq request) {
        Pageable pageable = PageRequest.of(request.getPageNo() - 1, request.getPageSize(), Sort.by(Sort.Direction.DESC, "createdTime"));
        
        Page<ImageGenerationResultEntity> page = resultRepository.searchResults(
                request.getPrompt(),
                request.getPromptStartTime(),
                request.getPromptEndTime(),
                request.getCompletedStartTime(),
                request.getCompletedEndTime(),
                pageable
        );

        List<ImageResultResp> list = page.getContent().stream().map(this::convertToResultResp).collect(Collectors.toList());
        return new PageResponse<>(page.getNumber() + 1, page.getSize(), page.getTotalElements(), list);
    }

    @Override
    @Transactional
    public void registerResult(ImageResultRegisterReq request) {
        ImageGenerationTaskEntity task = taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new BizException(500, "任务不存在"));

        // 查找是否已存在该任务的结果
        ImageGenerationResultEntity resultEntity = resultRepository.findByTaskId(request.getTaskId())
                .stream()
                .findFirst()
                .orElse(null);
        
        // 如果不存在则创建新记录
        if (resultEntity == null) {
            resultEntity = new ImageGenerationResultEntity();
            resultEntity.setTaskId(task.getId());
            resultEntity.setPromptId(task.getPromptId());
            resultEntity.setImageId(String.valueOf(idGenerator.nextId()));
        }
        
        // 处理多图路径
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            List<String> paths = request.getImages().stream()
                    .map(ImageResultRegisterReq.ImageItem::getPath)
                    .collect(Collectors.toList());
            
            // 生成前端代理URL（路径中的反斜杠转换为正斜杠）
            List<String> urls = paths.stream()
                    .map(path -> "/bless/web/img/" + path.replace("\\", "/"))
                    .collect(Collectors.toList());
            
            try {
                resultEntity.setImagePaths(objectMapper.writeValueAsString(paths));
                resultEntity.setImageUrls(objectMapper.writeValueAsString(urls));
            } catch (JsonProcessingException e) {
                log.error("Failed to serialize image paths/urls", e);
            }
        }
        
        // 更新其他字段
        resultEntity.setGenerationTime(request.getGenerationTime());
        resultEntity.setCompletedTime(request.getCompletedTime() != null ? request.getCompletedTime() : LocalDateTime.now());
        
        resultRepository.save(resultEntity);
        
        // 更新任务状态为已完成
        task.setTaskStatus("COMPLETED");
        task.setStatusChangedTime(LocalDateTime.now());
        taskRepository.save(task);
    }

    @Override
    public ImageGenerationTaskResp getTaskDetail(Long taskId) {
        ImageGenerationTaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new BizException(500, "任务不存在"));
        
        ImageGenerationTaskResp resp = convertToTaskResp(task);
        
        // 获取该任务生成的所有结果
        List<ImageGenerationResultEntity> results = resultRepository.findByTaskId(taskId);
        resp.setResults(results.stream().map(this::convertToResultResp).collect(Collectors.toList()));
        
        return resp;
    }

    @Override
    public ImageGenerationTaskResp getEarliestWaitingTask() {
        return taskRepository.findFirstByTaskStatusOrderByCreatedTimeAsc("WAITING")
                .map(this::convertToTaskResp)
                .orElse(null);
    }

    @Override
    @Transactional
    public void handleTaskPullSuccess(Long taskId) {
        ImageGenerationTaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new BizException(10001, "任务不存在"));

        if (!"WAITING".equals(task.getTaskStatus())) {
            throw new BizException(10005, "任务状态不是等待中，无法开始处理");
        }

        task.setTaskStatus("PROCESSING");
        task.setStatusChangedTime(LocalDateTime.now());
        taskRepository.save(task);
    }

    @Override
    @Transactional
    public void reportTaskFailure(ImageTaskReportFailureReq request) {
        ImageGenerationTaskEntity task = taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new BizException(10001, "任务不存在"));

        task.setTaskStatus("FAILED");
        task.setErrorMessage(request.getErrorMsg());
        task.setStatusChangedTime(LocalDateTime.now());
        taskRepository.save(task);
    }

    private ImageGenerationTaskResp convertToTaskResp(ImageGenerationTaskEntity entity) {
        ImageGenerationTaskResp resp = new ImageGenerationTaskResp();
        BeanUtils.copyProperties(entity, resp);
        
        if (entity.getEnableCustomParams() != null && entity.getEnableCustomParams() && entity.getCustomParams() != null) {
            try {
                resp.setCustomParams(objectMapper.readValue(entity.getCustomParams(), new TypeReference<java.util.Map<String, String>>() {}));
            } catch (JsonProcessingException e) {
                log.error("Failed to deserialize custom params", e);
            }
        }
        
        // 补充提示语内容
        promptRepository.findById(entity.getPromptId()).ifPresent(p -> resp.setPromptContent(p.getPromptContent()));
        
        return resp;
    }

    private ImageResultResp convertToResultResp(ImageGenerationResultEntity entity) {
        ImageResultResp resp = new ImageResultResp();
        BeanUtils.copyProperties(entity, resp);
        
        // 反序列化多图路径
        try {
            if (entity.getImagePaths() != null) {
                resp.setImagePaths(objectMapper.readValue(entity.getImagePaths(), new TypeReference<List<String>>() {}));
            }
            if (entity.getImageUrls() != null) {
                resp.setImageUrls(objectMapper.readValue(entity.getImageUrls(), new TypeReference<List<String>>() {}));
            }
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize image paths/urls", e);
        }
        
        // 补充提示语内容和任务参数
        taskRepository.findById(entity.getTaskId()).ifPresent(task -> {
            resp.setResolution(task.getResolution());
            resp.setNumImages(task.getNumImages());
            resp.setSeed(task.getSeed());
            resp.setSmartOptimization(task.getSmartOptimization());
            resp.setInferenceSteps(task.getInferenceSteps());
            resp.setCfgScale(task.getCfgScale());
            resp.setNegativePrompt(task.getNegativePrompt());
            resp.setEnableCustomParams(task.getEnableCustomParams() != null && task.getEnableCustomParams() ? 1 : 0);
            
            if (task.getEnableCustomParams() != null && task.getEnableCustomParams() && task.getCustomParams() != null) {
                try {
                    resp.setCustomParams(objectMapper.readValue(task.getCustomParams(), new TypeReference<java.util.Map<String, Object>>() {}));
                } catch (JsonProcessingException e) {
                    log.error("Failed to deserialize custom params", e);
                }
            }
        });

        promptRepository.findById(entity.getPromptId()).ifPresent(p -> resp.setPromptContent(p.getPromptContent()));
        
        return resp;
    }
}
