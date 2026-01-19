package com.zhufuyu.bless.model.request.chatbot;

import jakarta.validation.constraints.NotNull;

public class ChatbotListReq {

    //@NotNull(message = "用户ID不能为空")
    private Long userId;

    private Integer pageNo = 1;

    private Integer pageSize = 20;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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