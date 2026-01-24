package com.zhufuyu.bless.repository;

import com.zhufuyu.bless.entity.ImagePromptTemplateEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImagePromptTemplateRepository extends JpaRepository<ImagePromptTemplateEntity, Long> {
    
    Page<ImagePromptTemplateEntity> findByIsDeletedAndTemplateContentContaining(Integer isDeleted, String content, Pageable pageable);
    
    Page<ImagePromptTemplateEntity> findByIsDeleted(Integer isDeleted, Pageable pageable);

    @Modifying
    @Query("update ImagePromptTemplateEntity t set t.isDeleted = 1, t.deletedTime = CURRENT_TIMESTAMP where t.id = ?1")
    void softDeleteById(Long id);
}
