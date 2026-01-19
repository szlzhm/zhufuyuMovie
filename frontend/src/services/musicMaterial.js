import http from './http.js';

/**
 * 上传背景音乐文件
 */
export const uploadMusicMaterial = (formData) => {
  return http.post('/material/music/upload/v1', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
};

/**
 * 创建背景音乐
 */
export const createMusicMaterial = (data) => {
  return http.post('/material/music/create/v1', data);
};

/**
 * 分页查询背景音乐列表
 */
export const listMusicMaterials = (data) => {
  return http.post('/material/music/list/query/v1', data);
};

/**
 * 获取背景音乐详情
 */
export const getMusicMaterialDetail = (id) => {
  return http.post('/material/music/detail/query/v1', id);
};

/**
 * 更新背景音乐
 */
export const updateMusicMaterial = (data) => {
  return http.post('/material/music/update/v1', data);
};

/**
 * 删除背景音乐
 */
export const deleteMusicMaterial = (id) => {
  return http.post('/material/music/delete/v1', { id });
};
