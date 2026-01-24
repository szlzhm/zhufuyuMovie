package com.zhufuyu.bless.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhufuyu.bless.config.DashScopeConfig;
import com.zhufuyu.bless.config.TextToImageConfig;
import com.zhufuyu.bless.model.request.ImagePromptSubmitReq;
import com.zhufuyu.bless.model.request.ImageResultRegisterReq;
import com.zhufuyu.bless.model.request.ImageTaskStatusUpdateReq;
import com.zhufuyu.bless.service.ImageCreationService;
import com.zhufuyu.bless.service.ImageStorageService;
import com.zhufuyu.bless.service.QwenImageGenerationService;
import com.zhufuyu.bless.util.HttpClientUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Qwen-Image 模型生成服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QwenImageGenerationServiceImpl implements QwenImageGenerationService {

    private final DashScopeConfig dashScopeConfig;
    private final TextToImageConfig textToImageConfig;
    private final ImageCreationService imageCreationService;
    private final ImageStorageService imageStorageService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Async
    public void executeGenerationTask(Long taskId, String prompt, ImagePromptSubmitReq params) {
        log.info("Starting image generation task: {}, prompt: {}", taskId, prompt);
        try {
            // 1. 更新任务状态为 PROCESSING
            ImageTaskStatusUpdateReq statusUpdate = new ImageTaskStatusUpdateReq();
            statusUpdate.setId(taskId);
            statusUpdate.setTaskStatus("PROCESSING");
            imageCreationService.updateTaskStatus(statusUpdate);

            // 2. 构建 DashScope 请求
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + dashScopeConfig.getApiKey());
            headers.put("Content-Type", "application/json");

            Map<String, Object> requestBody = new HashMap<>();
            // Qwen-Image-v25 是当前支持 Multimodal Generation 的模型
            requestBody.put("model", "qwen-image-v25");

            Map<String, Object> input = new HashMap<>();
            List<Map<String, Object>> messages = new ArrayList<>();
            Map<String, Object> message = new HashMap<>();
            message.put("role", "user");
            
            List<Map<String, String>> content = new ArrayList<>();
            Map<String, String> textContent = new HashMap<>();
            textContent.put("text", prompt);
            content.add(textContent);
            
            message.put("content", content);
            messages.add(message);
            input.put("messages", messages);
            requestBody.put("input", input);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("size", params.getResolution());
            parameters.put("n", params.getNumImages());
            if (params.getSeed() != null && params.getSeed() != -1) {
                parameters.put("seed", params.getSeed());
            }
            // 提示语智能优化
            parameters.put("prompt_extend", params.getSmartOptimization() == 1);
            
            // 负面提示语
            if (params.getNegativePrompt() != null && !params.getNegativePrompt().isEmpty()) {
                parameters.put("negative_prompt", params.getNegativePrompt());
            }
            
            // 推理步数和CFG
            parameters.put("steps", params.getInferenceSteps());
            parameters.put("cfg_scale", params.getCfgScale());
            
            requestBody.put("parameters", parameters);

            // 3. 发送请求
            log.info("Requesting DashScope: {}", objectMapper.writeValueAsString(requestBody));
            String response = HttpClientUtil.post(textToImageConfig.getApi().getSyncUrl(), headers, requestBody);
            log.info("DashScope response for task {}: {}", taskId, response);

            // 4. 解析结果
            JsonNode responseNode = objectMapper.readTree(response);
            
            if (responseNode.has("output") && responseNode.get("output").has("choices")) {
                JsonNode choices = responseNode.get("output").get("choices");
                List<ImageResultRegisterReq.ImageItem> resultImages = new ArrayList<>();
                
                long startTime = System.currentTimeMillis();
                for (JsonNode choice : choices) {
                    JsonNode msgNode = choice.get("message");
                    if (msgNode != null && msgNode.has("content")) {
                        JsonNode contentItems = msgNode.get("content");
                        if (contentItems.isArray()) {
                            for (JsonNode item : contentItems) {
                                if (item.has("image")) {
                                    String imageUrl = item.get("image").asText();
                                    
                                    // 下载并保存图片
                                    String fileName = "qwen_" + taskId + "_" + UUID.randomUUID().toString().substring(0, 8) + ".png";
                                    String localPath = imageStorageService.downloadAndSaveImage(imageUrl, fileName);
                                    
                                    ImageResultRegisterReq.ImageItem imageItem = new ImageResultRegisterReq.ImageItem();
                                    imageItem.setPath(localPath);
                                    imageItem.setUrl(imageUrl);
                                    resultImages.add(imageItem);
                                }
                            }
                        }
                    }
                }
                long endTime = System.currentTimeMillis();
                
                // 注册生成结果
                if (!resultImages.isEmpty()) {
                    ImageResultRegisterReq registerReq = new ImageResultRegisterReq();
                    registerReq.setTaskId(taskId);
                    registerReq.setImages(resultImages);
                    registerReq.setGenerationTime((endTime - startTime) / 1000.0);
                    imageCreationService.registerResult(registerReq);
                }
                
                // 状态更新已在 registerResult 中处理，此处无需额外更新
            } else {
                String errorMsg = "API 响应异常";
                if (responseNode.has("message")) {
                    errorMsg = responseNode.get("message").asText();
                } else if (responseNode.has("error")) {
                    errorMsg = responseNode.get("error").get("message").asText();
                }
                throw new RuntimeException("生成图像失败: " + errorMsg);
            }

        } catch (Exception e) {
            log.error("Image generation failed for task {}: ", taskId, e);
            try {
                ImageTaskStatusUpdateReq statusUpdate = new ImageTaskStatusUpdateReq();
                statusUpdate.setId(taskId);
                statusUpdate.setTaskStatus("FAILED");
                imageCreationService.updateTaskStatus(statusUpdate);
            } catch (Exception ex) {
                log.error("Failed to update status to FAILED for task {}", taskId, ex);
            }
        }
    }
}
