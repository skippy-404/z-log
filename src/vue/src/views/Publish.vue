<template>
  <app-layout>
    <div class="publish-container">
      <div class="utility-buttons">
        <el-button type="primary" size="small" icon="Lightning" @click="showInspirationDialog">灵感闪现</el-button>
        <el-button type="success" size="small" icon="ChatDotRound" @click="showAIGenerateDialog">AI一键生成</el-button>
      </div>
      
      <!-- 灵感闪现对话框 -->
      <el-dialog
        v-model="inspirationDialogVisible"
        title="灵感闪现"
        width="500px"
      >
        <div class="inspiration-content">
          <p class="inspiration-tip">以下是一些创作灵感，希望能够帮助到你：</p>
          <div class="inspiration-list">
            <div class="inspiration-item" v-for="(item, index) in inspirationList" :key="index" @click="selectInspiration(item)">
              <div class="inspiration-icon">
                <el-icon><BulbFilled /></el-icon>
              </div>
              <div class="inspiration-text">{{ item }}</div>
            </div>
          </div>
        </div>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="inspirationDialogVisible = false">关闭</el-button>
            <el-button type="primary" @click="applyInspiration">随机灵感</el-button>
          </span>
        </template>
      </el-dialog>
      
      <!-- AI一键生成对话框 -->
      <el-dialog
        v-model="aiGenerateDialogVisible"
        title="AI一键生成"
        width="500px"
      >
        <div class="ai-generate-content">
          <p class="ai-generate-tip">请选择你想生成的内容类型：</p>
          <el-form :model="aiGenerateForm" label-position="top">
            <el-form-item label="内容类型">
              <el-select v-model="aiGenerateForm.type" placeholder="选择内容类型" style="width: 100%;">
                <el-option label="旅行游记" value="travel" />
                <el-option label="美食分享" value="food" />
                <el-option label="读书心得" value="book" />
                <el-option label="影视剧评" value="movie" />
                <el-option label="数码评测" value="tech" />
              </el-select>
            </el-form-item>
            <el-form-item label="关键词">
              <el-input v-model="aiGenerateForm.keyword" placeholder="输入相关关键词（选填）" />
            </el-form-item>
          </el-form>
          
          <div v-if="aiGeneratingStatus" class="ai-generating-status">
            <el-progress type="circle" :percentage="aiGeneratingProgress" :status="aiGeneratingProgress === 100 ? 'success' : ''" />
            <p>{{ aiGeneratingMessage }}</p>
          </div>
        </div>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="aiGenerateDialogVisible = false">关闭</el-button>
            <el-button type="success" @click="generateContent" :loading="aiGeneratingStatus">生成内容</el-button>
          </span>
        </template>
      </el-dialog>
      
      <div class="publish-header">
        <h2>发布笔记</h2>
        <p>分享你的精彩瞬间和心得体会</p>
      </div>
      
      <el-form 
        ref="formRef"
        :model="publishForm" 
        :rules="formRules" 
        label-position="top"
        class="publish-form"
      >
        <div class="form-layout">
          <!-- 左侧内容区 -->
          <div class="form-left">
            <!-- 标题 -->
            <el-form-item label="标题" prop="title">
              <el-input 
                v-model="publishForm.title" 
                placeholder="请输入标题（2-50个字符）" 
                maxlength="50"
                show-word-limit
              ></el-input>
            </el-form-item>
            
            <!-- 上传图片 -->
            <el-form-item label="添加图片" prop="images">
              <el-upload
                v-model:file-list="fileList"
                action="#"
                list-type="picture-card"
                :auto-upload="false"
                multiple
                :limit="9"
                accept="image/*"
                :on-preview="handlePreview"
                :on-remove="handleRemove"
                :on-exceed="handleExceed"
                :before-upload="beforeUpload"
              >
                <el-icon><Plus /></el-icon>
              </el-upload>
              
              <!-- 图片预览对话框 -->
              <el-dialog v-model="dialogVisible" width="50%" class="image-preview-dialog">
                <div class="image-preview-container">
                  <img :src="dialogImageUrl" alt="Preview Image" class="preview-image" />
                </div>
              </el-dialog>
            </el-form-item>
            
            <!-- 正文内容 -->
            <el-form-item label="正文内容" prop="content">
              <el-input
                v-model="publishForm.content"
                type="textarea"
                :rows="6"
                placeholder="请输入正文内容（10-2000个字符）"
                maxlength="2000"
                show-word-limit
              ></el-input>
            </el-form-item>
          </div>
          
          <!-- 分隔线 -->
          <div class="form-divider"></div>
          
          <!-- 右侧附加内容区 -->
          <div class="form-right">
            <!-- 所属话题 -->
            <el-form-item label="所属话题" prop="topics">
              <el-select
                v-model="publishForm.topics"
                multiple
                filterable
                allow-create
                default-first-option
                placeholder="选择或创建话题（最多3个）"
                :max-collapse-tags="3"
                :multiple-limit="3"
                style="width: 100%;"
              >
                <el-option
                  v-for="item in topicOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                ></el-option>
              </el-select>
            </el-form-item>
            
            <!-- 位置信息 -->
            <el-form-item label="位置信息">
              <el-input
                v-model="publishForm.location"
                placeholder="添加位置信息（可选）"
                prefix-icon="Location"
              ></el-input>
            </el-form-item>
            
            <!-- 权限设置 -->
            <el-form-item label="权限设置">
              <el-select
                v-model="publishForm.permission"
                placeholder="选择可见权限"
                style="width: 100%;"
              >
                <el-option
                  v-for="item in permissionOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                >
                  <div class="permission-option">
                    <el-icon class="permission-icon">
                      <component :is="item.icon"></component>
                    </el-icon>
                    <span>{{ item.label }}</span>
                  </div>
                </el-option>
              </el-select>
            </el-form-item>
          </div>
        </div>
        
        <!-- 操作按钮 -->
        <div class="form-actions">
          <el-button @click="handleCancel">取消</el-button>
          <el-button type="info" @click="saveAsDraft">存为草稿</el-button>
          <el-button type="danger" @click="handlePublish" :loading="submitting">发布</el-button>
        </div>
      </el-form>
    </div>
  </app-layout>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import AppLayout from '@/components/layout/AppLayout.vue'

