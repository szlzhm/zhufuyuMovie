package com.zhufuyu.bless.repository;

import com.zhufuyu.bless.entity.TextMaterialEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TextMaterialRepository extends JpaRepository<TextMaterialEntity, Long>, JpaSpecificationExecutor<TextMaterialEntity> {
    
    boolean existsByName(String name);
}
