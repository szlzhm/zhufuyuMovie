package com.zhufuyu.bless.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 负面提示语请求类
 */
@Data
public class NegativePromptReq {

    private Long id;

    @NotBlank(message = "内容不能为空")
    private String content;

    private String remark;
}
