package com.zhufuyu.bless.model.request.video;

import lombok.Data;
import java.util.List;

@Data
public class VideoPublishInfoUpdateReq {
    private Long videoId;  // 视频ID
    private List<VideoPublishRecordReq> records;  // 发布记录列表

    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public List<VideoPublishRecordReq> getRecords() {
        return records;
    }

    public void setRecords(List<VideoPublishRecordReq> records) {
        this.records = records;
    }
}