package com.zhufuyu.bless.model.response;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ImageResultResp {
    private Long id;
    private Long promptId;
    private String promptContent;
    private Long taskId;
    private List<String> imagePaths;
    private List<String> imageUrls;
    private String imageId;
    private Double generationTime;
    private LocalDateTime completedTime;
    private LocalDateTime createdTime;
    private Long templateId;

    // 增加任务参数字段，用于保存为模板
    private String resolution;
    private Integer numImages;
    private Long seed;
    private Integer smartOptimization;
    private Integer inferenceSteps;
    private Double cfgScale;
    private String negativePrompt;
    private Integer enableCustomParams;
    private java.util.Map<String, Object> customParams;
}
