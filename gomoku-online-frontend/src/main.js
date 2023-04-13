import { createApp } from 'vue'
import App from './App.vue'

import './assets/main.css'

import axios from 'axios'

axios.defaults.baseURL = '/'

createApp(App).mount('#app')
