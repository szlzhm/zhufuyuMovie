package com.zhufuyu.bless.model.response.config;

import java.time.LocalDateTime;

public class EmotionListItemResp {

    private Long id;
    
    private String emotionCode;
    
    private String emotionName;
    
    private String usageDesc;
    
    private Integer status;
    
    private LocalDateTime createdTime;
    
    private LocalDateTime updatedTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmotionCode() {
        return emotionCode;
    }

    public void setEmotionCode(String emotionCode) {
        this.emotionCode = emotionCode;
    }

    public String getEmotionName() {
        return emotionName;
    }

    public void setEmotionName(String emotionName) {
        this.emotionName = emotionName;
    }

    public String getUsageDesc() {
        return usageDesc;
    }

    public void setUsageDesc(String usageDesc) {
        this.usageDesc = usageDesc;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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
}
