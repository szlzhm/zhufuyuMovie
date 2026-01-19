package com.zhufuyu.bless.service;

import com.zhufuyu.bless.model.common.PageResult;
import com.zhufuyu.bless.model.request.chatbot.ChatbotCreateReq;
import com.zhufuyu.bless.model.request.chatbot.ChatbotDetailCreateReq;
import com.zhufuyu.bless.model.request.chatbot.ChatbotDetailListReq;
import com.zhufuyu.bless.model.request.chatbot.ChatbotListReq;
import com.zhufuyu.bless.model.response.chatbot.ChatbotDetailItemResp;
import com.zhufuyu.bless.model.response.chatbot.ChatbotMetaItemResp;

import java.util.List;

public interface ChatbotService {

    /**
     * 创建对话
     */
    String createConversation(ChatbotCreateReq request);

    /**
     * 添加对话详情
     */
    String addConversationDetail(ChatbotDetailCreateReq request, Long userId);

    /**
     * 获取用户对话列表
     */
    PageResult<ChatbotMetaItemResp> getConversationList(ChatbotListReq request);

    /**
     * 获取对话详情列表
     */
    List<ChatbotDetailItemResp> getConversationDetails(ChatbotDetailListReq request, Long userId);

    /**
     * 获取最近的对话详情（用于上下文）
     */
    List<ChatbotDetailItemResp> getRecentConversationDetails(String conversationId, int limit, Long userId);

    /**
     * 切换对话
     */
    void switchConversation(String conversationId, Long userId);

    /**
     * 删除对话
     */
    void deleteConversation(String conversationId, Long userId);

    /**
     * 获取对话meta信息
     */
    ChatbotMetaItemResp getConversationMeta(String conversationId, Long userId);
}