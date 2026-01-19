package com.zhufuyu.bless.repository;

import com.zhufuyu.bless.entity.SysConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SysConfigRepository extends JpaRepository<SysConfigEntity, Long> {
    
    Optional<SysConfigEntity> findByConfigKey(String configKey);
}
