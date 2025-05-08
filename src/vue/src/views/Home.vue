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
          <el-button  @click="$router.push('/login')">立即登录</el-button>
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
@import '@/assets/styles/variables.scss';

.home-container {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  box-sizing: border-box;
  
  @media (max-width: $breakpoint-sm) {
    padding: 0 10px;
  }
}

.category-tabs {
  background-color: $bg-light;
  border-radius: $border-radius;
  padding: 8px 16px;
  margin-bottom: 20px;
  box-shadow: $box-shadow-light;
  width: 100%;
  
  :deep(.el-tabs__item) {
    font-size: $font-size-medium;
    padding: 0 20px;
    
    @media (max-width: $breakpoint-sm) {
      padding: 0 10px;
      font-size: $font-size-normal;
    }
    
    &.is-active {
      color: $primary-color;
      font-weight: bold;
    }
  }
  
  :deep(.el-tabs__active-bar) {
    background-color: $primary-color;
  }
}

.content-container {
  background-color: $bg-light;
  border-radius: $border-radius;
  padding: 20px;
  min-height: 400px;
  box-shadow: $box-shadow-light;
  width: 100%;
  
  .placeholder-content {
    margin: 20px 0;
    
    .user-tips {
      display: flex;
      align-items: center;
      justify-content: center;
      margin-top: 30px;
      color: $text-light;
      
      .el-icon {
        margin-right: 8px;
        color: $primary-color;
      }
    }
  }
}
</style>