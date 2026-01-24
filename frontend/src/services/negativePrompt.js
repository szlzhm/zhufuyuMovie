import http from './http.js';

/**
 * 负面提示语相关接口
 */

/**
 * 保存或更新负面提示语
 * @param {Object} data 负面提示语数据
 */
export function saveNegativePrompt(data) {
  return http.post('/negative-prompt/save/v1', data);
}

/**
 * 分页查询负面提示语
 * @param {Object} data 查询参数
 */
export function queryNegativePrompts(data) {
  return http.post('/negative-prompt/query/v1', data);
}

/**
 * 删除负面提示语
 * @param {number} id 负面提示语ID
 */
export function deleteNegativePrompt(id) {
  return http.post('/negative-prompt/delete/v1', { id });
}

/**
 * 获取所有负面提示语列表
 */
export function listAllNegativePrompts() {
  return http.post('/negative-prompt/list-all/v1');
}
