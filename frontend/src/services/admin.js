import http from './http.js';

export function listUsers(params) {
  return http.post('/admin/user/list/query/v1', params);
}

export function createUser(data) {
  return http.post('/admin/user/create/v1', data);
}

export function resetUserPassword(data) {
  return http.post('/admin/user/reset/password/v1', data);
}

export function toggleUserStatus(data) {
  return http.post('/admin/user/toggle/status/v1', data);
}
