import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
// 导入路由
import router from './router'
// 导入ElementPlus 组件库
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
// 导入ElementPlus 图标库
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

//createApp(App).use(router).use(ElementPlus).mount('#app')
const app = createApp(App)
// 注册ElementsPlus组件库中所有图标库 （for循环迭代注册）
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}
app.use(router)
app.use(ElementPlus)
app.mount('#app')
