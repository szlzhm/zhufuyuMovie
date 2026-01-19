package com.zhufuyu.bless.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhufuyu.bless.config.DashScopeConfig;
import com.zhufuyu.bless.config.TextToImageConfig;
import com.zhufuyu.bless.entity.TextToImageTaskEntity;
import com.zhufuyu.bless.model.request.image.AsyncTextToImageRequest;
import com.zhufuyu.bless.model.response.image.AsyncTextToImageResponse;
import com.zhufuyu.bless.repository.TextToImageTaskRepository;
import com.zhufuyu.bless.service.AsyncTextToImageService;
import com.zhufuyu.bless.service.DashScopeImageGenerationService;
import com.zhufuyu.bless.service.ImageStorageService;
import com.zhufuyu.bless.util.HttpClientUtil;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * 异步文生图服务实现
 */
@Service
public class AsyncTextToImageServiceImpl implements AsyncTextToImageService {

    @Resource
    private TextToImageTaskRepository textToImageTaskRepository;

    @Resource
    private DashScopeImageGenerationService dashScopeImageGenerationService;

    @Resource
    private ImageStorageService imageStorageService;

    @Resource
    private TextToImageConfig textToImageConfig;

    @Resource
    private DashScopeConfig dashScopeConfig;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public AsyncTextToImageResponse submitTask(AsyncTextToImageRequest request) {
        // 创建任务实体
        TextToImageTaskEntity taskEntity = new TextToImageTaskEntity();
        taskEntity.setTaskId("task_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8));
        taskEntity.setPrompt(request.getPrompt());
        taskEntity.setModelName(request.getModel());
        taskEntity.setNegativePrompt(request.getNegativePrompt());
        taskEntity.setImageCount(request.getN());
        taskEntity.setImageSize(request.getSize());
        taskEntity.setSeed(request.getSeed());
        taskEntity.setStatus("SUBMITTED");
        taskEntity.setProgress(0);
        taskEntity.setCreatedTime(LocalDateTime.now());
        taskEntity.setUpdatedTime(LocalDateTime.now());

        // 保存任务到数据库
        taskEntity = textToImageTaskRepository.save(taskEntity);

        // 异步执行文生图任务
        executeTextToImageTask(taskEntity.getTaskId(), request);

        // 构建响应
        AsyncTextToImageResponse response = new AsyncTextToImageResponse();
        response.setTaskId(taskEntity.getTaskId());
        response.setStatus(taskEntity.getStatus());
        response.setProgress(taskEntity.getProgress());
        response.setCreateTime(taskEntity.getCreatedTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli());

        return response;
    }

    @Override
    public AsyncTextToImageResponse getTaskStatus(String taskId) {
        Optional<TextToImageTaskEntity> taskOpt = textToImageTaskRepository.findByTaskId(taskId);
        if (!taskOpt.isPresent()) {
            AsyncTextToImageResponse response = new AsyncTextToImageResponse();
            response.setTaskId(taskId);
            response.setStatus("FAILED");
            response.setErrorMessage("任务不存在");
            return response;
        }

        TextToImageTaskEntity task = taskOpt.get();
        AsyncTextToImageResponse response = new AsyncTextToImageResponse();
        response.setTaskId(task.getTaskId());
        response.setStatus(task.getStatus());
        response.setProgress(task.getProgress());
        response.setErrorMessage(task.getErrorMessage());

        if (task.getImageUrls() != null) {
            // 解析JSON格式的图像URL数组
            try {
                java.util.List<String> urls = com.fasterxml.jackson.databind.ObjectMapper
                    .class.cast(new com.fasterxml.jackson.databind.ObjectMapper())
                    .readValue(task.getImageUrls(), java.util.List.class);
                response.setImageUrls(urls.toArray(new String[0]));
            } catch (Exception e) {
                // 解析失败，返回空数组
                response.setImageUrls(new String[0]);
            }
        }

        if (task.getCreatedTime() != null) {
            response.setCreateTime(task.getCreatedTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli());
        }
        if (task.getCompletedTime() != null) {
            response.setCompleteTime(task.getCompletedTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli());
        }

        return response;
    }

    @Async
    public void executeTextToImageTask(String taskId, AsyncTextToImageRequest request) {
        try {
            // 更新任务状态为处理中
            updateTaskProgress(taskId, "PROCESSING", 10, null, null);

            // 构建请求头
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + dashScopeConfig.getApiKey());
            headers.put("Content-Type", "application/json");
            
            // 构建请求体 - 使用同步API格式
            Map<String, Object> requestBodyMap = new HashMap<>();
            requestBodyMap.put("model", "qwen-image-max"); // 使用API中正确的模型名称
            
            // 构建messages数组
            Map<String, Object> input = new HashMap<>();
            List<Map<String, Object>> messages = new ArrayList<>();
            Map<String, Object> message = new HashMap<>();
            message.put("role", "user");
            
            // 构建content数组
            List<Map<String, String>> content = new ArrayList<>();
            Map<String, String> textContent = new HashMap<>();
            textContent.put("text", request.getPrompt());
            content.add(textContent);
            
            message.put("content", content);
            messages.add(message);
            input.put("messages", messages);
            requestBodyMap.put("input", input);
            
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("n", request.getN());
            parameters.put("size", request.getSize());
            if (request.getNegativePrompt() != null && !request.getNegativePrompt().isEmpty()) {
                parameters.put("negative_prompt", request.getNegativePrompt());
            }
            parameters.put("prompt_extend", true);
            parameters.put("watermark", false);
            if (request.getSeed() != null) {
                parameters.put("seed", request.getSeed());
            }
            requestBodyMap.put("parameters", parameters);
            
            String requestBody = objectMapper.writeValueAsString(requestBodyMap);
            
            // 发送同步API请求（模拟异步行为）
            updateTaskProgress(taskId, "PROCESSING", 30, null, null);
            String response = HttpClientUtil.post(textToImageConfig.getApi().getSyncUrl(), headers, requestBody);
            
            // 解析响应
            JsonNode responseNode = objectMapper.readTree(response);
            JsonNode outputNode = responseNode.get("output");
            
            if (outputNode != null) {
                JsonNode resultsNode = outputNode.get("results");
                if (resultsNode != null && resultsNode.isArray()) {
                    List<String> imageUrls = new ArrayList<>();
                    for (JsonNode resultNode : resultsNode) {
                        JsonNode urlNode = resultNode.get("url");
                        if (urlNode != null) {
                            imageUrls.add(urlNode.asText());
                        }
                    }
                    
                    if (!imageUrls.isEmpty()) {
                        // 下载并保存图片到本地
                        updateTaskProgress(taskId, "PROCESSING", 70, null, null);
                        List<String> localImagePaths = imageStorageService.downloadAndSaveImages(imageUrls);
                        
                        // 更新任务进度
                        updateTaskProgress(taskId, "SUCCESS", 100, localImagePaths, null);
                        return;
                    }
                }
            }
            
            updateTaskProgress(taskId, "FAILED", 0, null, "API响应中未找到有效的图像URL");
        } catch (Exception e) {
            updateTaskProgress(taskId, "FAILED", 0, null, e.getMessage());
        }
    }

    private void updateTaskProgress(String taskId, String status, Integer progress, java.util.List<String> imageUrls, String errorMessage) {
        Optional<TextToImageTaskEntity> taskOpt = textToImageTaskRepository.findByTaskId(taskId);
        if (!taskOpt.isPresent()) {
            return;
        }

        TextToImageTaskEntity task = taskOpt.get();
        task.setStatus(status);
        task.setProgress(progress);
        task.setErrorMessage(errorMessage);
        task.setUpdatedTime(LocalDateTime.now());

        if ("SUCCESS".equals(status)) {
            task.setCompletedTime(LocalDateTime.now());
            if (imageUrls != null) {
                try {
                    String jsonUrls = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(imageUrls);
                    task.setImageUrls(jsonUrls);
                } catch (Exception e) {
                    task.setErrorMessage("序列化图像URL失败: " + e.getMessage());
                }
            }
        }

        textToImageTaskRepository.save(task);
    }
    

}