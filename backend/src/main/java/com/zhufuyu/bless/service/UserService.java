package com.zhufuyu.bless.service;

import com.zhufuyu.bless.model.request.user.UserUpdatePasswordReq;

public interface UserService {

    void updatePassword(UserUpdatePasswordReq request);
}
