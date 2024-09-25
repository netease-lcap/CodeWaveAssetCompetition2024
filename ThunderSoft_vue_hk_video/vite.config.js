import {fileURLToPath, URL} from 'node:url'

import {defineConfig} from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
    plugins: [
        vue(),
    ],
    resolve: {
        alias: {
            '@': fileURLToPath(new URL('./src', import.meta.url))
        }
    },
    server: {
        host: '0.0.0.0', // 服务器地址
        proxy: {
            '/api': {
                target: 'https://dev.cfn.defaulttenant.lcap.huaqi-it.com.cn',
                changeOrigin: true,
                rewrite: path => path.replace(/^\/api/, ''),
            }
        }
    },
})
