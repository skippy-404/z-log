<template>
  <div class="register-container">
    <div class="register-card">
      <div class="register-header">
        <h2>注册 Z-Log</h2>
        <p>加入我们，分享你的精彩生活</p>
      </div>
      
      <el-form 
        ref="formRef" 
        :model="registerForm" 
        :rules="registerRules" 
        label-position="top"
        @submit.prevent="handleRegister"
      >
        <el-form-item label="用户名" prop="username">
          <el-input 
            v-model="registerForm.username" 
            placeholder="请输入用户名" 
            prefix-icon="User"
          ></el-input>
        </el-form-item>
        
        <el-form-item label="手机号" prop="phone">
          <el-input 
            v-model="registerForm.phone" 
            placeholder="请输入手机号" 
            prefix-icon="Phone"
          ></el-input>
        </el-form-item>
        
        <el-form-item label="验证码" prop="verifyCode">
          <div class="verify-code-input">
            <el-input 
              v-model="registerForm.verifyCode" 
              placeholder="请输入验证码"
              maxlength="6"
            ></el-input>
            <el-button type="primary" :disabled="codeSending" @click="sendVerifyCode">
              {{ codeButtonText }}
            </el-button>
          </div>
        </el-form-item>
        
        <el-form-item label="密码" prop="password">
          <el-input 
            v-model="registerForm.password" 
            type="password" 
            placeholder="请输入密码（6-20位字符）" 
            prefix-icon="Lock"
            show-password
          ></el-input>
        </el-form-item>
        
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input 
            v-model="registerForm.confirmPassword" 
            type="password" 
            placeholder="请再次输入密码" 
            prefix-icon="Lock"
            show-password
          ></el-input>
        </el-form-item>
        
        <el-form-item>
          <el-checkbox v-model="agreeTerms" @change="validateAgree">
            我已阅读并同意<el-button text type="primary">《用户协议》</el-button>和<el-button text type="primary">《隐私政策》</el-button>
          </el-checkbox>
        </el-form-item>
        
        <el-button 
          type="danger" 
          native-type="submit" 
          :loading="loading" 
          :disabled="!agreeTerms"
          class="register-button"
        >
          立即注册
        </el-button>
        
        <div class="login-link">
          已有账号？<el-button text type="primary" @click="$router.push('/login')">立即登录</el-button>
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
import { ElMessage } from 'element-plus'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)
const agreeTerms = ref(false)
const codeSending = ref(false)
const countdown = ref(0)
const codeButtonText = ref('获取验证码')

// 表单数据
const registerForm = reactive({
  username: '',
  phone: '',
  verifyCode: '',
  password: '',
  confirmPassword: ''
})

// 表单验证规则
const validatePass = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入密码'))
  } else {
    if (registerForm.confirmPassword !== '') {
      formRef.value?.validateField('confirmPassword')
    }
    callback()
  }
}

const validatePass2 = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  verifyCode: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 6, message: '验证码长度应为6位', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' },
    { validator: validatePass, trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validatePass2, trigger: 'blur' }
  ]
}

// 发送验证码
const sendVerifyCode = () => {
  formRef.value?.validateField('phone', (valid) => {
    if (!valid) {
      startCountdown()
      ElMessage.success('验证码已发送至您的手机')
    }
  })
}

// 开始倒计时
const startCountdown = () => {
  codeSending.value = true
  countdown.value = 60
  codeButtonText.value = `${countdown.value}秒后重新获取`
  
  const timer = setInterval(() => {
    countdown.value--
    codeButtonText.value = `${countdown.value}秒后重新获取`
    
    if (countdown.value <= 0) {
      clearInterval(timer)
      codeSending.value = false
      codeButtonText.value = '获取验证码'
    }
  }, 1000)
}

// 验证用户协议是否勾选
const validateAgree = () => {
  if (!agreeTerms.value) {
    ElMessage.warning('请阅读并同意用户协议和隐私政策')
  }
}

// 注册处理
const handleRegister = async () => {
  if (!formRef.value) return
  
  if (!agreeTerms.value) {
    ElMessage.warning('请阅读并同意用户协议和隐私政策')
    return
  }
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      
      try {
        // 这里暂时模拟注册成功
        setTimeout(() => {
          ElMessage.success('注册成功')
          
          // 注册成功，跳转到登录页
          router.push('/login')
          loading.value = false
        }, 1500)
      } catch (error) {
        loading.value = false
        console.error('注册失败', error)
      }
    }
  })
}
</script>

<style lang="scss" scoped>
@import '@/assets/styles/variables.scss';

.register-container {
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

.register-card {
  width: 100%;
  max-width: 480px;
  background-color: $bg-light;
  border-radius: $border-radius;
  box-shadow: $box-shadow;
  padding: 30px;
  
  @media (max-width: $breakpoint-xs) {
    padding: 20px;
  }
}

.register-header {
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

.verify-code-input {
  display: flex;
  gap: 10px;
  
  .el-input {
    flex: 1;
  }
  
  .el-button {
    width: 140px;
    border-radius: 20px;
    
    @media (max-width: $breakpoint-xs) {
      width: 120px;
      font-size: $font-size-mini;
    }
  }
}

.register-button {
  width: 100%;
  margin-top: 10px;
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
  
  &.is-disabled {
    background-color: lighten($primary-color, 20%);
    border-color: lighten($primary-color, 20%);
  }
}

.login-link {
  text-align: center;
  font-size: $font-size-normal;
  color: $text-secondary;
}

:deep(.el-form-item__label) {
  color: $text-secondary;
  font-size: $font-size-normal;
}

:deep(.el-input__inner) {
  border-radius: 8px;
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