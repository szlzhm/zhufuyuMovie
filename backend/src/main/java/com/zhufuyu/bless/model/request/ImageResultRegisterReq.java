package com.zhufuyu.bless.model.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ImageResultRegisterReq {
    @NotNull(message = "任务ID不能为空")
    private Long taskId;
    
    @NotEmpty(message = "图片列表不能为空")
    private List<ImageItem> images;
    
    private Double generationTime;
    
    private LocalDateTime completedTime;

    @Data
    public static class ImageItem {
        private String path;
        private String url;
    }
}
