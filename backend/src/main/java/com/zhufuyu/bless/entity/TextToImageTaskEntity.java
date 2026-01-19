package com.zhufuyu.bless.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文生图任务实体
 */
@Entity
@Table(name = "text_to_image_task")
@Data
public class TextToImageTaskEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 任务ID (外部可见的唯一标识)
     */
    @Column(name = "task_id", unique = true, nullable = false)
    private String taskId;
    
    /**
     * 用户ID
     */
    @Column(name = "user_id")
    private Long userId;
    
    /**
     * 提示词
     */
    @Column(name = "prompt", columnDefinition = "TEXT")
    private String prompt;
    
    /**
     * 模型名称
     */
    @Column(name = "model_name")
    private String modelName;
    
    /**
     * 反向提示词
     */
    @Column(name = "negative_prompt", columnDefinition = "TEXT")
    private String negativePrompt;
    
    /**
     * 图像数量
     */
    @Column(name = "image_count")
    private Integer imageCount;
    
    /**
     * 图像尺寸
     */
    @Column(name = "image_size")
    private String imageSize;
    
    /**
     * 随机种子
     */
    @Column(name = "seed")
    private Integer seed;
    
    /**
     * 任务状态 (SUBMITTED, PROCESSING, SUCCESS, FAILED)
     */
    @Column(name = "status")
    private String status;
    
    /**
     * 任务进度 (0-100)
     */
    @Column(name = "progress")
    private Integer progress;
    
    /**
     * 生成的图像URL (JSON格式存储)
     */
    @Column(name = "image_urls", columnDefinition = "TEXT")
    private String imageUrls;
    
    /**
     * 错误信息
     */
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
    
    /**
     * 创建时间
     */
    @Column(name = "created_time")
    private LocalDateTime createdTime;
    
    /**
     * 更新时间
     */
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;
    
    /**
     * 完成时间
     */
    @Column(name = "completed_time")
    private LocalDateTime completedTime;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getNegativePrompt() {
        return negativePrompt;
    }

    public void setNegativePrompt(String negativePrompt) {
        this.negativePrompt = negativePrompt;
    }

    public Integer getImageCount() {
        return imageCount;
    }

    public void setImageCount(Integer imageCount) {
        this.imageCount = imageCount;
    }

    public String getImageSize() {
        return imageSize;
    }

    public void setImageSize(String imageSize) {
        this.imageSize = imageSize;
    }

    public Integer getSeed() {
        return seed;
    }

    public void setSeed(Integer seed) {
        this.seed = seed;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(String imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }

    public LocalDateTime getCompletedTime() {
        return completedTime;
    }

    public void setCompletedTime(LocalDateTime completedTime) {
        this.completedTime = completedTime;
    }
}