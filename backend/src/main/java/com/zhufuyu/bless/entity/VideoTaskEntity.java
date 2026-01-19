package com.zhufuyu.bless.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 视频创作任务实体
 */
@Entity
@Table(name = "video_task")
public class VideoTaskEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "task_name", nullable = false, length = 200)
    private String taskName;  // 任务名称

    @Column(name = "batch_name", nullable = false, length = 200)
    private String batchName;  // 任务批次

    @Column(name = "video_title", nullable = false, length = 200)
    private String videoTitle;  // 生成的视频标题

    @Column(name = "task_type", nullable = false, length = 20)
    private String taskType;  // 任务类型: AUDIO_TO_VIDEO

    @Column(name = "background_image_id")
    private Long backgroundImageId;  // 背景图片ID

    @Column(name = "background_music_id")
    private Long backgroundMusicId;  // 背景音乐ID(可选)

    @Column(name = "voice_audio_path", length = 500)
    private String voiceAudioPath;  // 祝福语音频路径

    @Column(name = "text_material_name", length = 200)
    private String textMaterialName;  // 文案名称(可选)

    @Column(name = "task_status", nullable = false, length = 20)
    private String taskStatus = "PENDING";  // 任务状态: PENDING/PROCESSING/SUCCESS/FAILED

    @Column(name = "error_message", length = 1000)
    private String errorMessage;  // 失败原因

    @Column(name = "generated_video_path", length = 500)
    private String generatedVideoPath;  // 生成的视频路径

    @Column(name = "generated_cover_path", length = 500)
    private String generatedCoverPath;  // 生成的封面路径

    @Column(name = "video_duration")
    private Integer videoDuration;  // 视频时长(秒)

    @Column(name = "confirmed_to_library")
    private Boolean confirmedToLibrary = false;  // 是否已确认入库

    @Column(name = "bless_video_id")
    private Long blessVideoId;  // 入库后的视频ID

    @Column(name = "created_time", nullable = false, updatable = false)
    private LocalDateTime createdTime;

    @Column(name = "generated_time")
    private LocalDateTime generatedTime;  // 任务执行/生成时间

    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    @PrePersist
    protected void onCreate() {
        createdTime = LocalDateTime.now();
        updatedTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedTime = LocalDateTime.now();
    }
    
    // Getter and Setter methods
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

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
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

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public LocalDateTime getGeneratedTime() {
        return generatedTime;
    }

    public void setGeneratedTime(LocalDateTime generatedTime) {
        this.generatedTime = generatedTime;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }
}
