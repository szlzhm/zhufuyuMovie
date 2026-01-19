package com.zhufuyu.bless.model.response.image;

import lombok.Data;

/**
 * 异步文生图响应
 */
@Data
public class AsyncTextToImageResponse {
    
    /**
     * 任务ID
     */
    private String taskId;
    
    /**
     * 任务状态 (SUBMITTED, PROCESSING, SUCCESS, FAILED)
     */
    private String status;
    
    /**
     * 任务进度 (0-100)
     */
    private Integer progress;
    
    /**
     * 生成的图像URL列表
     */
    private String[] imageUrls;
    
    /**
     * 错误信息
     */
    private String errorMessage;
    
    /**
     * 创建时间
     */
    private Long createTime;
    
    /**
     * 完成时间
     */
    private Long completeTime;

    // Getters and Setters
    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
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

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public String[] getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(String[] imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(Long completeTime) {
        this.completeTime = completeTime;
    }
}