package com.zhufuyu.bless.model.request.chatbot;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ChatbotDetailCreateReq {

    @NotBlank(message = "对话ID不能为空")
    private String conversationId;

    @NotNull(message = "角色不能为空")
    private Integer role; // 0-用户提问, 1-ChatBot回答

    @NotBlank(message = "内容类型不能为空")
    private String contentType; // text/image/audio/video/file

    private String content;

    private String originalFilename;

    private String relativePath;

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
}