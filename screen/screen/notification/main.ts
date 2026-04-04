import '../../global.css'
import {createApp} from 'vue'
import Screen from './Screen.vue'
import Vue3Toastify, {ToastContainerOptions} from "vue3-toastify";
import 'vue3-toastify/dist/index.css';

createApp(Screen).use(Vue3Toastify, {
    autoClose: 5000,
    position: 'top-right',
    newestOnTop: true,
    closeOnClick: false,
    multiple: true,
    theme: 'colored',
    transition: 'slide',
    closeButton: null
} as ToastContainerOptions).mount('#app')
