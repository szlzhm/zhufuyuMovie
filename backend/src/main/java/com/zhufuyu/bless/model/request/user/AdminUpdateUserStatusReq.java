package com.zhufuyu.bless.model.request.user;

import jakarta.validation.constraints.NotNull;

public class AdminUpdateUserStatusReq {

    @NotNull
    private Long userId;

    @NotNull
    private Integer status;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
