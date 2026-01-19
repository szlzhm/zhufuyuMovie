package com.zhufuyu.bless.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhufuyu.bless.config.DashScopeConfig;
import com.zhufuyu.bless.config.TextToImageConfig;
import com.zhufuyu.bless.model.request.image.TextToImageRequest;
import com.zhufuyu.bless.model.response.image.TextToImageResponse;
import com.zhufuyu.bless.service.DashScopeImageGenerationService;
import com.zhufuyu.bless.util.HttpClientUtil;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * DashScope文生图服务实现 - 仅支持qwen-image-max模型
 */
@Service
public class DashScopeImageGenerationServiceImpl implements DashScopeImageGenerationService {
    
    private final DashScopeConfig dashScopeConfig;
    private final TextToImageConfig textToImageConfig;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public DashScopeImageGenerationServiceImpl(DashScopeConfig dashScopeConfig, TextToImageConfig textToImageConfig) {
        this.dashScopeConfig = dashScopeConfig;
        this.textToImageConfig = textToImageConfig;
    }
    
    @Override
    public TextToImageResponse generateImage(TextToImageRequest request) {
        // 构建请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + dashScopeConfig.getApiKey());
        headers.put("Content-Type", "application/json");
        
        // 构建请求体 - 使用qwen-image-max对应的API模型ID
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "qwen-image-max"); // qwen-image-max在API中的实际模型ID
        
        // 构建messages数组 - qwen-image模型的输入格式
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
        requestBody.put("input", input);
        
        // qwen-image模型的参数格式
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("n", request.getN());
        parameters.put("size", request.getSize());
        if (request.getNegativePrompt() != null && !request.getNegativePrompt().isEmpty()) {
            parameters.put("negative_prompt", request.getNegativePrompt());
        }
        // 添加额外的参数
        parameters.put("prompt_extend", true);
        parameters.put("watermark", false);
        if (request.getSeed() != null) {
            parameters.put("seed", request.getSeed());
        }
        requestBody.put("parameters", parameters);
        
        try {
            // 发送HTTP请求 - 使用同步API端点
            String response = HttpClientUtil.post(textToImageConfig.getApi().getSyncUrl(), headers, requestBody);
            
            // 解析响应
            JsonNode responseNode = objectMapper.readTree(response);
            
            TextToImageResponse result = new TextToImageResponse();
            result.setSuccess(true);
            result.setErrorMessage(null);
            
            // 提取图像URL
            JsonNode outputNode = responseNode.get("output");
            if (outputNode != null) {
                JsonNode choicesNode = outputNode.get("choices");
                if (choicesNode != null && choicesNode.isArray()) {
                    List<String> imageUrls = new ArrayList<>();
                    for (JsonNode choiceNode : choicesNode) {
                        JsonNode messageNode = choiceNode.get("message");
                        if (messageNode != null) {
                            JsonNode contentNode = messageNode.get("content");
                            if (contentNode != null && contentNode.isArray()) {
                                for (JsonNode contentItemNode : contentNode) {
                                    JsonNode imageNode = contentItemNode.get("image");
                                    if (imageNode != null) {
                                        imageUrls.add(imageNode.asText());
                                    }
                                }
                            }
                        }
                    }
                    result.setImageUrls(imageUrls);
                }
            }
            
            return result;
        } catch (Exception e) {
            TextToImageResponse errorResult = new TextToImageResponse();
            errorResult.setSuccess(false);
            errorResult.setErrorMessage("调用文生图API失败: " + e.getMessage());
            return errorResult;
        }
    }
}