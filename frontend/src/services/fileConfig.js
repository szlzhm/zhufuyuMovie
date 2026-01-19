import http from './http.js';

// 查询文件根目录
export function getFileRootPath() {
  return http.post('/admin/config/file/root/query/v1', {});
}

// 更新文件根目录
export function updateFileRootPath(data) {
  return http.post('/admin/config/file/root/update/v1', data);
}

// 查询文件配置列表
export function listFileConfigs() {
  return http.post('/admin/config/file/list/query/v1', {});
}

// 更新文件配置
export function updateFileConfig(data) {
  return http.post('/admin/config/file/update/v1', data);
}
