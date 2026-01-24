package com.zhufuyu.bless.model.response;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 负面提示语响应类
 */
@Data
public class NegativePromptResp {

    private Long id;
    private String content;
    private String remark;
    private LocalDateTime createdTime;
}
