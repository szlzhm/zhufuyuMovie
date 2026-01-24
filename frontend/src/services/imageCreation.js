import http from './http.js';

/**
 * 图片创作相关接口
 */

// --- 提示语模板相关接口 ---

/**
 * 保存或更新提示语模板
 * @param {Object} data 模板数据
 */
export function saveTemplate(data) {
  return http.post('/image-creation/template/save/v1', data);
}

/**
 * 分页查询提示语模板
 * @param {Object} data 查询参数
 */
export function queryTemplates(data) {
  return http.post('/image-creation/template/query/v1', data);
}

/**
 * 删除提示语模板
 * @param {number} id 模板ID
 */
export function deleteTemplate(id) {
  return http.post('/image-creation/template/delete/v1', { id });
}

/**
 * 获取所有启用的模板列表 (用于下拉选择)
 */
export function listAllActiveTemplates() {
  return http.post('/image-creation/template/list-all/v1');
}

/**
 * 获取模板详情
 * @param {number} id 模板ID
 */
export function getTemplateDetail(id) {
  return http.post('/image-creation/template/get/v1', { id });
}

/**
 * 从创作结果保存为模板
 * @param {Object} data 保存参数
 */
export function saveTemplateFromResult(data) {
  return http.post('/image-creation/template/save-from-result/v1', data);
}

// --- 图片创作任务相关接口 ---

/**
 * 提交图片创作任务
 * @param {Object} data 任务提交参数
 */
export function submitImageTask(data) {
  return http.post('/image-creation/task/submit/v1', data);
}

/**
 * 分页查询创作任务列表
 * @param {Object} data 查询参数
 */
export function queryTasks(data) {
  return http.post('/image-creation/task/query/v1', data);
}

/**
 * 更新任务状态
 * @param {Object} data 状态更新参数 { taskId, newStatus }
 */
export function updateTaskStatus(data) {
  return http.post('/image-creation/task/update-status/v1', data);
}

/**
 * 获取任务详情（含生成结果）
 * @param {number} taskId 任务ID
 */
export function getTaskDetail(taskId) {
  return http.post('/image-creation/task/detail/v1', { taskId });
}

// --- 图片生成结果相关接口 ---

/**
 * 分页查询创作结果列表
 * @param {Object} data 查询参数
 */
export function queryResults(data) {
  return http.post('/image-creation/result/query/v1', data);
}
