package com.zhufuyu.bless.model.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ImagePromptTemplateResp {
    private Long id;
    private String templateContent;
    private String placeholderKeywords;
    private Integer templateStatus;
    private java.util.Map<String, Object> parameters;
    private String templateImagePath;
    private String templateImageUrl;
    private LocalDateTime createdTime;
}
