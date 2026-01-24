package com.zhufuyu.bless.service.impl;

import com.zhufuyu.bless.entity.SysUserEntity;
import com.zhufuyu.bless.exception.BizException;
import com.zhufuyu.bless.model.common.PageResult;
import com.zhufuyu.bless.model.request.user.AdminCreateUserReq;
import com.zhufuyu.bless.model.request.user.AdminResetPasswordReq;
import com.zhufuyu.bless.model.request.user.AdminUpdateUserStatusReq;
import com.zhufuyu.bless.model.request.user.AdminUserListReq;
import com.zhufuyu.bless.model.response.user.AdminUserListItemResp;
import com.zhufuyu.bless.repository.SysUserRepository;
import com.zhufuyu.bless.security.LoginUserContext;
import com.zhufuyu.bless.service.AdminUserService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdminUserServiceImpl implements AdminUserService {

    private final SysUserRepository sysUserRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserServiceImpl(SysUserRepository sysUserRepository,
                                PasswordEncoder passwordEncoder) {
        this.sysUserRepository = sysUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private void checkAdmin() {
        LoginUserContext.LoginUserInfo info = LoginUserContext.get();
        if (info == null || !"ADMIN".equals(info.getRole())) {
            throw new BizException(10004, "无权限访问");
        }
    }

    @Override
    public PageResult<AdminUserListItemResp> queryUserList(AdminUserListReq request) {
        checkAdmin();

        int pageNo = request.getPageNo() != null ? request.getPageNo() : 1;
        int pageSize = request.getPageSize() != null ? request.getPageSize() : 10;

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize,
                Sort.by(Sort.Direction.DESC, "createdTime"));

        Specification<SysUserEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(request.getUsername())) {
                predicates.add(cb.like(root.get("username"), "%" + request.getUsername() + "%"));
            }
            if (StringUtils.hasText(request.getRole())) {
                predicates.add(cb.equal(root.get("role"), request.getRole()));
            }
            if (request.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), request.getStatus()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<SysUserEntity> page = sysUserRepository.findAll(spec, pageable);

        List<AdminUserListItemResp> items = page.getContent().stream().map(entity -> {
            AdminUserListItemResp vo = new AdminUserListItemResp();
            vo.setId(entity.getId());
            vo.setUsername(entity.getUsername());
            vo.setRole(entity.getRole());
            vo.setStatus(entity.getStatus());
            vo.setCreatedTime(entity.getCreatedTime());
            vo.setLastLoginTime(entity.getLastLoginTime());
            return vo;
        }).toList();

        PageResult<AdminUserListItemResp> result = new PageResult<>();
        result.setPageNo(pageNo);
        result.setPageSize(pageSize);
        result.setTotal(page.getTotalElements());
        result.setList(items);
        return result;
    }

    @Override
    public Long createUser(AdminCreateUserReq request) {
        checkAdmin();

        if (sysUserRepository.existsByUsername(request.getUsername())) {
            throw new BizException(10008, "用户名已存在");
        }

        SysUserEntity entity = new SysUserEntity();
        entity.setUsername(request.getUsername());
        entity.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        entity.setRole(request.getRole());
        entity.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        SysUserEntity saved = sysUserRepository.save(entity);
        return saved.getId();
    }

    @Override
    public void resetPassword(AdminResetPasswordReq request) {
        checkAdmin();

        SysUserEntity entity = sysUserRepository.findById(request.getUserId())
                .orElseThrow(() -> new BizException(10009, "用户不存在"));

        entity.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        sysUserRepository.save(entity);
    }

    @Override
    public void updateStatus(AdminUpdateUserStatusReq request) {
        checkAdmin();

        SysUserEntity entity = sysUserRepository.findById(request.getUserId())
                .orElseThrow(() -> new BizException(10009, "用户不存在"));
        
        // 不允许禁用admin用户，也不允许禁用任何管理员
        if ("admin".equals(entity.getUsername()) || "ADMIN".equals(entity.getRole())) {
            throw new BizException(10010, "不能禁用管理员用户");
        }
        
        entity.setStatus(request.getStatus());
        sysUserRepository.save(entity);
    }
}
