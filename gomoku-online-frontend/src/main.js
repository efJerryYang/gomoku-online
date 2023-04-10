import { createApp } from 'vue'
import App from './App.vue'

import './assets/main.css'

import axios from 'axios'
import jwt from 'jsonwebtoken'

axios.defaults.baseURL = 'http://localhost:8080'

createApp(App).mount('#app')
