<template>
  <div class="app-layout">
    <!-- 顶部导航栏（发布页同款） -->
    <el-header class="header">
      <div class="header-content">
        <div class="logo-container">
          <router-link to="/">
            <h1 class="logo">Z-Log</h1>
          </router-link>
        </div>
        <div class="search-container">
          <el-input
            placeholder="搜索"
            prefix-icon="Search"
            v-model="searchText"
            clearable
            @keyup.enter="handleSearch"
          ></el-input>
        </div>
        <div class="menu-container">
          <el-menu
            mode="horizontal"
            :ellipsis="false"
            class="nav-menu"
            :default-active="activeIndex"
            background-color="transparent"
            text-color="#333"
            active-text-color="#333"
          >
            <div class="flex-grow" />
            <el-menu-item index="1" class="profile-item">
              <router-link to="/profile" class="profile-link">
                <el-button circle>
                  <el-icon><User /></el-icon>
                </el-button>
              </router-link>
            </el-menu-item>
            <el-menu-item index="2" class="publish-item">
              <router-link to="/publish" class="publish-btn">
                <el-button type="" round>
                  <el-icon><Plus /></el-icon>发布
                </el-button>
              </router-link>
            </el-menu-item>
            <template v-if="isLogin">
              <el-sub-menu index="3">
                <template #title>
                  <el-avatar :size="32" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"></el-avatar>
                </template>
                <el-menu-item index="3-1">我的主页</el-menu-item>
                <el-menu-item index="3-2">我的收藏</el-menu-item>
                <el-menu-item index="3-3">个人设置</el-menu-item>
                <el-menu-item index="3-4" @click="handleLogout">退出登录</el-menu-item>
              </el-sub-menu>
            </template>
            <template v-else>
              <el-menu-item index="4" class="login-item">
                <router-link to="/login">
                  <el-button type="" plain :style="{ color: '$text-secondary', backgroundColor: '$primary-color' }">登录</el-button>
                </router-link>
              </el-menu-item>
              <el-menu-item index="5" class="register-item">
                <router-link to="/register">
                  <el-button>注册</el-button>
                </router-link>
              </el-menu-item>
            </template>
          </el-menu>
        </div>
      </div>
    </el-header>
    <!-- 主要内容区 -->
    <div class="main-content">
      <slot></slot>
    </div>
    <!-- 底部信息（简单版本） -->
    <el-footer class="footer">
      <p>© 2023 Z-Log 版权所有</p>
    </el-footer>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { Plus, User } from '@element-plus/icons-vue'

const router = useRouter()
const searchText = ref('')
const activeIndex = ref('0') // 设置一个默认不存在的索引，避免高亮

// 简单模拟登录状态，实际应使用全局状态管理
const isLogin = computed(() => {
  return localStorage.getItem('token') !== null
})

// 搜索功能
const handleSearch = () => {
  if (searchText.value.trim()) {
    console.log('搜索:', searchText.value)
    // 实际开发中可跳转到搜索结果页面
    // router.push(`/search?q=${encodeURIComponent(searchText.value)}`)
  }
}

// 登出功能
const handleLogout = () => {
  localStorage.removeItem('token')
  router.push('/login')
}
</script>

<style lang="scss" scoped>
@use '@/assets/styles/variables.scss' as *;

.app-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: #f7fafd;
}

