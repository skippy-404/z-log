<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <h2>登录 Z-Log</h2>
        <p>分享生活，发现美好</p>
      </div>
      
      <el-form 
        ref="formRef" 
        :model="loginForm" 
        :rules="loginRules" 
        label-position="top"
        @submit.prevent="handleLogin"
      >
        <el-form-item label="账号" prop="username">
          <el-input 
            v-model="loginForm.username" 
            placeholder="用户名/手机号/邮箱" 
            prefix-icon="User"
          ></el-input>
        </el-form-item>
        
        <el-form-item label="密码" prop="password">
          <el-input 
            v-model="loginForm.password" 
            type="password" 
            placeholder="请输入密码" 
            prefix-icon="Lock"
            show-password
          ></el-input>
        </el-form-item>
        
        <div class="login-options">
          <el-checkbox v-model="rememberMe">记住我</el-checkbox>
          <el-button text type="primary">忘记密码?</el-button>
        </div>
        
        <el-button type="danger" native-type="submit" :loading="loading" class="login-button">登录</el-button>
        
        <div class="register-link">
          还没有账号? <el-button text type="primary" @click="$router.push('/register')">立即注册</el-button>
        </div>
        
        <div class="other-login">
          <p>其他登录方式</p>
          <div class="social-icons">
            <el-button circle><el-icon><ChatDotSquare /></el-icon></el-button>
            <el-button circle><el-icon><ChatRound /></el-icon></el-button>
            <el-button circle><el-icon><ChatLineSquare /></el-icon></el-button>
          </div>
        </div>
      </el-form>
    </div>
    
    <div class="back-home">
      <el-button text @click="$router.push('/')">
        <el-icon><ArrowLeft /></el-icon> 返回首页
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const loginForm = reactive({
  username: '',
  password: ''
})

const loginRules = {
  username: [
    { required: true, message: '请输入账号', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少为6个字符', trigger: 'blur' }
  ]
}

const rememberMe = ref(false)
const loading = ref(false)
const formRef = ref(null)

// 登录处理
const handleLogin = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      
      try {
        // 这里暂时模拟登录成功
        setTimeout(() => {
          // 保存token到localStorage
          localStorage.setItem('token', 'mock-token')
          
          // 登录成功，跳转到首页
          router.push('/')
          loading.value = false
        }, 1000)
      } catch (error) {
        loading.value = false
        console.error('登录失败', error)
      }
    }
  })
}
</script>

<style lang="scss" scoped>
@import '@/assets/styles/variables.scss';

.login-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background-color: $bg-color;
  background-image: linear-gradient(135deg, #f8f9fa 0%, #FFEEF1 100%);
  background-size: cover;
  background-attachment: fixed;
  padding: 20px;
  width: 100%;
  position: absolute;
  left: 0;
  top: 0;
  right: 0;
  bottom: 0;
}

.login-card {
  width: 100%;
  max-width: 420px;
  background-color: $bg-light;
  border-radius: $border-radius;
  box-shadow: $box-shadow;
  padding: 30px;
  
  @media (max-width: $breakpoint-xs) {
    padding: 20px;
    margin-top: -50px;
  }
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
  
  h2 {
    font-size: $font-size-xxlarge;
    color: $text-primary;
    margin: 0 0 8px;
    
    @media (max-width: $breakpoint-xs) {
      font-size: $font-size-xlarge;
    }
  }
  
  p {
    font-size: $font-size-normal;
    color: $text-light;
    margin: 0;
  }
}

.login-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  
  @media (max-width: $breakpoint-xs) {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
}

.login-button {
  width: 100%;
  margin-bottom: 20px;
  height: 44px;
  font-size: $font-size-medium;
  border-radius: 22px;
  background-color: $primary-color;
  border-color: $primary-color;
  
  &:hover, &:focus {
    background-color: darken($primary-color, 10%);
    border-color: darken($primary-color, 10%);
  }
}

.register-link {
  text-align: center;
  margin-bottom: 24px;
  font-size: $font-size-normal;
  color: $text-secondary;
}

.other-login {
  text-align: center;
  border-top: 1px solid $border-light;
  padding-top: 20px;
  
  p {
    font-size: $font-size-normal;
    color: $text-light;
    margin-bottom: 16px;
  }
  
  .social-icons {
    display: flex;
    justify-content: center;
    gap: 20px;
    
    .el-button {
      color: $primary-color;
      border-color: $border-light;
      
      &:hover {
        background-color: $secondary-color;
        border-color: $secondary-color;
      }
    }
  }
}

.back-home {
  margin-top: 20px;
  color: $text-light;
  
  .el-button {
    color: $text-secondary;
    
    &:hover {
      color: $primary-color;
    }
  }
}
</style>