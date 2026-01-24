package com.zhufuyu.bless.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SaveTemplateFromResultReq {

    @NotNull(message = "结果ID不能为空")
    private Long resultId;

    @NotBlank(message = "模板内容不能为空")
    @Size(max = 5000, message = "模板内容最多5000个字符")
    private String templateContent;

    @Size(max = 500, message = "占位符关键词最多500个字符")
    private String placeholderKeywords;

    private Integer templateStatus = 1;

    private java.util.Map<String, Object> parameters;

    @NotBlank(message = "第一张图片URL不能为空")
    private String firstImageUrl;
}
