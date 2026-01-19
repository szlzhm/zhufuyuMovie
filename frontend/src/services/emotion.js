import http from './http.js';

// 查询情绪列表
export function listEmotions(params = {}) {
  // 设置默认分页参数
  const defaultParams = {
    pageNo: 1,
    pageSize: 10,
    ...params
  };
  return http.post('/admin/emotion/list/query/v1', defaultParams);
}

// 创建情绪
export function createEmotion(data) {
  return http.post('/admin/emotion/create/v1', data);
}

// 更新情绪
export function updateEmotion(data) {
  return http.post('/admin/emotion/update/v1', data);
}

// 更新情绪状态（启用/禁用）
export function updateEmotionStatus(data) {
  return http.post('/admin/emotion/status/update/v1', data);
}

// 删除情绪
export function deleteEmotion(id) {
  return http.post('/admin/emotion/delete/v1', { id });
}
