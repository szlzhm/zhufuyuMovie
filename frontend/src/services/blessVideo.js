import http from './http.js';

/**
 * 分页查询祝福语视频列表
 */
export const listBlessVideos = (data) => {
  return http.post('/video/list/query/v1', data);
};

/**
 * 获取祝福语视频详情
 */
export const getBlessVideoDetail = (id) => {
  return http.post('/video/detail/query/v1', id);
};

/**
 * 更新视频发布信息
 */
export const updateVideoPublishInfo = (data) => {
  return http.post('/video/publish-info/update/v1', data);
};

/**
 * 删除祝福语视频
 */
export const deleteBlessVideo = (id) => {
  return http.post('/video/delete/v1', { id });
};
