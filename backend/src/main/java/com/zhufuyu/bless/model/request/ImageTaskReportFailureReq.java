package com.zhufuyu.bless.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ImageTaskReportFailureReq {
    @NotNull(message = "任务ID不能为空")
    private Long taskId;
    
    private String errorMsg;
}
