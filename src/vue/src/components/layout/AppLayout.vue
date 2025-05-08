<template>
  <div class="app-layout">
    <!-- 顶部导航栏 -->
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
            <!-- 占位菜单项，使真正的按钮靠右 -->
            <div class="flex-grow" />
            
            <!-- 个人主页图标 -->
            <el-menu-item index="1" class="profile-item">
              <router-link to="/profile" class="profile-link">
                <el-button circle>
                  <el-icon><User /></el-icon>
                </el-button>
              </router-link>
            </el-menu-item>
            
            <!-- 发布按钮 -->
            <el-menu-item index="2" class="publish-item">
              <router-link to="/publish" class="publish-btn">
                <el-button type="danger" round>
                  <el-icon><Plus /></el-icon>发布
                </el-button>
              </router-link>
            </el-menu-item>
            
            <!-- 登录/注册按钮或用户头像 -->
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
                  <el-button type="danger" plain>登录</el-button>
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
@import '@/assets/styles/variables.scss';

.app-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.header {
  padding: 0;
  box-shadow: $box-shadow-light;
  position: sticky;
  top: 0;
  z-index: 100;
  background-color: white;
  
  .header-content {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
    height: 100%;
    display: flex;
    align-items: center;
  }
  
  .logo-container {
    margin-right: 20px;
    
    .logo {
      color: $primary-color;
      font-size: 24px;
      margin: 0;
      font-weight: bold;
      white-space: nowrap;
    }
  }
  
  .search-container {
    width: 300px;
    margin-right: 20px;
    
    @media (max-width: $breakpoint-md) {
      width: 200px;
    }
    
    @media (max-width: $breakpoint-sm) {
      flex: 1;
    }
  }
  
  .menu-container {
    flex: 1;
    
    .nav-menu {
      display: flex;
      justify-content: flex-end;
      border-bottom: none;
      
      .flex-grow {
        flex: 1;
      }
      
      :deep(.el-menu-item) {
        padding: 0 10px;
        
        &.is-active {
          background-color: transparent !important;
          border-bottom-color: transparent !important;
        }
        
        &:hover {
          background-color: transparent !important;
        }
      }
      
      :deep(.el-sub-menu) {
        &.is-active {
          .el-sub-menu__title {
            border-bottom-color: transparent !important;
          }
        }
        
        .el-sub-menu__title:hover {
          background-color: transparent !important;
        }
      }
      
      .profile-item, .publish-item, .login-item, .register-item {
        height: auto;
        line-height: normal;
        padding: 10px 5px;
      }
      
      .profile-link {
        .el-button {
          color: $text-secondary;
          display: flex;
          align-items: center;
          justify-content: center;
          
          &:hover {
            color: $primary-color;
          }
          
          .el-icon {
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0;
            font-size: 16px;
          }
        }
      }
      
      @media (max-width: $breakpoint-sm) {
        .publish-btn {
          .el-button {
            padding: 6px 12px;
            
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
  background-color: #f4f4f4;
  padding: 20px 0;
  text-align: center;
  font-size: $font-size-small;
  color: $text-light;
  
  p {
    max-width: 1200px;
    width: 100%;
    margin: 0 auto;
    padding: 0 20px;
  }
}
</style> 