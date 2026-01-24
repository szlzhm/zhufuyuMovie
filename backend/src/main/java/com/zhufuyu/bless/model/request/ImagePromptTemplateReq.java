package com.zhufuyu.bless.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ImagePromptTemplateReq {

    private Long id;

    @NotBlank(message = "模板内容不能为空")
    @Size(max = 5000, message = "模板内容最多5000个字符")
    private String templateContent;

    @Size(max = 500, message = "占位符关键字最多500个字符")
    private String placeholderKeywords;

    private Integer templateStatus;

    private java.util.Map<String, Object> parameters;
}
