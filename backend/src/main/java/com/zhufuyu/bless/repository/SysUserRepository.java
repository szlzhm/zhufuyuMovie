package com.zhufuyu.bless.repository;

import com.zhufuyu.bless.entity.SysUserEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface SysUserRepository extends JpaRepository<SysUserEntity, Long>, JpaSpecificationExecutor<SysUserEntity> {

    Optional<SysUserEntity> findByUsername(String username);

    boolean existsByUsername(String username);
}
