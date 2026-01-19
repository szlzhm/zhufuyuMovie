package com.zhufuyu.bless.repository;

import com.zhufuyu.bless.entity.ChatbotConversationMetaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatbotConversationMetaRepository extends JpaRepository<ChatbotConversationMetaEntity, Long> {

    Optional<ChatbotConversationMetaEntity> findByConversationId(String conversationId);

    Optional<ChatbotConversationMetaEntity> findByConversationIdAndUserId(String conversationId, Long userId);

    List<ChatbotConversationMetaEntity> findByUserIdOrderByCreatedTimeDesc(Long userId);

    Page<ChatbotConversationMetaEntity> findByUserIdOrderByCreatedTimeDesc(Long userId, Pageable pageable);

    void deleteByUserId(Long userId);

    @Query("SELECT c FROM ChatbotConversationMetaEntity c WHERE c.userId = :userId ORDER BY c.createdTime DESC")
    List<ChatbotConversationMetaEntity> findRecentConversations(@Param("userId") Long userId);
}