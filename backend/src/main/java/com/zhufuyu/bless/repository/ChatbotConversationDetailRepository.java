package com.zhufuyu.bless.repository;

import com.zhufuyu.bless.entity.ChatbotConversationDetailEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChatbotConversationDetailRepository extends JpaRepository<ChatbotConversationDetailEntity, Long> {

    List<ChatbotConversationDetailEntity> findByConversationIdOrderByOccurredTimeAsc(String conversationId);

    Page<ChatbotConversationDetailEntity> findByConversationIdOrderByOccurredTimeAsc(String conversationId, Pageable pageable);

    void deleteByConversationId(String conversationId);

    void deleteByConversationIdIn(List<String> conversationIds);

    @Query("SELECT c FROM ChatbotConversationDetailEntity c WHERE c.conversationId = :conversationId AND c.occurredTime >= :startTime ORDER BY c.occurredTime ASC")
    List<ChatbotConversationDetailEntity> findRecentDetailsByConversationId(@Param("conversationId") String conversationId, 
                                                                          @Param("startTime") LocalDateTime startTime);

    @Query("SELECT c FROM ChatbotConversationDetailEntity c WHERE c.conversationId = :conversationId ORDER BY c.occurredTime DESC")
    List<ChatbotConversationDetailEntity> findRecentDetails(@Param("conversationId") String conversationId, Pageable pageable);
}