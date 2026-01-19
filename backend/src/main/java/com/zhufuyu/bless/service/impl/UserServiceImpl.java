package com.zhufuyu.bless.service.impl;

import com.zhufuyu.bless.entity.SysUserEntity;
import com.zhufuyu.bless.exception.BizException;
import com.zhufuyu.bless.model.request.user.UserUpdatePasswordReq;
import com.zhufuyu.bless.repository.SysUserRepository;
import com.zhufuyu.bless.security.LoginUserContext;
import com.zhufuyu.bless.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {

    private final SysUserRepository sysUserRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(SysUserRepository sysUserRepository,
                           PasswordEncoder passwordEncoder) {
        this.sysUserRepository = sysUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void updatePassword(UserUpdatePasswordReq request) {
        LoginUserContext.LoginUserInfo info = LoginUserContext.get();
        if (info == null) {
            throw new BizException(10003, "未登录或登录已失效");
        }

        if (!Objects.equals(request.getNewPassword(), request.getConfirmPassword())) {
            throw new BizException(10006, "新密码与确认密码不一致");
        }

        if (request.getNewPassword().length() < 6) {
            throw new BizException(10007, "新密码过于简单");
        }

        SysUserEntity entity = sysUserRepository.findById(info.getUserId())
                .orElseThrow(() -> new BizException(10009, "用户不存在"));

        if (!passwordEncoder.matches(request.getOldPassword(), entity.getPasswordHash())) {
            throw new BizException(10005, "旧密码错误");
        }

        entity.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        sysUserRepository.save(entity);
    }
}
