package com.zhufuyu.bless.model.request;

import lombok.Data;

/**
 * 负面提示语查询请求类
 */
@Data
public class NegativePromptQueryReq {

    private Integer page = 1;
    private Integer size = 10;
    private String content;
}
