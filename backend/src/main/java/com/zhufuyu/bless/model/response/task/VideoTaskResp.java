package com.zhufuyu.bless.model.response.task;

import lombok.Data;

@Data
public class VideoTaskResp {
    private Long id;
    private String taskName;
    private String batchName;
    private String videoTitle;
    private String taskType;
    private String taskStatus;
    private String taskStatusText;  // 状态文本: 等待中/创作中/成功/失败
    private String errorMessage;
    private String generatedVideoPath;
    private String generatedCoverPath;
    private Integer videoDuration;
    private Boolean confirmedToLibrary;
    private Long blessVideoId;
    private String createdTime;
    private String generatedTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getBatchName() {
        return batchName;
    }

    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getTaskStatusText() {
        return taskStatusText;
    }

    public void setTaskStatusText(String taskStatusText) {
        this.taskStatusText = taskStatusText;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getGeneratedVideoPath() {
        return generatedVideoPath;
    }

    public void setGeneratedVideoPath(String generatedVideoPath) {
        this.generatedVideoPath = generatedVideoPath;
    }

    public String getGeneratedCoverPath() {
        return generatedCoverPath;
    }

    public void setGeneratedCoverPath(String generatedCoverPath) {
        this.generatedCoverPath = generatedCoverPath;
    }

    public Integer getVideoDuration() {
        return videoDuration;
    }

    public void setVideoDuration(Integer videoDuration) {
        this.videoDuration = videoDuration;
    }

    public Boolean getConfirmedToLibrary() {
        return confirmedToLibrary;
    }

    public void setConfirmedToLibrary(Boolean confirmedToLibrary) {
        this.confirmedToLibrary = confirmedToLibrary;
    }

    public Long getBlessVideoId() {
        return blessVideoId;
    }

    public void setBlessVideoId(Long blessVideoId) {
        this.blessVideoId = blessVideoId;
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
}