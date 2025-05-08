<template>
  <div>
    <h1>注册</h1>
    <form @submit.prevent="register">
      <input type="text" v-model="username" placeholder="用户名">
      <input type="email" v-model="email" placeholder="邮箱">
      <input type="password" v-model="password" placeholder="密码">
      <button type="submit">注册</button>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const username = ref('')
const email = ref('')
const password = ref('')

function register() {
  console.log('注册', username.value, email.value, password.value)
}
</script>

<style scoped>
</style>