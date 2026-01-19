package com.zhufuyu.bless.model.request.chatbot;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ChatbotCreateReq {

    @NotBlank(message = "对话名称不能为空")
    private String conversationName;

    // @NotNull(message = "用户ID不能为空")
    private Long userId;

    public String getConversationName() {
        return conversationName;
    }

    public void setConversationName(String conversationName) {
        this.conversationName = conversationName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}