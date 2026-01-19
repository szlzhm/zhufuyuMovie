package com.zhufuyu.bless.model.request.video;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class VideoPublishRecordReq {
    private Long id;              // 记录ID(更新时需要)
    private String channelName;   // 视频号名称
    private LocalDateTime publishTime;  // 发布时间

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public LocalDateTime getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(LocalDateTime publishTime) {
        this.publishTime = publishTime;
    }
}