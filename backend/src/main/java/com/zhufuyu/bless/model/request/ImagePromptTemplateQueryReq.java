package com.zhufuyu.bless.model.request;

import lombok.Data;

@Data
public class ImagePromptTemplateQueryReq {
    private String templateContent;
    private Integer page = 1;
    private Integer size = 10;
}
