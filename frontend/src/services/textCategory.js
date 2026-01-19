import http from './http.js';

// 查询文案分类列表
export function listTextCategories(params) {
  return http.post('/admin/text-category/list/query/v1', params);
}

// 创建文案分类
export function createTextCategory(data) {
  return http.post('/admin/text-category/create/v1', data);
}

// 更新文案分类
export function updateTextCategory(data) {
  return http.post('/admin/text-category/update/v1', data);
}

// 更新文案分类状态（启用/禁用）
export function updateTextCategoryStatus(data) {
  return http.post('/admin/text-category/status/update/v1', data);
}

// 删除文案分类
export function deleteTextCategory(id) {
  return http.post('/admin/text-category/delete/v1', { id });
}
