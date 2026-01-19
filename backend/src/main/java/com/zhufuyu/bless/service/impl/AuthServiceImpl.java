package com.zhufuyu.bless.service.impl;

import com.zhufuyu.bless.entity.SysUserEntity;
import com.zhufuyu.bless.exception.BizException;
import com.zhufuyu.bless.model.request.auth.LoginReq;
import com.zhufuyu.bless.model.response.auth.LoginResultResp;
import com.zhufuyu.bless.model.response.auth.LoginUserResp;
import com.zhufuyu.bless.repository.SysUserRepository;
import com.zhufuyu.bless.security.JwtUtil;
import com.zhufuyu.bless.security.LoginUserContext;
import com.zhufuyu.bless.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthServiceImpl implements AuthService {

    private final SysUserRepository sysUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(SysUserRepository sysUserRepository,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil) {
        this.sysUserRepository = sysUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public LoginResultResp login(LoginReq request) {
        SysUserEntity user = sysUserRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BizException(10001, "用户名或密码错误"));

        if (user.getStatus() == null || user.getStatus() == 0) {
            throw new BizException(10002, "账号已禁用");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BizException(10001, "用户名或密码错误");
        }

        user.setLastLoginTime(LocalDateTime.now());
        sysUserRepository.save(user);

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        LoginUserResp userResp = new LoginUserResp();
        userResp.setId(user.getId());
        userResp.setUsername(user.getUsername());
        userResp.setRole(user.getRole());
        userResp.setLastLoginTime(user.getLastLoginTime());

        LoginResultResp resultResp = new LoginResultResp();
        resultResp.setToken(token);
        resultResp.setUser(userResp);
        return resultResp;
    }

    @Override
    public LoginUserResp getCurrentUser() {
        LoginUserContext.LoginUserInfo info = LoginUserContext.get();
        if (info == null) {
            throw new BizException(10003, "未登录或登录已失效");
        }
        LoginUserResp resp = new LoginUserResp();
        resp.setId(info.getUserId());
        resp.setUsername(info.getUsername());
        resp.setRole(info.getRole());
        return resp;
    }
}
