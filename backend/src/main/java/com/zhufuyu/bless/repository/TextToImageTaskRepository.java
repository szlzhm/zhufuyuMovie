package com.zhufuyu.bless.repository;

import com.zhufuyu.bless.entity.TextToImageTaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 文生图任务仓库
 */
@Repository
public interface TextToImageTaskRepository extends JpaRepository<TextToImageTaskEntity, Long> {
    
    /**
     * 根据任务ID查找任务
     */
    Optional<TextToImageTaskEntity> findByTaskId(String taskId);
}