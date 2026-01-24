package com.zhufuyu.bless.repository;

import com.zhufuyu.bless.entity.ImageGenerationTaskEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ImageGenerationTaskRepository extends JpaRepository<ImageGenerationTaskEntity, Long> {

    Optional<ImageGenerationTaskEntity> findFirstByTaskStatusOrderByCreatedTimeAsc(String taskStatus);

    @Query("SELECT t FROM ImageGenerationTaskEntity t JOIN ImagePromptEntity p ON t.promptId = p.id " +
           "WHERE (?1 IS NULL OR t.taskStatus = ?1) " +
           "AND (?2 IS NULL OR p.promptContent LIKE %?2%) " +
           "AND (?3 IS NULL OR t.createdTime >= ?3) " +
           "AND (?4 IS NULL OR t.createdTime <= ?4)")
    Page<ImageGenerationTaskEntity> searchTasks(String status, String prompt, LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);

    @Query("SELECT t FROM ImageGenerationTaskEntity t JOIN ImagePromptEntity p ON t.promptId = p.id " +
           "WHERE (?1 IS NULL OR t.taskStatus = ?1) " +
           "AND (?2 IS NULL OR p.promptContent LIKE %?2%) " +
           "AND (?3 IS NULL OR t.statusChangedTime >= ?3) " +
           "AND (?4 IS NULL OR t.statusChangedTime <= ?4)")
    Page<ImageGenerationTaskEntity> searchTasksByStatusUpdateTime(String status, String prompt, LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
}
