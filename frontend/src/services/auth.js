import http from './http.js';

export function login(data) {
  return http.post('/auth/login/v1', data);
}
