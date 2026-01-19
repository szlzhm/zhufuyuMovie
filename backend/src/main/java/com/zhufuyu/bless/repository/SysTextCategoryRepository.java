package com.zhufuyu.bless.repository;

import com.zhufuyu.bless.entity.SysTextCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SysTextCategoryRepository extends JpaRepository<SysTextCategoryEntity, Long>,
        JpaSpecificationExecutor<SysTextCategoryEntity> {
    
    List<SysTextCategoryEntity> findByParentIdIsNull();
    
    List<SysTextCategoryEntity> findByParentId(Long parentId);
    
    boolean existsByCategoryCode(String categoryCode);
}
