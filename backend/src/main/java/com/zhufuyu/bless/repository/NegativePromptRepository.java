package com.zhufuyu.bless.repository;

import com.zhufuyu.bless.entity.NegativePromptEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 负面提示语仓储接口
 */
@Repository
public interface NegativePromptRepository extends JpaRepository<NegativePromptEntity, Long> {

    @Query("SELECT n FROM NegativePromptEntity n WHERE (:content IS NULL OR n.content LIKE %:content%) ORDER BY n.createdTime DESC")
    Page<NegativePromptEntity> search(@Param("content") String content, Pageable pageable);
}
