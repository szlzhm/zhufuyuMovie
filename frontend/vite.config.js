import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  base: '/bless/web/',
  server: {
    host: '0.0.0.0', // 允许外部访问
    port: 8803,
    allowedHosts: ['js2.blockelite.cn', 'localhost', '127.0.0.1'],
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/bless/web/img': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => {
          // 将 /bless/web/img/{path} 转换为 /api/image-creation/image?path={path}
          const imagePath = path.replace('/bless/web/img/', '');
          return `/api/image-creation/image?path=${imagePath}`;
        }
      }
    }
  }
});