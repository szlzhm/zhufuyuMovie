package com.zhufuyu.bless.repository;

import com.zhufuyu.bless.entity.SysEmotionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SysEmotionRepository extends JpaRepository<SysEmotionEntity, Long>, 
        JpaSpecificationExecutor<SysEmotionEntity> {
    
    Optional<SysEmotionEntity> findByEmotionCode(String emotionCode);
    
    boolean existsByEmotionCode(String emotionCode);
}
