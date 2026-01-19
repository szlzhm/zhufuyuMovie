package com.zhufuyu.bless.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 祝福语视频实体
 */
@Data
@Entity
@Table(name = "bless_video")
public class BlessVideoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 200)
    private String title;  // 视频标题

    @Column(name = "video_path", nullable = false, length = 500)
    private String videoPath;  // 视频地址(相对路径)

    @Column(name = "cover_path", length = 500)
    private String coverPath;  // 视频封面(首个关键帧截图)

    @Column(name = "text_material_name", length = 200)
    private String textMaterialName;  // 祝福语文案名称(可为空)

    @Column(name = "duration")
    private Integer duration;  // 播放时长(秒)

    @Column(name = "created_time", nullable = false, updatable = false)
    private LocalDateTime createdTime;  // 创建时间(入库时间)

    @Column(name = "generated_time")
    private LocalDateTime generatedTime;  // 生成时间(合成任务提交时间)

    @Column(name = "publish_status", nullable = false)
    private Integer publishStatus = 0;  // 发布状态: 0-未发布, 1-已发布

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

    public Integer getPublishStatus() {
        return publishStatus;
    }

    public void setPublishStatus(int publishStatus) {
        this.publishStatus = publishStatus;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }
}