const router = useRouter()
const formRef = ref(null)
const fileList = ref([])
const dialogImageUrl = ref('')
const dialogVisible = ref(false)
const submitting = ref(false)

// 灵感闪现相关
const inspirationDialogVisible = ref(false)
const inspirationList = [
  "分享一下你最近的旅行中最令你难忘的风景或经历",
  "介绍一道你最拿手的美食和它背后的故事",
  "分享一本近期阅读的好书和它给你带来的启发",
  "记录一次有趣的社交活动或聚会",
  "分享一个生活小技巧，让日常更加便利",
  "记录一次购物体验或分享你最近的心水好物",
  "探讨一部你最近看过的电影或剧集",
  "分享你的健身心得或健康饮食小贴士"
]

// AI一键生成相关
const aiGenerateDialogVisible = ref(false)
const aiGeneratingStatus = ref(false)
const aiGeneratingProgress = ref(0)
const aiGeneratingMessage = ref('正在生成内容...')
const aiGenerateForm = reactive({
  type: 'travel',
  keyword: ''
})

// 显示灵感对话框
const showInspirationDialog = () => {
  inspirationDialogVisible.value = true
}

// 显示AI生成对话框
const showAIGenerateDialog = () => {
  aiGenerateDialogVisible.value = true
}

// AI生成内容
const generateContent = () => {
  aiGeneratingStatus.value = true
  aiGeneratingProgress.value = 0
  aiGeneratingMessage.value = '正在生成内容...'
  
  // 模拟生成进度
  const timer = setInterval(() => {
    aiGeneratingProgress.value += 10
    if (aiGeneratingProgress.value >= 100) {
      clearInterval(timer)
      aiGeneratingMessage.value = '内容生成完成!'
      
      // 根据不同类型生成不同内容
      let generatedTitle = ''
      let generatedContent = ''
      
      switch (aiGenerateForm.type) {
        case 'travel':
          generatedTitle = aiGenerateForm.keyword ? `${aiGenerateForm.keyword}之旅` : '我的精彩旅行记录'
          generatedContent = '这是一次难忘的旅行体验，沿途的风景如画，人文风情独特。当地的美食令人回味无穷，特别是...[AI生成的旅行内容]'
          break
        case 'food':
          generatedTitle = aiGenerateForm.keyword ? `品尝${aiGenerateForm.keyword}的美味体验` : '舌尖上的美食探索'
          generatedContent = '这道美食的色香味俱全，口感丰富多层次。制作过程并不复杂，但需要注意火候的掌控...[AI生成的美食内容]'
          break
        case 'book':
          generatedTitle = aiGenerateForm.keyword ? `《${aiGenerateForm.keyword}》读后感` : '近期阅读心得分享'
          generatedContent = '这本书给我带来了全新的视角和思考，作者通过细腻的笔触描绘了...[AI生成的读书心得]'
          break
        case 'movie':
          generatedTitle = aiGenerateForm.keyword ? `《${aiGenerateForm.keyword}》观后感` : '近期观影推荐'
          generatedContent = '这部影片的剧情设计精妙，演员表演到位，特别是在情感表达方面...[AI生成的影评内容]'
          break
        case 'tech':
          generatedTitle = aiGenerateForm.keyword ? `${aiGenerateForm.keyword}深度评测` : '数码新品体验报告'
          generatedContent = '这款产品的设计和用户体验做得相当出色，功能丰富且实用。在性能方面...[AI生成的评测内容]'
          break
        default:
          generatedTitle = '我的精彩分享'
          generatedContent = '这是AI帮我生成的精彩内容...[AI生成的通用内容]'
      }
      
      // 应用生成的内容
      publishForm.title = generatedTitle
      publishForm.content = generatedContent
      
      // 随机选择相关话题
      const topicMap = {
        travel: '旅行',
        food: '美食',
        book: '读书',
        movie: '电影',
        tech: '数码'
      }
      publishForm.topics = [topicMap[aiGenerateForm.type] || topicOptions[0].value]
      
      // 延迟关闭对话框
      setTimeout(() => {
        aiGenerateDialogVisible.value = false
        aiGeneratingStatus.value = false
        ElMessage({
          message: 'AI内容生成成功',
          type: 'success'
        })
      }, 1000)
    }
  }, 300)
}

