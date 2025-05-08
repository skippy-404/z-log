<template>
  <div>
    <h1>登录</h1>
    <form @submit.prevent="login">
      <input type="text" v-model="username" placeholder="用户名">
      <input type="password" v-model="password" placeholder="密码">
      <button type="submit">登录</button>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const username = ref('')
const password = ref('')

function login() {
  console.log('登录', username.value, password.value)
}
</script>

<style scoped>
</style>