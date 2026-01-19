package com.zhufuyu.bless.service;

import com.zhufuyu.bless.model.request.auth.LoginReq;
import com.zhufuyu.bless.model.response.auth.LoginResultResp;
import com.zhufuyu.bless.model.response.auth.LoginUserResp;

public interface AuthService {

    LoginResultResp login(LoginReq request);

    LoginUserResp getCurrentUser();
}
