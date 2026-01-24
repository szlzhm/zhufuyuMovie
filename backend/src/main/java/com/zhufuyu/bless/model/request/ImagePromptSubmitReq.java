package com.zhufuyu.bless.model.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ImagePromptSubmitReq {

    private Long templateId;

    @NotBlank(message = "提示语内容不能为空")
    private String promptContent;

    private String negativePrompt;

    @NotBlank(message = "分辨率不能为空")
    private String resolution;

    @Min(value = 1, message = "图片数量至少为1")
    @Max(value = 4, message = "图片数量最多为4")
    private Integer numImages = 1;

    private Long seed = -1L;

    private Integer smartOptimization = 0;
    
    private Integer inferenceSteps = 4;
    
    private Double cfgScale = 1.0;
    
    private Boolean enableCustomParams = false;
    
    private java.util.Map<String, String> customParams;
}
