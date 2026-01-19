package com.zhufuyu.bless.model.response.chatbot;

import java.time.LocalDateTime;

public class ChatbotDetailItemResp {

    private Long id;
    private String detailId;
    private String conversationId;
    private Integer role; // 0-用户提问, 1-ChatBot回答
    private String contentType; // text/image/audio/video/file
    private String content;
    private String originalFilename;
    private String relativePath;
    private LocalDateTime occurredTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDetailId() {
        return detailId;
    }

    public void setDetailId(String detailId) {
        this.detailId = detailId;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }

    public LocalDateTime getOccurredTime() {
        return occurredTime;
    }

    public void setOccurredTime(LocalDateTime occurredTime) {
        this.occurredTime = occurredTime;
    }
}