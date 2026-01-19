package com.zhufuyu.bless.model.request.task;

public class VideoTaskCreateReq {
    private String taskName;            // 任务名称
    private String batchName;           // 任务批次
    private String videoTitle;          // 视频标题
    private String taskType;            // 任务类型
    private Long backgroundImageId;     // 背景图片ID
    private Long backgroundMusicId;     // 背景音乐ID(可选)
    private String voiceAudioPath;      // 祝福语音频路径
    private String textMaterialName;    // 文案名称(可选)
    
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

    public Long getBackgroundImageId() {
        return backgroundImageId;
    }

    public void setBackgroundImageId(Long backgroundImageId) {
        this.backgroundImageId = backgroundImageId;
    }

    public Long getBackgroundMusicId() {
        return backgroundMusicId;
    }

    public void setBackgroundMusicId(Long backgroundMusicId) {
        this.backgroundMusicId = backgroundMusicId;
    }

    public String getVoiceAudioPath() {
        return voiceAudioPath;
    }

    public void setVoiceAudioPath(String voiceAudioPath) {
        this.voiceAudioPath = voiceAudioPath;
    }

    public String getTextMaterialName() {
        return textMaterialName;
    }

    public void setTextMaterialName(String textMaterialName) {
        this.textMaterialName = textMaterialName;
    }
}
