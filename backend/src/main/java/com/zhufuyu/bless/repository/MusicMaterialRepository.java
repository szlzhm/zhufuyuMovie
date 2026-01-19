package com.zhufuyu.bless.repository;

import com.zhufuyu.bless.entity.MusicMaterialEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MusicMaterialRepository extends JpaRepository<MusicMaterialEntity, Long> {

    @Query("SELECT m FROM MusicMaterialEntity m WHERE " +
           "(:name IS NULL OR m.name LIKE %:name%) AND " +
           "(:emotion IS NULL OR m.emotion = :emotion)")
    Page<MusicMaterialEntity> findByConditions(
            @Param("name") String name,
            @Param("emotion") Long emotion,
            Pageable pageable);
}
