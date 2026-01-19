import axios from 'axios';
import apiConfig from '../config/apiConfig';

const instance = axios.create({
  baseURL: apiConfig.baseURL,
  timeout: apiConfig.timeout
});

instance.interceptors.request.use((config) => {
  const token = localStorage.getItem('authToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

instance.interceptors.response.use(
  (response) => response.data,
  (error) => {
    if (error.response && error.response.data) {
      return Promise.reject(error.response.data);
    }
    return Promise.reject(error);
  }
);

export default instance;