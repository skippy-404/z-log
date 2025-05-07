import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
// 导入路由
import router from './router'
// 导入ElementPlus 组件库
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'


//createApp(App).use(router).use(ElementPlus).mount('#app')
const app = createApp(App)
app.use(router)
app.use(ElementPlus)
app.mount('#app')