// 选择特定灵感
const selectInspiration = (inspiration) => {
  publishForm.title = '灵感：' + inspiration.substring(0, 10) + '...'
  publishForm.content = inspiration
  inspirationDialogVisible.value = false
  ElMessage({
    message: '已应用灵感到内容中',
    type: 'success'
  })
}

// 应用随机灵感
const applyInspiration = () => {
  const randomInspiration = inspirationList[Math.floor(Math.random() * inspirationList.length)]
  publishForm.title = '灵感：' + randomInspiration.substring(0, 10) + '...'
  publishForm.content = randomInspiration
  inspirationDialogVisible.value = false
  ElMessage({
    message: '已应用随机灵感到内容中',
    type: 'success'
  })
}

// 表单数据
const publishForm = reactive({
  title: '',
  content: '',
  images: [],
  topics: [],
  location: '',
  permission: 'public'
})

// 话题选项
const topicOptions = [
  { value: '旅行', label: '旅行' },
  { value: '美食', label: '美食' },
  { value: '穿搭', label: '穿搭' },
  { value: '数码', label: '数码' },
  { value: '读书', label: '读书' },
  { value: '电影', label: '电影' },
  { value: '游戏', label: '游戏' },
  { value: '健身', label: '健身' },
  { value: '宠物', label: '宠物' }
]

// 权限选项
const permissionOptions = [
  { value: 'public', label: '全部可见', icon: 'View' },
  { value: 'friends', label: '粉丝可见', icon: 'User' },
  { value: 'private', label: '仅自己可见', icon: 'Lock' }
]

// 表单验证规则
const formRules = {
  title: [
    { required: true, message: '请输入标题', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入正文内容', trigger: 'blur' },
    { min: 10, max: 2000, message: '长度在 10 到 2000 个字符', trigger: 'blur' }
  ],
  topics: [
    { required: true, message: '请选择至少一个话题', trigger: 'change' }
  ]
}

// 图片预览
const handlePreview = (file) => {
  dialogImageUrl.value = file.url
  dialogVisible.value = true
}

// 移除图片
const handleRemove = (file) => {
  console.log('removed file:', file)
}

// 超出数量限制
const handleExceed = () => {
  ElMessage.warning('最多只能上传9张图片')
}

// 上传前检查
const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt10M = file.size / 1024 / 1024 < 10

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
  }
  
  if (!isLt10M) {
    ElMessage.error('图片大小不能超过 10MB!')
  }
  
  return isImage && isLt10M
}

// 取消发布
const handleCancel = () => {
  ElMessageBox.confirm('确定要放弃当前编辑的内容吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    router.push('/')
  }).catch(() => {})
}

// 保存为草稿
const saveAsDraft = () => {
  ElMessage.success('已保存为草稿')
  // TODO: 实际项目中调用保存草稿API
}

// 发布笔记
const handlePublish = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      if (fileList.value.length === 0) {
        ElMessage.warning('请至少上传一张图片')
        return
      }
      
      submitting.value = true
      
      try {
        // 模拟发布操作
        setTimeout(() => {
          ElMessage.success('发布成功')
          router.push('/')
          submitting.value = false
        }, 1500)
      } catch (error) {
        submitting.value = false
        console.error('发布失败', error)
      }
    } else {
      ElMessage.error('请完善表单信息')
    }
  })
}
</script>

<style lang="scss" scoped>
@import '@/assets/styles/variables.scss';

.publish-container {
  max-width: 1150px;
  margin: 0 auto;
  padding: 20px;
  background-color: $bg-light;
  border-radius: $border-radius;
  box-shadow: $box-shadow;
  position: relative;
  
  @media (max-width: $breakpoint-sm) {
    padding: 15px;
  }
}

