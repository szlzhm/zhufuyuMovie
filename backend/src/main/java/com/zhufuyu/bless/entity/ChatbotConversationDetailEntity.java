package com.zhufuyu.bless.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chatbot_conversation_detail")
public class ChatbotConversationDetailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "conversation_id", nullable = false, length = 64)
    private String conversationId;

    @Column(name = "detail_id", nullable = false, unique = true, length = 64)
    private String detailId;

    @Column(name = "role", nullable = false)
    private Integer role; // 0-用户提问, 1-ChatBot回答

    @Column(name = "content_type", nullable = false, length = 20)
    private String contentType; // text/image/audio/video/file

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "original_filename", length = 255)
    private String originalFilename;

    @Column(name = "relative_path", length = 500)
    private String relativePath;

    @Column(name = "occurred_time", nullable = false)
    private LocalDateTime occurredTime;

    @Column(name = "created_time", nullable = false, updatable = false)
    private LocalDateTime createdTime;

    @PrePersist
    protected void onCreate() {
        createdTime = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getDetailId() {
        return detailId;
    }

    public void setDetailId(String detailId) {
        this.detailId = detailId;
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

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }
}