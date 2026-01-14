import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  server: {
    port: 3000,
    open: true, // 自动打开浏览器
    proxy: {
      '/api': {
        target: 'http://localhost:9999', // 网关服务端口
        changeOrigin: true,
        rewrite: (path) => {
          // 移除 /api 前缀，因为后端服务不需要这个前缀
          return path.replace(/^\/api/, '');
        }
      }
    }
  }
})