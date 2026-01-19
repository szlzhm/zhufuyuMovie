package com.zhufuyu.bless.repository;

import com.zhufuyu.bless.entity.VideoTaskEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoTaskRepository extends JpaRepository<VideoTaskEntity, Long> {

    /**
     * 多条件分页查询
     */
    @Query("SELECT t FROM VideoTaskEntity t WHERE " +
           "(:taskName IS NULL OR t.taskName LIKE %:taskName%) AND " +
           "(:batchName IS NULL OR t.batchName LIKE %:batchName%) AND " +
           "(:textMaterialName IS NULL OR t.textMaterialName LIKE %:textMaterialName%)")
    Page<VideoTaskEntity> findByConditions(
            @Param("taskName") String taskName,
            @Param("batchName") String batchName,
            @Param("textMaterialName") String textMaterialName,
            Pageable pageable);
}
