package com.zhufuyu.bless.model.request;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ImageTaskQueryReq {
    private Long taskId;
    private String taskStatus;
    private String prompt;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String filterTimeType; // "CREATED" or "STATUS_CHANGED"
    private Integer pageNo = 1;
    private Integer pageSize = 10;
}
