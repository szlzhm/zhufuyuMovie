package com.zhufuyu.bless.controller.chatbot;

import com.zhufuyu.bless.model.common.BaseResponse;
import com.zhufuyu.bless.model.common.PageResult;
import com.zhufuyu.bless.model.request.chatbot.ChatbotCreateReq;
import com.zhufuyu.bless.model.request.chatbot.ChatbotDetailCreateReq;
import com.zhufuyu.bless.model.request.chatbot.ChatbotDetailListReq;
import com.zhufuyu.bless.model.request.chatbot.ChatbotListReq;
import com.zhufuyu.bless.model.response.chatbot.ChatbotDetailItemResp;
import com.zhufuyu.bless.model.response.chatbot.ChatbotMetaItemResp;
import com.zhufuyu.bless.security.LoginUserContext;
import com.zhufuyu.bless.service.ChatbotService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    private final ChatbotService chatbotService;

    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    /**
     * 创建对话
     */
    @PostMapping("/conversation/create/v1")
    public BaseResponse<String> createConversation(@Valid @RequestBody ChatbotCreateReq request) {
        // 设置当前用户ID
        LoginUserContext.LoginUserInfo userInfo = LoginUserContext.get();
        if (userInfo == null) {
            throw new RuntimeException("用户未登录");
        }
        request.setUserId(userInfo.getUserId());
        
        String conversationId = chatbotService.createConversation(request);
        return BaseResponse.success(conversationId);
    }

    /**
     * 添加对话详情
     */
    @PostMapping("/conversation/detail/create/v1")
    public BaseResponse<String> addConversationDetail(@Valid @RequestBody ChatbotDetailCreateReq request) {
        // 设置当前用户ID
        LoginUserContext.LoginUserInfo userInfo = LoginUserContext.get();
        if (userInfo == null) {
            throw new RuntimeException("用户未登录");
        }
        
        String detailId = chatbotService.addConversationDetail(request, userInfo.getUserId());
        return BaseResponse.success(detailId);
    }

    /**
     * 获取用户对话列表
     */
    @PostMapping("/conversation/list/v1")
    public BaseResponse<PageResult<ChatbotMetaItemResp>> getConversationList(@Valid @RequestBody ChatbotListReq request) {
        // 设置当前用户ID
        LoginUserContext.LoginUserInfo userInfo = LoginUserContext.get();
        if (userInfo == null) {
            throw new RuntimeException("用户未登录");
        }
        request.setUserId(userInfo.getUserId());
        
        PageResult<ChatbotMetaItemResp> result = chatbotService.getConversationList(request);
        return BaseResponse.success(result);
    }

    /**
     * 获取对话详情列表
     */
    @PostMapping("/conversation/details/v1")
    public BaseResponse<List<ChatbotDetailItemResp>> getConversationDetails(@Valid @RequestBody ChatbotDetailListReq request) {
        // 设置当前用户ID
        LoginUserContext.LoginUserInfo userInfo = LoginUserContext.get();
        if (userInfo == null) {
            throw new RuntimeException("用户未登录");
        }
        
        List<ChatbotDetailItemResp> details = chatbotService.getConversationDetails(request, userInfo.getUserId());
        return BaseResponse.success(details);
    }

    /**
     * 获取最近的对话详情（用于上下文）
     */
    @GetMapping("/conversation/{conversationId}/recent-details/{limit}")
    public BaseResponse<List<ChatbotDetailItemResp>> getRecentConversationDetails(
            @PathVariable String conversationId, 
            @PathVariable int limit) {
        // 设置当前用户ID
        LoginUserContext.LoginUserInfo userInfo = LoginUserContext.get();
        if (userInfo == null) {
            throw new RuntimeException("用户未登录");
        }
        
        List<ChatbotDetailItemResp> details = chatbotService.getRecentConversationDetails(conversationId, limit, userInfo.getUserId());
        return BaseResponse.success(details);
    }

    /**
     * 切换对话
     */
    @PostMapping("/conversation/switch/v1")
    public BaseResponse<Void> switchConversation(@RequestParam String conversationId) {
        // 设置当前用户ID
        LoginUserContext.LoginUserInfo userInfo = LoginUserContext.get();
        if (userInfo == null) {
            throw new RuntimeException("用户未登录");
        }
        
        chatbotService.switchConversation(conversationId, userInfo.getUserId());
        return BaseResponse.success(null);
    }

    /**
     * 删除对话
     */
    @PostMapping("/conversation/delete/v1")
    public BaseResponse<Void> deleteConversation(@RequestParam String conversationId) {
        // 设置当前用户ID
        LoginUserContext.LoginUserInfo userInfo = LoginUserContext.get();
        if (userInfo == null) {
            throw new RuntimeException("用户未登录");
        }
        
        chatbotService.deleteConversation(conversationId, userInfo.getUserId());
        return BaseResponse.success(null);
    }

    /**
     * 获取对话meta信息
     */
    @GetMapping("/conversation/meta/{conversationId}")
    public BaseResponse<ChatbotMetaItemResp> getConversationMeta(@PathVariable String conversationId) {
        // 设置当前用户ID
        LoginUserContext.LoginUserInfo userInfo = LoginUserContext.get();
        if (userInfo == null) {
            throw new RuntimeException("用户未登录");
        }
        
        ChatbotMetaItemResp meta = chatbotService.getConversationMeta(conversationId, userInfo.getUserId());
        return BaseResponse.success(meta);
    }
}