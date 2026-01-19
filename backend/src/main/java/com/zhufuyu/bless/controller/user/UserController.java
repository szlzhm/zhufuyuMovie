package com.zhufuyu.bless.controller.user;

import com.zhufuyu.bless.model.common.BaseResponse;
import com.zhufuyu.bless.model.request.user.UserUpdatePasswordReq;
import com.zhufuyu.bless.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/update/password/v1")
    public BaseResponse<Void> updatePassword(@Valid @RequestBody UserUpdatePasswordReq req) {
        userService.updatePassword(req);
        return BaseResponse.success(null);
    }
}
