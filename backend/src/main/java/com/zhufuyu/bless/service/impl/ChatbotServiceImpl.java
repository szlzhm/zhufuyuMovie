package com.zhufuyu.bless.service.impl;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.RandomBasedGenerator;
import com.zhufuyu.bless.entity.ChatbotConversationDetailEntity;
import com.zhufuyu.bless.entity.ChatbotConversationMetaEntity;
import com.zhufuyu.bless.exception.BizException;
import com.zhufuyu.bless.model.common.PageResult;
import com.zhufuyu.bless.model.request.chatbot.ChatbotCreateReq;
import com.zhufuyu.bless.model.request.chatbot.ChatbotDetailCreateReq;
import com.zhufuyu.bless.model.request.chatbot.ChatbotDetailListReq;
import com.zhufuyu.bless.model.request.chatbot.ChatbotListReq;
import com.zhufuyu.bless.model.response.chatbot.ChatbotDetailItemResp;
import com.zhufuyu.bless.model.response.chatbot.ChatbotMetaItemResp;
import com.zhufuyu.bless.repository.ChatbotConversationDetailRepository;
import com.zhufuyu.bless.repository.ChatbotConversationMetaRepository;
import com.zhufuyu.bless.service.ChatbotService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ChatbotServiceImpl implements ChatbotService {

    private final RandomBasedGenerator uuidGenerator = Generators.randomBasedGenerator();
    private final ChatbotConversationMetaRepository metaRepository;
    private final ChatbotConversationDetailRepository detailRepository;

    public ChatbotServiceImpl(ChatbotConversationMetaRepository metaRepository,
                              ChatbotConversationDetailRepository detailRepository) {
        this.metaRepository = metaRepository;
        this.detailRepository = detailRepository;
    }

    @Override
    @Transactional
    public String createConversation(ChatbotCreateReq request) {
        // 生成UUID作为对话ID
        String conversationId = uuidGenerator.generate().toString().replace("-", "");
        
        ChatbotConversationMetaEntity meta = new ChatbotConversationMetaEntity();
        meta.setConversationId(conversationId);
        meta.setUserId(request.getUserId());
        meta.setConversationName(request.getConversationName());
        
        metaRepository.save(meta);
        return conversationId;
    }

    @Override
    @Transactional
    public String addConversationDetail(ChatbotDetailCreateReq request, Long userId) {
        // 验证对话是否属于当前用户
        metaRepository.findByConversationIdAndUserId(request.getConversationId(), userId)
            .orElseThrow(() -> new BizException(403, "无权限访问该对话"));
        
        // 生成UUID作为详情ID
        String detailId = uuidGenerator.generate().toString().replace("-", "");
        
        ChatbotConversationDetailEntity detail = new ChatbotConversationDetailEntity();
        detail.setDetailId(detailId);
        detail.setConversationId(request.getConversationId());
        detail.setRole(request.getRole());
        detail.setContentType(request.getContentType());
        detail.setContent(request.getContent());
        detail.setOriginalFilename(request.getOriginalFilename());
        detail.setRelativePath(request.getRelativePath());
        detail.setOccurredTime(LocalDateTime.now());
        
        detailRepository.save(detail);
        return detailId;
    }

    @Override
    public PageResult<ChatbotMetaItemResp> getConversationList(ChatbotListReq request) {
        Pageable pageable = PageRequest.of(
            request.getPageNo() - 1, 
            request.getPageSize(),
            Sort.by(Sort.Direction.DESC, "createdTime")
        );

        Page<ChatbotConversationMetaEntity> page = metaRepository
            .findByUserIdOrderByCreatedTimeDesc(request.getUserId(), pageable);

        List<ChatbotMetaItemResp> items = page.getContent().stream().map(this::convertToMetaResp).collect(Collectors.toList());

        PageResult<ChatbotMetaItemResp> result = new PageResult<>();
        result.setPageNo(request.getPageNo());
        result.setPageSize(request.getPageSize());
        result.setTotal(page.getTotalElements());
        result.setList(items);
        return result;
    }

    @Override
    public List<ChatbotDetailItemResp> getConversationDetails(ChatbotDetailListReq request, Long userId) {
        // 验证对话是否属于当前用户
        metaRepository.findByConversationIdAndUserId(request.getConversationId(), userId)
            .orElseThrow(() -> new BizException(403, "无权限访问该对话"));
            
        Pageable pageable = PageRequest.of(
            request.getPageNo() - 1, 
            request.getPageSize(),
            Sort.by(Sort.Direction.ASC, "occurredTime")
        );

        Page<ChatbotConversationDetailEntity> page = 
            detailRepository.findByConversationIdOrderByOccurredTimeAsc(request.getConversationId(), pageable);
        List<ChatbotConversationDetailEntity> entities = page.getContent();

        return entities.stream().map(this::convertToDetailResp).collect(Collectors.toList());
    }

    @Override
    public List<ChatbotDetailItemResp> getRecentConversationDetails(String conversationId, int limit, Long userId) {
        // 验证对话是否属于当前用户
        metaRepository.findByConversationIdAndUserId(conversationId, userId)
            .orElseThrow(() -> new BizException(403, "无权限访问该对话"));
            
        // 从数据库获取最近的对话详情，按时间倒序排列（最新的在前）
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "occurredTime"));
        List<ChatbotConversationDetailEntity> entities = 
            detailRepository.findRecentDetails(conversationId, pageable);

        // 反转列表，使结果按时间正序排列（最老的在前，最新的在后）
        Collections.reverse(entities);
        
        return entities.stream()
            .map(this::convertToDetailResp)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void switchConversation(String conversationId, Long userId) {
        // 验证对话是否属于当前用户
        metaRepository.findByConversationIdAndUserId(conversationId, userId)
            .orElseThrow(() -> new BizException(403, "无权限访问该对话"));
    }

    @Override
    @Transactional
    public void deleteConversation(String conversationId, Long userId) {
        // 验证对话是否属于当前用户
        ChatbotConversationMetaEntity meta = metaRepository.findByConversationIdAndUserId(conversationId, userId)
            .orElseThrow(() -> new BizException(403, "无权限访问该对话"));
        
        // 先删除详情记录
        detailRepository.deleteByConversationId(conversationId);
        // 再删除meta记录
        metaRepository.delete(meta);
    }

    @Override
    public ChatbotMetaItemResp getConversationMeta(String conversationId, Long userId) {
        ChatbotConversationMetaEntity meta = metaRepository.findByConversationIdAndUserId(conversationId, userId)
            .orElseThrow(() -> new BizException(403, "无权限访问该对话"));
        
        return convertToMetaResp(meta);
    }

    private ChatbotMetaItemResp convertToMetaResp(ChatbotConversationMetaEntity entity) {
        ChatbotMetaItemResp resp = new ChatbotMetaItemResp();
        resp.setId(entity.getId());
        resp.setConversationId(entity.getConversationId());
        resp.setConversationName(entity.getConversationName());
        resp.setCreatedTime(entity.getCreatedTime());
        return resp;
    }

    private ChatbotDetailItemResp convertToDetailResp(ChatbotConversationDetailEntity entity) {
        ChatbotDetailItemResp resp = new ChatbotDetailItemResp();
        resp.setId(entity.getId());
        resp.setDetailId(entity.getDetailId());
        resp.setConversationId(entity.getConversationId());
        resp.setRole(entity.getRole());
        resp.setContentType(entity.getContentType());
        resp.setContent(entity.getContent());
        resp.setOriginalFilename(entity.getOriginalFilename());
        resp.setRelativePath(entity.getRelativePath());
        resp.setOccurredTime(entity.getOccurredTime());
        return resp;
    }
}