package com.zhufuyu.bless.model.request;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ImageResultQueryReq {
    private String prompt;
    private LocalDateTime promptStartTime;
    private LocalDateTime promptEndTime;
    private LocalDateTime completedStartTime;
    private LocalDateTime completedEndTime;
    private Integer pageNo = 1;
    private Integer pageSize = 10;
}
