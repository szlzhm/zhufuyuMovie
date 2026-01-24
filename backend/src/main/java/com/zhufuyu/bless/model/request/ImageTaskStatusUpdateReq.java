package com.zhufuyu.bless.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ImageTaskStatusUpdateReq {
    @NotNull(message = "任务ID不能为空")
    private Long id;
    
    @NotBlank(message = "状态不能为空")
    private String taskStatus;
}
