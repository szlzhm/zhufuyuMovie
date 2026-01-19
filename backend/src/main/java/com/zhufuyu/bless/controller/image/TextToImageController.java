package com.zhufuyu.bless.controller.image;

import com.zhufuyu.bless.model.common.BaseResponse;
import com.zhufuyu.bless.model.request.image.TextToImageRequest;
import com.zhufuyu.bless.model.response.image.TextToImageResponse;
import com.zhufuyu.bless.service.DashScopeImageGenerationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 文生图控制器
 */
@RestController
@RequestMapping("/api/image")
public class TextToImageController {
    
    private final DashScopeImageGenerationService dashScopeImageGenerationService;
    
    public TextToImageController(DashScopeImageGenerationService dashScopeImageGenerationService) {
        this.dashScopeImageGenerationService = dashScopeImageGenerationService;
    }
    
    /**
     * 生成图像
     */
    @PostMapping("/generate/v1")
    public BaseResponse<TextToImageResponse> generateImage(@RequestBody TextToImageRequest request) {
        TextToImageResponse response = dashScopeImageGenerationService.generateImage(request);
        return BaseResponse.success(response);
    }
}