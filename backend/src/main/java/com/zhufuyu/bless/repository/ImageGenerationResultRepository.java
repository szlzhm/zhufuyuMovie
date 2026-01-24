package com.zhufuyu.bless.repository;

import com.zhufuyu.bless.entity.ImageGenerationResultEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ImageGenerationResultRepository extends JpaRepository<ImageGenerationResultEntity, Long> {

    @Query("SELECT r FROM ImageGenerationResultEntity r JOIN ImagePromptEntity p ON r.promptId = p.id " +
           "WHERE (?1 IS NULL OR p.promptContent LIKE %?1%) " +
           "AND (?2 IS NULL OR p.createdTime >= ?2) " +
           "AND (?3 IS NULL OR p.createdTime <= ?3) " +
           "AND (?4 IS NULL OR r.completedTime >= ?4) " +
           "AND (?5 IS NULL OR r.completedTime <= ?5)")
    Page<ImageGenerationResultEntity> searchResults(String prompt, LocalDateTime pStartTime, LocalDateTime pEndTime, LocalDateTime cStartTime, LocalDateTime cEndTime, Pageable pageable);

    java.util.List<ImageGenerationResultEntity> findByTaskId(Long taskId);
}
