package com.zhufuyu.bless.model.response.auth;

public class LoginResultResp {

    private String token;
    private LoginUserResp user;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LoginUserResp getUser() {
        return user;
    }

    public void setUser(LoginUserResp user) {
        this.user = user;
    }
}
