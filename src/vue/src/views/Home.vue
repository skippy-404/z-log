<template>
  <app-layout>
    <div class="home-container">
      <!-- 顶部分类标签 -->
      <div class="category-tabs">
        <el-tabs v-model="activeCategory">
          <el-tab-pane label="推荐" name="recommended"></el-tab-pane>
          <el-tab-pane label="关注" name="following"></el-tab-pane>
          <el-tab-pane label="热门" name="hot"></el-tab-pane>
        </el-tabs>
      </div>
      
      <!-- 简单内容展示区域 -->
      <div class="content-container">
        <el-empty v-if="!isLogin" description="登录后查看更多内容">
          <template #description>
            <span>登录后查看更多精彩内容</span>
          </template>
          <el-button @click="$router.push('/login')">立即登录</el-button>
        </el-empty>
        
        <div v-else class="placeholder-content">
          <el-skeleton :rows="5" animated />
          <div class="user-tips">
            <el-icon><InfoFilled /></el-icon>
            <span>这里将显示您关注的内容</span>
          </div>
        </div>
      </div>
  </div>
  </app-layout>
</template>

<script setup>
import { ref, computed } from 'vue'
import AppLayout from '@/components/layout/AppLayout.vue'

const activeCategory = ref('recommended')

// 简单模拟登录状态
const isLogin = computed(() => {
  return localStorage.getItem('token') !== null
})
</script>

<style lang="scss" scoped>
@use '@/assets/styles/variables.scss' as *;

.home-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 32px;
  box-sizing: border-box;
  
  @media (max-width: $breakpoint-sm) {
    padding: 20px 16px;
  }
}

.category-tabs {
  background-color: #fff;
  border-radius: 20px;
  padding: 16px 24px;
  margin-bottom: 24px;
  box-shadow: 0 8px 32px rgba(125, 176, 232, 0.10), 0 1.5px 8px rgba(0,0,0,0.04);
  border: 1.5px solid #f0f4fa;
  
  :deep(.el-tabs__item) {
    font-size: 1.1rem;
    font-weight: 600;
    color: #555;
    padding: 0 24px;
    
    @media (max-width: $breakpoint-sm) {
      padding: 0 16px;
      font-size: 1rem;
    }
    
    &.is-active {
      color: #4a90e2;
    }
  }
  
  :deep(.el-tabs__active-bar) {
    background-color: #4a90e2;
    height: 3px;
    border-radius: 3px;
  }

  :deep(.el-tabs__nav-wrap::after) {
    height: 1px;
    background-color: #f0f4fa;
  }
}

.content-container {
  background-color: #fff;
  border-radius: 20px;
  padding: 32px;
  min-height: 500px;
  box-shadow: 0 8px 32px rgba(125, 176, 232, 0.10), 0 1.5px 8px rgba(0,0,0,0.04);
  border: 1.5px solid #f0f4fa;
  
  :deep(.el-empty) {
    padding: 60px 0;
    
    .el-empty__description {
      margin-top: 16px;
      margin-bottom: 24px;
      color: #7db0e8;
      font-size: 1.1rem;
    }
    
    .el-button {
      background: linear-gradient(90deg, #7db0e8 0%, #4a90e2 100%);
      border: none;
      border-radius: 14px !important;
      padding: 12px 32px;
      font-size: 1rem;
      font-weight: 600;
      box-shadow: 0 4px 16px rgba(125, 176, 232, 0.13);
      
      &:hover {
        background: linear-gradient(90deg, #4a90e2 0%, #7db0e8 100%);
        box-shadow: 0 8px 24px rgba(125, 176, 232, 0.2);
      }
    }
  }
  
  .placeholder-content {
    margin: 20px 0;
    
    .user-tips {
      display: flex;
      align-items: center;
      justify-content: center;
      margin-top: 40px;
      color: #7db0e8;
      font-size: 1.1rem;
      
      .el-icon {
        margin-right: 8px;
        font-size: 1.2rem;
        color: #4a90e2;
      }
    }
  }
}
</style>