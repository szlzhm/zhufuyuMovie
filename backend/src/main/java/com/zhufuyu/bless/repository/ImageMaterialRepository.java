package com.zhufuyu.bless.repository;

import com.zhufuyu.bless.entity.ImageMaterialEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * 图片素材Repository
 */
@Repository
public interface ImageMaterialRepository extends JpaRepository<ImageMaterialEntity, Long>, 
        JpaSpecificationExecutor<ImageMaterialEntity> {
    
    boolean existsByTitle(String title);
}
