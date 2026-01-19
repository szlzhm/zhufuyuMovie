package com.zhufuyu.bless.model.response.image;

import java.util.List;

/**
 * 文生图响应
 */
public class TextToImageResponse {
    
    /**
     * 生成的图像URL列表
     */
    private List<String> imageUrls;
    
    /**
     * 任务ID（如果是异步调用）
     */
    private String taskId;
    
    /**
     * 请求是否成功
     */
    private Boolean success;
    
    /**
     * 错误信息
     */
    private String errorMessage;
    
    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}