<script setup>
// App主组件
</script>

<template>
  <router-view></router-view>
</template>

<style lang="scss">
@import '@/assets/styles/variables.scss';

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: $text-primary;
  background-color: $bg-color;
  line-height: 1.5;
  width: 100%;
  overflow-x: hidden;
}

a {
  text-decoration: none;
  color: inherit;
}

.page-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

#app {
  width: 100%;
  min-height: 100vh;
}

@media (max-width: $breakpoint-xs) {
  html {
    font-size: 14px;
  }
}
</style>
