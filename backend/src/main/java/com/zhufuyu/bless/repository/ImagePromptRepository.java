package com.zhufuyu.bless.repository;

import com.zhufuyu.bless.entity.ImagePromptEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImagePromptRepository extends JpaRepository<ImagePromptEntity, Long> {
}
