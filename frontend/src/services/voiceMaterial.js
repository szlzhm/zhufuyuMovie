import http from './http.js';

/**
 * 创建音色素材
 */
export const createVoiceMaterial = (data) => {
  return http.post('/material/voice/create/v1', data);
};

/**
 * 分页查询音色素材列表
 */
export const listVoiceMaterials = (data) => {
  return http.post('/material/voice/list/query/v1', data);
};

/**
 * 获取音色素材详情
 */
export const getVoiceMaterialDetail = (id) => {
  return http.post('/material/voice/detail/query/v1', id);
};

/**
 * 更新音色素材
 */
export const updateVoiceMaterial = (data) => {
  return http.post('/material/voice/update/v1', data);
};

/**
 * 切换音色素材状态
 */
export const toggleVoiceMaterialStatus = (data) => {
  return http.post('/material/voice/toggle-status/v1', data);
};
