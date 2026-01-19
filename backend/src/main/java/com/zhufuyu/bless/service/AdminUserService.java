package com.zhufuyu.bless.service;

import com.zhufuyu.bless.model.common.PageResult;
import com.zhufuyu.bless.model.request.user.AdminCreateUserReq;
import com.zhufuyu.bless.model.request.user.AdminResetPasswordReq;
import com.zhufuyu.bless.model.request.user.AdminUpdateUserStatusReq;
import com.zhufuyu.bless.model.request.user.AdminUserListReq;
import com.zhufuyu.bless.model.response.user.AdminUserListItemResp;

public interface AdminUserService {

    PageResult<AdminUserListItemResp> queryUserList(AdminUserListReq request);

    Long createUser(AdminCreateUserReq request);

    void resetPassword(AdminResetPasswordReq request);

    void updateStatus(AdminUpdateUserStatusReq request);
}
