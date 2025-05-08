// 路由部分
import { createRouter, createWebHistory } from 'vue-router'

// 导入组件
// 懒加载方式
const Home = () => import('@/views/Home.vue')
const Login = () => import('@/views/Login.vue')
const Register = () => import('@/views/Register.vue')
const Publish = () => import('@/views/Publish.vue')

// 定义路由
const routes = [
    {
        path: '/',
        name: 'Home',
        component: Home,
        meta: {
          title: '首页'
        }
      },
    {
      path: '/login',
      name: 'login',
      component: Login,
      meta: {
        title: '登录'
      }
    },
    {
        path: '/register',
        name: 'register',
        component: Register,
        meta: {
          title: '注册'
        }
    },
    {
        path: '/publish',
        name: 'publish',
        component: Publish,
        meta: {
          title: '发布页'
        }
      }
]

// 创建路由实例
const router = createRouter({
  history: createWebHistory(),
  routes
})

// 导出路由
export default router