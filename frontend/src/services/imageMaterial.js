import http from './http.js';

// 上传图片素材
export const uploadImageMaterial = (formData) => {
  return http.post('/material/image/upload/v1', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
};

// 分页查询图片素材列表
export const listImageMaterials = (params) => {
  return http.post('/material/image/list/query/v1', params);
};

// 获取图片素材详情
export const getImageMaterialDetail = (id) => {
  return http.post('/material/image/detail/query/v1', id);
};

// 更新图片素材
export const updateImageMaterial = (data) => {
  return http.post('/material/image/update/v1', data);
};

// 删除图片素材
export const deleteImageMaterial = (id) => {
  return http.post('/material/image/delete/v1', { id });
};
