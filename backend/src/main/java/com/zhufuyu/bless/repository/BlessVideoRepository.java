package com.zhufuyu.bless.repository;

import com.zhufuyu.bless.entity.BlessVideoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BlessVideoRepository extends JpaRepository<BlessVideoEntity, Long> {

    /**
     * 多条件分页查询
     */
    @Query("SELECT v FROM BlessVideoEntity v WHERE " +
           "(:title IS NULL OR v.title LIKE %:title%)")
    Page<BlessVideoEntity> findByConditions(
            @Param("title") String title,
            Pageable pageable);
}
