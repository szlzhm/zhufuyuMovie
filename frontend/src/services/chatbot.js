import http from './http.js';

// 创建对话
export function createConversation(data) {
  return http.post('/chatbot/conversation/create/v1', data);
}

// 添加对话详情
export function addConversationDetail(data) {
  return http.post('/chatbot/conversation/detail/create/v1', data);
}

// 获取用户对话列表
export function getConversationList(params = {}) {
  const defaultParams = {
    pageNo: 1,
    pageSize: 20,
    ...params
  };
  return http.post('/chatbot/conversation/list/v1', defaultParams);
}

// 获取对话详情列表
export function getConversationDetails(params = {}) {
  const defaultParams = {
    pageNo: 1,
    pageSize: 100, // 默认获取最近100条作为上下文
    ...params
  };
  return http.post('/chatbot/conversation/details/v1', defaultParams);
}

// 获取最近的对话详情（用于上下文）
export function getRecentConversationDetails(conversationId, limit = 100) {
  return http.get(`/chatbot/conversation/${conversationId}/recent-details/${limit}`);
}

// 切换对话
export function switchConversation(conversationId) {
  return http.post('/chatbot/conversation/switch/v1', null, {
    params: { conversationId }
  });
}

// 删除对话
export function deleteConversation(conversationId) {
  return http.post('/chatbot/conversation/delete/v1', null, {
    params: { conversationId }
  });
}

// 获取对话meta信息
export function getConversationMeta(conversationId) {
  return http.get(`/chatbot/conversation/meta/${conversationId}`);
}