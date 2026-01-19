package com.zhufuyu.bless.model.response.video;

import lombok.Data;
import java.util.List;

@Data
public class BlessVideoResp {
    private Long id;
    private String title;
    private String videoPath;
    private String coverPath;
    private String textMaterialName;
    private Integer duration;
    private String createdTime;
    private String generatedTime;
    private Integer publishStatus;
    private String publishStatusText;  // 已发布/未发布
    private List<VideoPublishRecordResp> publishRecords;  // 发布记录列表

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public String getTextMaterialName() {
        return textMaterialName;
    }

    public void setTextMaterialName(String textMaterialName) {
        this.textMaterialName = textMaterialName;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getGeneratedTime() {
        return generatedTime;
    }

    public void setGeneratedTime(String generatedTime) {
        this.generatedTime = generatedTime;
    }

    public Integer getPublishStatus() {
        return publishStatus;
    }

    public void setPublishStatus(Integer publishStatus) {
        this.publishStatus = publishStatus;
    }

    public String getPublishStatusText() {
        return publishStatusText;
    }

    public void setPublishStatusText(String publishStatusText) {
        this.publishStatusText = publishStatusText;
    }

    public List<VideoPublishRecordResp> getPublishRecords() {
        return publishRecords;
    }

    public void setPublishRecords(List<VideoPublishRecordResp> publishRecords) {
        this.publishRecords = publishRecords;
    }
}