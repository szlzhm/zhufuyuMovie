import http from './http.js';

// 调用文生图API生成图像（同步）
export const generateImage = (params) => {
  return http.post('/image/generate/v1', params);
};

// 提交异步文生图任务
export const submitAsyncTask = (params) => {
  return http.post('/image/async/generate', params);
};

// 获取任务状态
export const getTaskStatus = (taskId) => {
  return http.get(`/image/async/task/${taskId}`);
};