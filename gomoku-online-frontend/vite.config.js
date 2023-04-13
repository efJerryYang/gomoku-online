import { fileURLToPath, URL } from 'node:url';

import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';

// https://vitejs.dev/config/

export default defineConfig({

  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  publicPath: '/',
  server: {
    port: 8081
  },
  build: {
    rollupOptions: {
      input: {
        main: 'src/main.js'
      }
    },
    optimizeDeps: {
      include: ['axios', 'vue']
    }
  }
});

