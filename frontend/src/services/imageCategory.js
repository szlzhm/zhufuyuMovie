import http from './http.js';

// 查询图片分类列表
export function listImageCategories(params) {
  return http.post('/admin/image-category/list/query/v1', params);
}

// 创建图片分类
export function createImageCategory(data) {
  return http.post('/admin/image-category/create/v1', data);
}

// 更新图片分类
export function updateImageCategory(data) {
  return http.post('/admin/image-category/update/v1', data);
}

// 更新图片分类状态（启用/禁用）
export function updateImageCategoryStatus(data) {
  return http.post('/admin/image-category/status/update/v1', data);
}

// 删除图片分类
export function deleteImageCategory(id) {
  return http.post('/admin/image-category/delete/v1', { id });
}