.utility-buttons {
  position: absolute;
  top: 20px;
  left: 25px;
  z-index: 10;
  display: flex;
  gap: 10px;
  
  :deep(.el-button) {
    box-shadow: $box-shadow;
    border-radius: 18px;
    padding: 18px 15px;
    font-size: $font-size-small;
    
    &:hover, &:focus {
      transform: translateY(-2px);
      transition: transform 0.3s ease;
    }
  }
}

.inspiration-content {
  padding: 10px;
}

.ai-generate-content {
  padding: 10px;
}

.ai-generate-tip {
  font-size: $font-size-medium;
  color: $text-secondary;
  margin-bottom: 20px;
}

.ai-generating-status {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 20px;
  padding: 20px 0;
  
  p {
    margin-top: 15px;
    color: $text-secondary;
    font-size: $font-size-medium;
  }
}

.inspiration-tip {
  font-size: $font-size-medium;
  color: $text-secondary;
  margin-bottom: 20px;
}

.inspiration-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.inspiration-item {
  display: flex;
  align-items: flex-start;
  padding: 10px 15px;
  background-color: $secondary-color;
  border-radius: $border-radius-small;
  transition: all 0.3s ease;
  cursor: pointer;
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: $box-shadow-light;
  }
}

.inspiration-icon {
  margin-right: 10px;
  color: $primary-color;
  font-size: $font-size-large;
}

.inspiration-text {
  color: $text-primary;
  font-size: $font-size-normal;
  line-height: 1.5;
}

:deep(.el-dialog__header) {
  border-bottom: 1px solid $border-light;
  padding: 15px 20px;
}

:deep(.el-dialog__body) {
  padding: 20px;
}

:deep(.el-dialog__footer) {
  border-top: 1px solid $border-light;
  padding: 15px 20px;
}

.publish-header {
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

.publish-form {
  .el-form-item {
    margin-bottom: 25px;
  }
  
  :deep(.el-form-item__label) {
    font-size: $font-size-medium;
    color: $text-secondary;
  }
}

.form-layout {
  display: flex;
  margin-bottom: $spacing-large;
  
  @media (max-width: $breakpoint-md) {
    flex-direction: column;
  }
}

.form-left {
  flex: 2;
  padding-right: $spacing-large;
  
  @media (max-width: $breakpoint-md) {
    padding-right: 0;
    margin-bottom: $spacing-large;
  }
}

.form-divider {
  width: 1px;
  background-color: $border-light;
  margin: 0 $spacing-large;
  
  @media (max-width: $breakpoint-md) {
    width: 100%;
    height: 1px;
    margin: $spacing-medium 0;
  }
}

.form-right {
  flex: 1;
  padding-left: $spacing-large;
  
  @media (max-width: $breakpoint-md) {
    padding-left: 0;
  }
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 30px;
  
  @media (max-width: $breakpoint-xs) {
    flex-direction: column;
    
    .el-button {
      margin-left: 0 !important;
    }
  }
  
  .el-button {
    min-width: 90px;
    border-radius: 20px;
    
    &:last-child {
      background-color: $primary-color;
      border-color: $primary-color;
      
      &:hover, &:focus {
        background-color: darken($primary-color, 10%);
        border-color: darken($primary-color, 10%);
      }
    }
  }
}

:deep(.el-upload--picture-card) {
  width: 120px;
  height: 120px;
  line-height: 120px;
  border-color: $border-light;
  border-radius: $border-radius-small;
  
  &:hover {
    border-color: $primary-color;
  }
  
  @media (max-width: $breakpoint-xs) {
    width: 100px;
    height: 100px;
    line-height: 100px;
  }
}

:deep(.el-upload-list--picture-card .el-upload-list__item) {
  width: 120px;
  height: 120px;
  border-radius: $border-radius-small;
  
  @media (max-width: $breakpoint-xs) {
    width: 100px;
    height: 100px;
  }
}

:deep(.el-textarea__inner) {
  border-radius: $border-radius-small;
  &:focus {
    border-color: $primary-color;
  }
}

:deep(.el-select .el-input__inner) {
  border-radius: $border-radius-small;
}

:deep(.el-radio) {
  margin-right: 25px;
  margin-bottom: 10px;
  
  @media (max-width: $breakpoint-xs) {
    margin-right: 15px;
  }
}

:deep(.el-radio__input.is-checked + .el-radio__label) {
  color: $primary-color;
}

:deep(.el-upload__tip) {
  font-size: $font-size-small;
  color: $text-light;
}

.permission-option {
  display: flex;
  align-items: center;
  padding: 2px 0;
}

.permission-icon {
  margin-right: 8px;
  font-size: $font-size-medium;
  color: $primary-color;
}

.image-preview-dialog {
  .image-preview-container {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100%;
  }

  .preview-image {
    max-width: 100%;
    max-height: 100%;
    object-fit: contain;
  }
}
</style>