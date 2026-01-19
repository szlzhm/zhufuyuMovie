package com.zhufuyu.bless.controller.user;

import com.zhufuyu.bless.model.common.BaseResponse;
import com.zhufuyu.bless.model.common.PageResult;
import com.zhufuyu.bless.model.request.user.AdminCreateUserReq;
import com.zhufuyu.bless.model.request.user.AdminResetPasswordReq;
import com.zhufuyu.bless.model.request.user.AdminUpdateUserStatusReq;
import com.zhufuyu.bless.model.request.user.AdminUserListReq;
import com.zhufuyu.bless.model.response.common.IdResp;
import com.zhufuyu.bless.model.response.user.AdminUserListItemResp;
import com.zhufuyu.bless.service.AdminUserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/user")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @PostMapping("/list/query/v1")
    public BaseResponse<PageResult<AdminUserListItemResp>> queryUserList(@Valid @RequestBody AdminUserListReq req) {
        return BaseResponse.success(adminUserService.queryUserList(req));
    }

    @PostMapping("/create/v1")
    public BaseResponse<IdResp> createUser(@Valid @RequestBody AdminCreateUserReq req) {
        Long id = adminUserService.createUser(req);
        return BaseResponse.success(new IdResp(id));
    }

    @PostMapping("/reset/password/v1")
    public BaseResponse<Void> resetPassword(@Valid @RequestBody AdminResetPasswordReq req) {
        adminUserService.resetPassword(req);
        return BaseResponse.success(null);
    }

    @PostMapping("/toggle/status/v1")
    public BaseResponse<Void> updateStatus(@Valid @RequestBody AdminUpdateUserStatusReq req) {
        adminUserService.updateStatus(req);
        return BaseResponse.success(null);
    }
}
