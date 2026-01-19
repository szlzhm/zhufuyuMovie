package com.zhufuyu.bless.controller.auth;

import com.zhufuyu.bless.model.common.BaseResponse;
import com.zhufuyu.bless.model.request.auth.LoginReq;
import com.zhufuyu.bless.model.response.auth.LoginResultResp;
import com.zhufuyu.bless.model.response.auth.LoginUserResp;
import com.zhufuyu.bless.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login/v1")
    public BaseResponse<LoginResultResp> login(@Valid @RequestBody LoginReq req) {
        return BaseResponse.success(authService.login(req));
    }

    @PostMapping("/get/currentUser/v1")
    public BaseResponse<LoginUserResp> getCurrentUser() {
        return BaseResponse.success(authService.getCurrentUser());
    }
}
