package com.zhufuyu.bless.repository;

import com.zhufuyu.bless.entity.SysImageCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SysImageCategoryRepository extends JpaRepository<SysImageCategoryEntity, Long>,
        JpaSpecificationExecutor<SysImageCategoryEntity> {
    
    List<SysImageCategoryEntity> findByParentIdIsNull();
    
    List<SysImageCategoryEntity> findByParentId(Long parentId);
    
    boolean existsByCategoryCode(String categoryCode);
}
