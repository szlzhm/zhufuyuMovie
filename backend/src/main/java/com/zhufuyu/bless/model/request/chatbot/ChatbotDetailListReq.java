package com.zhufuyu.bless.model.request.chatbot;

import jakarta.validation.constraints.NotBlank;

public class ChatbotDetailListReq {

    @NotBlank(message = "对话ID不能为空")
    private String conversationId;

    private Integer pageNo = 1;

    private Integer pageSize = 100; // 默认获取最近100条作为上下文

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}