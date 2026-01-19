import http from './http.js';

export function getCurrentUser() {
  return http.post('/auth/get/currentUser/v1', {});
}
