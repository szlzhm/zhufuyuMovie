import http from './http.js';

// 创建文案素材
export const createTextMaterial = (data) => {
  return http.post('/material/text/create/v1', data);
};

// 分页查询文案素材列表
export const listTextMaterials = (params) => {
  return http.post('/material/text/list/query/v1', params);
};

// 获取文案素材详情
export const getTextMaterialDetail = (id) => {
  return http.post('/material/text/detail/query/v1', id);
};

// 更新文案素材
export const updateTextMaterial = (data) => {
  return http.post('/material/text/update/v1', data);
};

// 删除文案素材
export const deleteTextMaterial = (id) => {
  return http.post('/material/text/delete/v1', { id });
};
