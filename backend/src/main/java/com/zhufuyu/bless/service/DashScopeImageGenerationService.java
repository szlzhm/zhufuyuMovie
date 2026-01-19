package com.zhufuyu.bless.service;

import com.zhufuyu.bless.model.request.image.TextToImageRequest;
import com.zhufuyu.bless.model.response.image.TextToImageResponse;

/**
 * DashScope文生图服务接口
 */
public interface DashScopeImageGenerationService {
    
    /**
     * 生成图像
     * @param request 文生图请求
     * @return 文生图响应
     */
    TextToImageResponse generateImage(TextToImageRequest request);
}