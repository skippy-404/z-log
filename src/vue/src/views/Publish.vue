<template>
  <div>
    <h1>发布内容</h1>
    <form @submit.prevent="submitContent">
      <div class="form-group">
        <label for="title">标题</label>
        <input type="text" id="title" v-model="title" placeholder="请输入标题">
      </div>
      
      <div class="form-group">
        <label for="content">内容</label>
        <textarea id="content" v-model="content" placeholder="请输入内容" rows="5"></textarea>
      </div>
      
      <div class="form-group">
        <label for="image">图片链接</label>
        <input type="text" id="image" v-model="image" placeholder="请输入图片链接">
      </div>
      
      <button type="submit">发布</button>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const title = ref('')
const content = ref('')
const image = ref('')

function submitContent() {
  console.log('发布内容', {
    title: title.value,
    content: content.value,
    image: image.value
  })
}
</script>

<style scoped>
.form-group {
  margin-bottom: 15px;
}

label {
  display: block;
  margin-bottom: 5px;
}

input, textarea {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

button {
  background-color: #42b983;
  color: white;
  padding: 10px 15px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

button:hover {
  background-color: #3aa876;
}
</style>