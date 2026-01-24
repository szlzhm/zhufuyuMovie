package com.zhufuyu.bless.model.response;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ImageGenerationTaskResp {
    private Long id;
    private Long promptId;
    private String promptContent;
    private String negativePrompt;
    private String resolution;
    private Integer numImages;
    private Long seed;
    private Integer smartOptimization;
    private Integer inferenceSteps;
    private Double cfgScale;
    private Boolean enableCustomParams;
    private java.util.Map<String, String> customParams;
    private String taskStatus;
    private String errorMessage;
    private LocalDateTime createdTime;
    private LocalDateTime statusChangedTime;
    private Long templateId;
    private List<ImageResultResp> results;
}
