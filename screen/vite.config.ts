import {defineConfig} from 'vite'
import vue from '@vitejs/plugin-vue'
import {resolve} from 'path'

export default defineConfig({
    plugins: [vue()],
    root: resolve(__dirname, 'screen'),
    publicDir: resolve(__dirname, 'public'),
    base: './',
    build: {
        outDir: '../dist',
        emptyOutDir: true,
        rolldownOptions: {
            input: {
                click: resolve(__dirname, 'screen/click/index.html'),
                hud: resolve(__dirname, 'screen/hud/index.html'),
                multiplayer: resolve(__dirname, 'screen/multiplayer/index.html'),
                title: resolve(__dirname, 'screen/title/index.html'),
            },
            output: {
                entryFileNames: 'assets/[name].js',
                chunkFileNames: 'assets/[name].js',
                assetFileNames: 'assets/[name].[ext]',
            }
        }
    }
})