package com.zhufuyu.bless.repository;

import com.zhufuyu.bless.entity.VoiceMaterialEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VoiceMaterialRepository extends JpaRepository<VoiceMaterialEntity, Long> {

    /**
     * 按名称查找
     */
    VoiceMaterialEntity findByName(String name);

    /**
     * 多条件分页查询
     */
    @Query("SELECT v FROM VoiceMaterialEntity v WHERE " +
           "(:name IS NULL OR v.name LIKE %:name%) AND " +
           "(:gender IS NULL OR v.gender = :gender) AND " +
           "(:language IS NULL OR v.language = :language)")
    Page<VoiceMaterialEntity> findByConditions(
            @Param("name") String name,
            @Param("gender") String gender,
            @Param("language") String language,
            Pageable pageable);
}
