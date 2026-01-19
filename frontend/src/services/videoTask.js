import http from './http.js';

/**
 * 创建视频任务
 */
export const createVideoTask = (data) => {
  return http.post('/api/task/create/v1', data);
};

/**
 * 获取任务列表
 */
export const getVideoTaskList = (data) => {
  return http.post('/api/task/list/query/v1', data);
};

/**
 * 执行单个任务
 */
export const executeTask = (taskId) => {
  return http.post('/api/task/execute/v1', taskId);
};

/**
 * 批量执行任务
 */
export const executeTasks = (taskIds) => {
  return http.post('/api/task/execute-batch/v1', taskIds);
};

/**
 * 确认任务结果入库
 */
export const confirmTaskToLibrary = (taskId) => {
  return http.post('/api/task/confirm-library/v1', taskId);
};
