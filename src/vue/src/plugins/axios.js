// 示例，后面可以改成自己的axios配置
import axios from 'axios'

// 创建Axios实例
const api = axios.create({
  // 基础URL，将自动添加到请求URL前
  baseURL: 'http://localhost:8080/api',
  // 请求超时时间
  timeout: 10000,
  // 请求头
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
api.interceptors.request.use(
  config => {
    // 在发送请求前做些什么
    // 例如：添加令牌到请求头
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    // 处理请求错误
    console.error('请求错误：', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  response => {
    // 2xx范围内的状态码会触发此函数
    // 对响应数据做些什么
    const res = response.data
    
    // 假设API返回格式为{code, message, data}
    if (res.code !== 200) {
      console.warn('API返回非成功状态：', res.message)
      // 可以在这里统一处理错误
    }
    
    return res
  },
  error => {
    // 超出2xx范围的状态码会触发此函数
    // 处理响应错误
    console.error('响应错误：', error)
    
    // 处理401未授权（例如令牌过期）
    if (error.response && error.response.status === 401) {
      // 清除令牌并重定向到登录页
      localStorage.removeItem('token')
      // 如果使用了路由，可以用以下方式重定向
      // router.push('/login')
    }
    
    return Promise.reject(error)
  }
)

export default api