.header {
  padding: 0;
  box-shadow: 0 4px 16px rgba(125, 176, 232, 0.08);
  position: sticky;
  top: 0;
  z-index: 100;
  background-color: white;
  height: 64px;
  
  .header-content {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 32px;
    height: 100%;
    display: flex;
    align-items: center;
  }
  
  .logo-container {
    margin-right: 32px;
    
    .logo {
      color: #4a90e2;
      font-size: 2rem;
      margin: 0;
      font-weight: 700;
      letter-spacing: 1px;
      white-space: nowrap;
    }
  }
  
  .search-container {
    width: 360px;
    margin-right: 40px;
    
    :deep(.el-input__wrapper) {
      border-radius: 14px !important;
      box-shadow: 0 2px 8px rgba(125, 176, 232, 0.08);
      border: 1.5px solid #e3eaf2;
      background: #f7fafd;
      transition: box-shadow 0.2s, border-color 0.2s;
      padding: 0 16px;
      height: 40px;
      
      &:hover {
        border-color: #7db0e8;
        box-shadow: 0 4px 16px rgba(125, 176, 232, 0.13);
      }
      
      &.is-focus {
        border-color: #4a90e2;
        box-shadow: 0 0 0 2px #b3d6f7;
      }
    }
    
    :deep(.el-input__inner) {
      font-size: 0.95rem;
      color: #333;
    }
    
    :deep(.el-input__icon) {
      color: #7db0e8;
      font-size: 1.2rem;
    }
    
    @media (max-width: $breakpoint-md) {
      width: 280px;
    }
    
    @media (max-width: $breakpoint-sm) {
      flex: 1;
      max-width: 200px;
    }
  }
  
  .menu-container {
    flex: 1;
    
    .nav-menu {
      display: flex;
      justify-content: flex-end;
      border-bottom: none;
      height: 64px;
      
      .flex-grow {
        flex: 1;
      }
      
      :deep(.el-menu-item) {
        padding: 0 12px;
        height: 64px;
        line-height: 64px;
        font-size: 1rem;
        
        &.is-active {
          background-color: transparent !important;
          border-bottom-color: transparent !important;
          color: #4a90e2 !important;
          font-weight: 600;
        }
        
        &:hover {
          background-color: transparent !important;
          color: #7db0e8 !important;
        }
      }
      
      :deep(.el-sub-menu) {
        &.is-active {
          .el-sub-menu__title {
            border-bottom-color: transparent !important;
            color: #4a90e2 !important;
          }
        }
        
        .el-sub-menu__title:hover {
          background-color: transparent !important;
          color: #7db0e8 !important;
        }
      }
      
      .profile-item, .publish-item, .login-item, .register-item {
        height: 64px;
        line-height: 64px;
        padding: 0 12px;
        display: flex;
        align-items: center;
      }
      
      .profile-link {
        display: flex;
        align-items: center;
        justify-content: center;
        
        .el-button {
          color: #7db0e8;
          display: flex;
          align-items: center;
          justify-content: center;
          border-radius: 14px !important;
          border: 1.5px solid #e3eaf2;
          background: #f7fafd;
          box-shadow: 0 2px 8px rgba(125, 176, 232, 0.08);
          height: 40px;
          width: 40px;
          min-width: 40px;
          max-width: 40px;
          padding: 0;
          
          &:hover {
            color: #4a90e2;
            border-color: #7db0e8;
            box-shadow: 0 4px 16px rgba(125, 176, 232, 0.13);
          }
          
          .el-icon {
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0;
            font-size: 1.2rem;
          }
        }
      }
      
      .publish-btn {
        display: flex;
        align-items: center;
        height: 100%;
        
        .el-button {
          background: linear-gradient(90deg, #7db0e8 0%, #4a90e2 100%);
          color: white;
          border: none;
          border-radius: 14px !important;
          padding: 0 20px;
          height: 40px;
          font-size: 0.95rem;
          font-weight: 600;
          box-shadow: 0 2px 8px rgba(125, 176, 232, 0.13);
          
          &:hover {
            background: linear-gradient(90deg, #4a90e2 0%, #7db0e8 100%);
            box-shadow: 0 4px 16px rgba(125, 176, 232, 0.2);
          }
          
          .el-icon {
            font-size: 1rem;
            margin-right: 4px;
          }
        }
      }
      
      .login-item, .register-item {
        display: flex;
        align-items: center;
        height: 64px;
        
        a {
          display: flex;
          align-items: center;
          height: 100%;
        }
        
        .el-button {
          border-radius: 14px !important;
          height: 40px;
          font-size: 0.95rem;
          font-weight: 600;
          padding: 0 20px;
        }
      }
      
      .login-item {
        .el-button {
          background: #f7fafd;
          color: #4a90e2;
          border: 1.5px solid #b3d6f7;
          
          &:hover {
            background: #e3eaf2;
            border-color: #7db0e8;
          }
        }
      }
      
      .register-item {
        .el-button {
          background: #4a90e2;
          color: white;
          border: none;
          box-shadow: 0 2px 8px rgba(125, 176, 232, 0.13);
          
          &:hover {
            background: #7db0e8;
            box-shadow: 0 4px 16px rgba(125, 176, 232, 0.2);
          }
        }
      }
      
      @media (max-width: $breakpoint-sm) {
        .publish-btn {
          .el-button {
            padding: 0 16px;
            
            .el-icon {
              margin-right: 0;
            }
            
            span:not(.el-icon) {
              display: none;
            }
          }
        }
      }
    }
  }
}

.main-content {
  flex: 1;
  padding: 20px;
  max-width: 1200px;
  width: 100%;
  margin: 0 auto;
  
  @media (max-width: $breakpoint-sm) {
    padding: 15px 10px;
  }
}

.footer {
  background-color: #f7fafd;
  padding: 28px 0;
  text-align: center;
  font-size: 0.9rem;
  color: #7db0e8;
  border-top: 1.5px solid #e3eaf2;
  
  p {
    max-width: 1200px;
    width: 100%;
    margin: 0 auto;
    padding: 0 32px;
    letter-spacing: 0.5px;
  }
}
</style>