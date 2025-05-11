<template>
  <app-layout>
    <div class="publish-container">
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
        <div class="utility-buttons">
          <el-button type="info" size="default" icon="Lightning" @click="showInspirationDialog" class="utility-btn inspiration-btn">AI润色</el-button>
          <el-button type="primary" size="default" icon="ChatDotRound" @click="showAIGenerateDialog" class="utility-btn ai-btn">AI一键生成</el-button>
        </div>
        <!-- 灵感闪现对话框 -->
        <el-dialog
          v-model="inspirationDialogVisible"
          title="AI润色"
          width="500px"
        >
          <!-- 分析中状态 -->
          <div v-if="isAnalyzing" class="analyzing-status">
            <el-progress type="circle" :percentage="getAnalysisProgress(aiAnalysisStep)" :status="aiAnalysisStep === '分析失败' ? 'exception' : ''" />
            <p>{{ aiAnalysisStep }}</p>
            <p class="analyzing-tip" v-if="aiAnalysisStep.includes('上传图片')">正在上传图片，请稍候...</p>
            <p class="analyzing-tip" v-else-if="aiAnalysisStep.includes('分析内容')">正在分析内容，这可能需要几秒钟...</p>
            <p class="analyzing-tip" v-else-if="aiAnalysisStep.includes('生成润色')">正在生成润色建议，即将完成...</p>
          </div>
          
          <!-- 分析结果 -->
          <div v-else-if="analysisResult" class="analysis-result">
            <!-- 图片建议部分 -->
            <div class="analysis-section" v-if="analysisResult.imageSuggestion || analysisResult.imageDescription" style="margin-top: -10px;">
              <h4>图片建议</h4>
              <div class="analysis-card">
                <el-icon class="analysis-icon"><Picture /></el-icon>
                <p>
                  <span v-if="fileList.length > 0">当前图片：{{ analysisResult.imageDescription }}</span>
                  <span v-else>您尚未上传图片。</span>
                  <br><br>
                  <span class="image-suggestion" v-if="analysisResult.imageSuggestion">
                    <strong>优化建议：</strong> 
                    {{ analysisResult.imageSuggestion }}
                  </span>
                  <span class="image-suggestion" v-else>
                    <strong>优化建议：</strong> 
                    {{ getImageSuggestion(analysisResult.imageDescription) }}
                  </span>
                </p>
              </div>
            </div>
            
            <!-- 文本内容分析部分 -->
            <div class="analysis-section" v-if="analysisResult.contentTheme || analysisResult.contentSuggestion">
              <h4>内容分析建议</h4>
              <div class="analysis-card">
                <el-icon class="analysis-icon"><Document /></el-icon>
                <p>
                  <span v-if="analysisResult.contentTheme">{{ analysisResult.contentTheme }}</span>
                  <span v-if="analysisResult.contentSuggestion" class="content-suggestion">
                    <strong>优化建议：</strong> 
                    {{ analysisResult.contentSuggestion }}
                  </span>
                </p>
              </div>
            </div>
            
            <!-- 标题优化建议 -->
            <div class="analysis-section" v-if="analysisResult.titleSuggestion">
              <h4>标题优化建议</h4>
              <div class="analysis-card">
                <el-icon class="analysis-icon"><EditPen /></el-icon>
                <p>{{ analysisResult.titleSuggestion }}</p>
              </div>
            </div>
            
            <!-- 话题标签建议 -->
            <div class="analysis-section" v-if="analysisResult.suggestedTags && analysisResult.suggestedTags.length > 0" style="margin-bottom: 0;">
              <h4>推荐话题</h4>
              <div class="tags-container">
                <el-tag 
                  v-for="(tag, index) in analysisResult.suggestedTags" 
                  :key="index"
                  class="suggested-tag"
                  @click="addTagToTopics(tag)"
                >
                  {{ tag }}
                </el-tag>
              </div>
            </div>
          </div>
          
          <!-- 默认灵感列表 -->
          <div v-else class="inspiration-content">
            <p class="inspiration-tip">还没有内容可以分析，你可以先从以下灵感开始：</p>
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
              <el-button v-if="!analysisResult && !isAnalyzing" type="primary" @click="selectRandomInspiration">随机灵感</el-button>
              <el-button v-if="analysisResult && !isAnalyzing" type="success" @click="applyAnalysisResult">应用润色建议</el-button>
            </span>
          </template>
        </el-dialog>
        
        <!-- AI一键生成对话框 -->
        <el-dialog
          v-model="aiGenerateDialogVisible"
          title="AI一键生成"
          width="600px"
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
            
            <!-- 生成结果显示区域 -->
            <div v-if="aiGenerateResponse && !aiGeneratingStatus" class="ai-generated-result">
              <el-divider></el-divider>
              
              <div class="result-preview">
                <h3>{{ aiGenerateResponse.title }}</h3>
                
                <!-- 图片建议部分 -->
                <div v-if="imagePromptVisible && aiGenerateResponse.imagePrompt" class="image-prompt-section">
                  <div class="image-prompt-content">
                    <div class="custom-prompt-box">
                      <div class="prompt-header">图片建议：</div>
                      <div class="prompt-body">{{ aiGenerateResponse.imagePrompt }}</div>
                    </div>
                  </div>
                </div>
                
                <div class="content-preview">
                  <div class="preview-text-container">
                    <p class="preview-text">{{ aiGenerateResponse.content }}</p>
                  </div>
                </div>
              </div>
              
              <el-button 
                type="success" 
                @click="aiGenerateDialogVisible = false" 
                style="width: 100%; margin-top: 15px;">
                应用内容并关闭
              </el-button>
            </div>
          </div>
          <template #footer>
            <span class="dialog-footer">
              <el-button @click="aiGenerateDialogVisible = false">关闭</el-button>
              <el-button 
                type="primary" 
                @click="generateContent" 
                :loading="aiGeneratingStatus" 
                :disabled="aiGeneratingStatus || (aiGenerateResponse !== null)" 
                style="background-color: #7db0e8; border-color: #7db0e8;">
                生成内容
              </el-button>
            </span>
          </template>
        </el-dialog>
        
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
          <el-button type="success" @click="handlePublish" :loading="submitting">发布</el-button>
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
const isAnalyzing = ref(false)
const analysisResult = ref(null)
const aiAnalysisStep = ref('准备分析')
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
const aiGenerateResponse = ref(null)
const imagePromptVisible = ref(false)

// 根据图片内容生成优化建议
const getImageSuggestion = (imageDescription) => {
  if (!imageDescription) return '请上传一张清晰的相关图片，以获取更好的分析效果。';
  
  // 针对不同类型的图片给出不同建议
  if (imageDescription.includes('猫') || imageDescription.includes('狗') || imageDescription.includes('宠物')) {
    return '建议使用高清、明亮的照片展示宠物的特写，尤其是眼睛和有趣的动作。可以添加文字标注突出宠物的个性或有趣表情，如"awsl"或"铲屎官被征服"等互动性标签。';
  } else if (imageDescription.includes('风景') || imageDescription.includes('建筑') || imageDescription.includes('景点')) {
    return '建议选择光线充足、构图平衡的照片。可以尝试不同角度拍摄同一景点，突出与众不同的视角。添加地点标签和拍摄时间可以增加内容真实感。';
  } else if (imageDescription.includes('食物') || imageDescription.includes('美食') || imageDescription.includes('餐')) {
    return '美食照片建议使用自然光线拍摄，突出食物的色泽和质感。可以添加简短的制作过程或食材信息，增加专业感。考虑从俯视角度拍摄以展示全貌。';
  } else if (imageDescription.includes('人') || imageDescription.includes('自拍') || imageDescription.includes('合影')) {
    return '人物照片建议使用柔和光线，注意背景简洁干净。可以尝试自然的生活场景而非刻意摆拍，展现真实情感。考虑添加简洁文字说明当时的心情或场景。';
  } else if (imageDescription.includes('电子') || imageDescription.includes('设备') || imageDescription.includes('数码')) {
    return '数码产品照片建议使用中性背景突出产品本身，保持画面整洁。可以展示产品使用场景或特写功能部分。建议添加简短的使用心得或技巧分享。';
  } else {
    // 通用建议
    return '建议使用高清、构图精美的照片，保持主题突出、背景简洁。可以考虑添加文字说明或特效标签增强表现力，与正文内容保持一致性。尝试从不同角度展示主体，突出特点。';
  }
}

// 显示灵感对话框
const showInspirationDialog = async () => {
  // 检查是否有已填写的内容
  if (!publishForm.title && !publishForm.content && fileList.value.length === 0) {
    inspirationDialogVisible.value = true
    return
  }
  
  // 如果用户已经填写了部分内容，尝试使用AI分析
  try {
    isAnalyzing.value = true
    inspirationDialogVisible.value = true
    aiAnalysisStep.value = '准备分析'
    
    // 准备图片URL和请求数据
    let imageDescription = ''
    
    // 如果有图片，先尝试上传获取URL
    if (fileList.value.length > 0) {
      // 对于外部URL，直接使用
      if (fileList.value[0].url && fileList.value[0].url.startsWith('http')) {
        imageDescription = fileList.value[0].url
        console.log('使用现有图片URL:', imageDescription)
        aiAnalysisStep.value = '使用已有图片URL'
      } 
      // 对于本地文件，先上传到服务器
      else if (fileList.value[0].raw) {
        console.log('上传本地图片并分析')
        aiAnalysisStep.value = '正在上传图片(1/3)'
        
        try {
          // 创建FormData对象
          const formData = new FormData()
          formData.append('file', fileList.value[0].raw)
          
          // 上传图片 - 不设置任何自定义头，让浏览器自动处理
          const uploadResponse = await fetch('http://localhost:8080/api/upload/image', {
            method: 'POST',
            body: formData
          })
          
          if (!uploadResponse.ok) {
            console.error('上传失败状态码:', uploadResponse.status)
            throw new Error(`图片上传失败: ${uploadResponse.status} ${uploadResponse.statusText}`)
          }
          
          const uploadResult = await uploadResponse.json()
          console.log('上传结果:', uploadResult)
          
          if (uploadResult.code === 200 && uploadResult.data) {
            imageDescription = uploadResult.data
            console.log('图片上传成功，URL:', imageDescription)
            
            // 添加一个小延迟，确保图片已经被服务器保存并可以被访问
            aiAnalysisStep.value = '等待图片处理(1.5/3)'
            await new Promise(resolve => setTimeout(resolve, 1000))

            // 检查图片URL是否可访问
            try {
              const testResponse = await fetch(imageDescription, { method: 'HEAD' })
              if (!testResponse.ok) {
                console.warn('图片URL似乎不可访问，这可能导致分析失败:', testResponse.status)
                ElMessage.warning('上传的图片可能无法被AI正确识别，分析结果可能不准确')
              }
            } catch (e) {
              console.warn('检查图片URL可访问性失败:', e)
            }
            
          } else {
            throw new Error(uploadResult.message || '图片上传失败')
          }
        } catch (error) {
          console.error('图片上传错误:', error)
          ElMessage.error('图片上传失败: ' + error.message)
          isAnalyzing.value = false
          return
        }
      }
    } else {
      console.log('没有图片，将仅分析文本内容')
    }
    
    // 准备请求数据
    const requestData = {
      title: publishForm.title || "无标题",
      content: publishForm.content || "无内容",
      image_url: imageDescription, // 使用上传后的URL
      prompt: "分析内容并给出润色建议"
    }
    
    console.log('发送AI润色请求:', requestData)
    aiAnalysisStep.value = '正在分析内容(2/3)'
    
    // 调用后端API
    try {
      // 添加详细日志，帮助调试
      console.log('准备发送分析请求，数据:', JSON.stringify(requestData))
      
      const response = await fetch('http://localhost:8080/api/userInput', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(requestData),
        credentials: 'include' // 确保包含凭证
      })
      
      console.log('分析请求状态:', response.status, response.statusText)
      
      if (!response.ok) {
        console.error('分析请求失败:', response.status, response.statusText)
        throw new Error(`内容分析失败: ${response.status} ${response.statusText}`)
      }
      
      // 解析返回的JSON结果
      const resultText = await response.text()
      console.log('分析返回原始数据:', resultText)
      
      let result
      try {
        result = JSON.parse(resultText)
      } catch (e) {
        console.error('解析返回JSON失败:', e)
        throw new Error('返回数据格式错误')
      }
      
      console.log('分析解析结果:', result)
      
      aiAnalysisStep.value = '生成润色建议(3/3)'
      
      if (result.code === 200) {
        // 即使data为null也不抛出错误
        if (result.data) {
          analysisResult.value = result.data
          console.log('分析结果数据:', analysisResult.value)
          
          // 记录详细的分析结果信息
          if (analysisResult.value.imageDescription) {
            console.log('图片描述:', analysisResult.value.imageDescription)
          }
          if (analysisResult.value.contentTheme) {
            console.log('文本内容分析:', analysisResult.value.contentTheme)
          }
          
          aiAnalysisStep.value = '分析完成'
          ElMessage({
            message: 'AI内容分析完成',
            type: 'success'
          })
        } else {
          // 处理data为null的情况
          console.warn('API返回data为null')
          aiAnalysisStep.value = '分析完成但无结果'
          
          // 创建一个基本的分析结果对象
          analysisResult.value = {
            imageDescription: "色泽诱人的中式餐盒，包含嫩滑的鸡肉、翠绿的青菜和浓香的酱汁",
            contentTheme: "内容主题分析暂时不可用",
            contentSuggestion: "AI服务暂时无法提供详细分析，请稍后再试。", 
            titleSuggestion: "标题看起来不错，可以继续使用。",
            suggestedTags: ["美食", "分享", "推荐"]
          }
          
          ElMessage({
            message: 'AI内容分析服务暂时不可用，请稍后再试',
            type: 'warning'
          })
        }
      } else {
        console.error('API返回错误:', result.message || '未知错误')
        throw new Error(result.message || '内容分析失败')
      }
    } catch (error) {
      console.error('请求或解析错误:', error)
      throw error // 向上层传递错误
    }
  } catch (error) {
    console.error('AI润色错误:', error)
    analysisResult.value = null
    aiAnalysisStep.value = '分析失败'
    ElMessage({
      message: '内容分析失败: ' + error.message,
      type: 'warning'
    })
  } finally {
    isAnalyzing.value = false
  }
}

// 添加推荐标签到话题
const addTagToTopics = (tag) => {
  // 检查是否已经有这个标签
  if (!publishForm.topics.includes(tag)) {
    // 检查是否超过最大限制
    if (publishForm.topics.length < 3) {
      publishForm.topics.push(tag);
      ElMessage({
        message: `已添加"${tag}"到话题`,
        type: 'success'
      });
    } else {
      ElMessage({
        message: '最多只能选择3个话题',
        type: 'warning'
      });
    }
  } else {
    ElMessage({
      message: `话题"${tag}"已存在`,
      type: 'info'
    });
  }
}

// 应用AI润色结果
const applyAnalysisResult = () => {
  if (!analysisResult.value) return
  
  // 应用优化后的标题（如果有）
  if (analysisResult.value.optimizedTitle) {
    publishForm.title = analysisResult.value.optimizedTitle;
  } else if (!publishForm.title.trim() && analysisResult.value.imageDescription) {
    // 如果标题为空但有图片描述，将图片描述设为标题
    publishForm.title = `关于${analysisResult.value.imageDescription}的分享`;
  }
  
  // 应用优化后的内容（如果有）
  if (analysisResult.value.optimizedContent) {
    publishForm.content = analysisResult.value.optimizedContent;
  } else if (analysisResult.value.contentTheme) {
    // 如果有内容主题分析，将其添加到内容开头
    const contentPrefix = `主题：${analysisResult.value.contentTheme}\n\n`;
    publishForm.content = contentPrefix + publishForm.content;
  }
  
  // 应用推荐的话题标签
  if (analysisResult.value.suggestedTags && analysisResult.value.suggestedTags.length > 0) {
    // 最多选择3个话题
    publishForm.topics = analysisResult.value.suggestedTags.slice(0, 3);
  } else {
    // 根据分析结果推断话题标签
    if (analysisResult.value.contentTheme) {
      const topicKeywords = {
        '旅行': ['旅行', '旅游', '景点', '风景', '游记'],
        '美食': ['美食', '食物', '菜', '餐厅', '吃'],
        '读书': ['书', '阅读', '文学', '小说'],
        '电影': ['电影', '影视', '剧', '电视'],
        '数码': ['数码', '科技', '手机', '电子'],
        '穿搭': ['穿搭', '服装', '时尚', '衣服'],
        '游戏': ['游戏', '玩'],
        '健身': ['健身', '运动', '锻炼'],
        '宠物': ['宠物', '猫', '狗', '动物']
      };
      
      const theme = analysisResult.value.contentTheme.toLowerCase();
      const matchedTopics = [];
      
      // 检查主题中是否包含关键词
      for (const [topic, keywords] of Object.entries(topicKeywords)) {
        if (keywords.some(keyword => theme.includes(keyword))) {
          matchedTopics.push(topic);
        }
      }
      
      // 如果找到匹配的话题，更新表单
      if (matchedTopics.length > 0) {
        publishForm.topics = matchedTopics.slice(0, 3); // 最多3个话题
      }
    }
  }
  
  inspirationDialogVisible.value = false;
  analysisResult.value = null;
  
  ElMessage({
    message: 'AI优化内容已应用',
    type: 'success'
  });
}

// 显示AI生成对话框
const showAIGenerateDialog = () => {
  aiGenerateDialogVisible.value = true
  aiGenerateResponse.value = null
  imagePromptVisible.value = false
}

// 调用后端AI生成内容API
const generateContent = async () => {
  aiGeneratingStatus.value = true
  aiGeneratingProgress.value = 0
  aiGeneratingMessage.value = '正在生成内容...'
  
  const timer = setInterval(() => {
    if (aiGeneratingProgress.value < 90) {
      aiGeneratingProgress.value += 10
    }
  }, 300)
  
  try {
    const contentTypeMap = {
      'travel': 'travel',
      'food': 'food',
      'book': 'book',
      'movie': 'movie',
      'tech': 'tech'
    }
    
    console.log('发送请求到:', 'http://localhost:8080/api/ai/generate')
    console.log('请求内容:', {
      contentType: contentTypeMap[aiGenerateForm.type],
      keyword: aiGenerateForm.keyword
    })
    
    const response = await fetch('http://localhost:8080/api/ai/generate', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        contentType: contentTypeMap[aiGenerateForm.type],
        keyword: aiGenerateForm.keyword
      })
    })
    
    if (!response.ok) {
      throw new Error(`HTTP错误: ${response.status} ${response.statusText}`)
    }
    
    const result = await response.json()
    console.log('API返回结果:', result)
    
    // 添加日志输出，显示图片建议内容
    console.log('图片建议内容:', result.data?.imagePrompt || '无图片建议')
    console.log('图片建议长度:', result.data?.imagePrompt?.length || 0)
    
    clearInterval(timer)
    aiGeneratingProgress.value = 100
    aiGeneratingMessage.value = '内容生成完成!'
    
    if (result.code === 200 && result.data) {
      aiGenerateResponse.value = result.data
      
      publishForm.title = result.data.title
      publishForm.content = result.data.content
      
      imagePromptVisible.value = true
      
      const topicMap = {
        travel: '旅行',
        food: '美食',
        book: '读书',
        movie: '电影',
        tech: '数码'
      }
      publishForm.topics = [topicMap[aiGenerateForm.type] || topicOptions[0].value]
      
      ElMessage({
        message: 'AI内容生成成功',
        type: 'success'
      })
    } else {
      throw new Error(result.message || '内容生成失败')
    }
  } catch (error) {
    clearInterval(timer)
    aiGeneratingProgress.value = 100
    aiGeneratingMessage.value = '生成失败: ' + error.message
    console.error('AI内容生成错误:', error)
    
    ElMessage({
      message: '内容生成失败: ' + error.message,
      type: 'error'
    })
  } finally {
    aiGeneratingStatus.value = false
  }
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
const selectRandomInspiration = () => {
  const randomInspiration = inspirationList[Math.floor(Math.random() * inspirationList.length)]
  selectInspiration(randomInspiration)
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
    return false
  }
  
  if (!isLt10M) {
    ElMessage.error('图片大小不能超过 10MB!')
    return false
  }
  
  // 将文件保存起来，但不自动上传
  return true
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

// 根据分析步骤获取进度百分比
const getAnalysisProgress = (step) => {
  switch (step) {
    case '准备分析':
      return 10;
    case '使用已有图片URL':
      return 30;
    case '正在上传图片(1/3)':
      return 20;
    case '等待图片处理(1.5/3)':
      return 30;
    case '图片上传成功':
      return 30;
    case '正在分析内容(2/3)':
      return 60;
    case '生成润色建议(3/3)':
      return 90;
    case '分析完成':
      return 100;
    case '分析失败':
      return 100;
    default:
      return 50;
  }
}
</script>

<style lang="scss" scoped>
@use '@/assets/styles/variables.scss' as *;

.publish-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px 40px 20px;
  background: none;
  border-radius: 0;
  box-shadow: none;
  border: none;
}

.publish-header {
  text-align: center;
  margin-bottom: 36px;
}

.publish-header h2 {
  font-size: 2.2rem;
  color: #222;
  font-weight: 700;
  letter-spacing: 1px;
  margin-bottom: 8px;
}

.publish-header p {
  color: #7db0e8;
  font-size: 1.1rem;
  letter-spacing: 0.5px;
}

.publish-form {
  background: #fff;
  padding: 40px 32px;
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(125, 176, 232, 0.10), 0 1.5px 8px rgba(0,0,0,0.04);
  border: 1.5px solid #f0f4fa;
}

.form-layout {
  display: flex;
  gap: 40px;
}

.form-left {
  flex: 2;
}

.form-right {
  flex: 1;
}

.form-divider {
  width: 1.5px;
  background: linear-gradient(180deg, #e3eaf2 0%, #f7fafd 100%);
  border-radius: 1px;
}

:deep(.el-input__wrapper), :deep(.el-select .el-input__wrapper), :deep(.el-select .el-input__inner), :deep(.el-textarea__inner) {
  border-radius: 14px !important;
  box-shadow: 0 2px 8px rgba(125, 176, 232, 0.08);
  border: 1.5px solid #e3eaf2;
  background: #f7fafd;
  transition: box-shadow 0.2s, border-color 0.2s;
}
:deep(.el-input__wrapper:hover), :deep(.el-select .el-input__wrapper:hover), :deep(.el-textarea__inner:hover) {
  border-color: #7db0e8;
  box-shadow: 0 4px 16px rgba(125, 176, 232, 0.13);
}
:deep(.el-input__wrapper.is-focus), :deep(.el-select .el-input__wrapper.is-focus), :deep(.el-textarea__inner:focus) {
  border-color: #4a90e2;
  box-shadow: 0 0 0 2px #b3d6f7;
}

:deep(.el-select-dropdown), :deep(.el-select-dropdown__wrap) {
  border-radius: 14px !important;
  box-shadow: 0 8px 32px rgba(125, 176, 232, 0.13);
}

.form-actions {
  margin-top: 36px;
  display: flex;
  justify-content: center;
  gap: 20px;
}
:deep(.el-button) {
  border-radius: 14px !important;
  padding: 12px 28px;
  font-size: 1.08rem;
  font-weight: 600;
  box-shadow: 0 2px 8px rgba(125, 176, 232, 0.08);
  transition: background 0.2s, box-shadow 0.2s, border-color 0.2s;
}
:deep(.el-button--success) {
  background: linear-gradient(90deg, #7db0e8 0%, #4a90e2 100%);
  border: none;
}
:deep(.el-button--success:hover) {
  background: linear-gradient(90deg, #4a90e2 0%, #7db0e8 100%);
  box-shadow: 0 4px 16px rgba(125, 176, 232, 0.13);
}
:deep(.el-button--info) {
  background: #f7fafd;
  color: #4a90e2;
  border: 1.5px solid #b3d6f7;
}
:deep(.el-button--info:hover) {
  background: #e3eaf2;
  color: #4a90e2;
  border-color: #7db0e8;
}
:deep(.el-button--primary) {
  background: #4a90e2;
  border: none;
}
:deep(.el-button--primary:hover) {
  background: #7db0e8;
}
:deep(.el-button--default) {
  background: #fff;
  color: #222;
  border: 1.5px solid #e3eaf2;
}
:deep(.el-button--default:hover) {
  background: #f7fafd;
  border-color: #7db0e8;
}

:deep(.el-upload--picture-card) {
  border-radius: 14px;
  border: 1.5px dashed #e3eaf2;
  background: #f7fafd;
  transition: border-color 0.2s, box-shadow 0.2s;
}
:deep(.el-upload--picture-card:hover) {
  border-color: #7db0e8;
  box-shadow: 0 2px 8px rgba(125, 176, 232, 0.13);
}
:deep(.el-dialog) {
  border-radius: 18px;
  box-shadow: 0 8px 32px rgba(125, 176, 232, 0.13);
}
:deep(.el-dialog__header) {
  padding: 24px 24px 12px 24px;
  border-bottom: 1px solid #f0f4fa;
}
:deep(.el-dialog__body) {
  padding: 32px 24px 10px 24px;
}
:deep(.el-dialog__footer) {
  padding: 10px 24px;
  border-top: 1px solid #f0f4fa;
}

.inspiration-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 18px;
  margin-top: 20px;
}
.inspiration-item {
  display: flex;
  align-items: center;
  padding: 14px;
  background: #f7fafd;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
  border: 1.5px solid #e3eaf2;
}
.inspiration-item:hover {
  background: #e3eaf2;
  transform: translateY(-2px) scale(1.03);
  box-shadow: 0 2px 8px rgba(125, 176, 232, 0.13);
}
.inspiration-icon {
  margin-right: 12px;
  color: #7db0e8;
  font-size: 1.3em;
}
.permission-option {
  display: flex;
  align-items: center;
  gap: 8px;
}
.permission-icon {
  font-size: 16px;
  color: #7db0e8;
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

.inspiration-tip {
  font-size: $font-size-medium;
  color: $text-secondary;
  margin-bottom: 20px;
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

.utility-buttons {
  display: flex;
  gap: 16px;
  justify-content: flex-start;
  margin-bottom: 24px;
  margin-top: 0;
  padding-left: 0;
}

.utility-btn {
  height: 44px !important;
  padding: 0 20px !important;
  font-size: 0.95rem !important;
  border-radius: 14px !important;
  box-shadow: 0 4px 8px rgba(125, 176, 232, 0.12) !important;
  transition: all 0.3s !important;
  
  :deep(.el-icon) {
    font-size: 1.1rem;
    margin-right: 6px;
  }
}

.inspiration-btn {
  background: #f0f7ff !important;
  color: #4a90e2 !important;
  border: 1.5px solid #b3d6f7 !important;
  
  &:hover {
    background: #e3eaf2 !important;
    transform: translateY(-2px);
    box-shadow: 0 6px 12px rgba(125, 176, 232, 0.18) !important;
  }
}

.ai-btn {
  background: linear-gradient(90deg, #7db0e8 0%, #4a90e2 100%) !important;
  border: none !important;
  color: white !important;
  
  &:hover {
    background: linear-gradient(90deg, #4a90e2 0%, #7db0e8 100%) !important;
    transform: translateY(-2px);
    box-shadow: 0 6px 12px rgba(125, 176, 232, 0.22) !important;
  }
}

@media (max-width: 700px) {
  .utility-buttons {
    justify-content: center;
    padding-left: 0;
  }
}

/* AI生成相关样式 */
.ai-generating-status {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px 0;
  text-align: center;
}

.ai-generating-status p {
  margin-top: 15px;
  color: #409EFF;
  font-size: 14px;
  text-align: center;
}

.ai-generated-result {
  margin-top: 20px;
  text-align: center;
}

.result-preview {
  background-color: #f9f9f9;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 15px;
  text-align: center;
}

.result-preview h3 {
  margin-top: 0;
  margin-bottom: 5px;
  color: #333;
  font-size: 16px;
  font-weight: 600;
  text-align: center;
}

.content-preview {
  margin-top: 10px;
  text-align: center;
}

.preview-text-container {
  max-height: 200px;
  overflow-y: auto;
  padding: 5px 10px;
  border-radius: 6px;
  background-color: #f9f9f9;
  margin-top: 10px;
  border: 1px solid #eee;
}

.preview-text {
  margin: 0;
  text-align: left;
  font-size: 14px;
  line-height: 1.6;
  color: #555;
  white-space: pre-wrap;
  word-break: break-word;
}

/* 自定义滚动条样式 */
.preview-text-container::-webkit-scrollbar {
  width: 6px;
}

.preview-text-container::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.preview-text-container::-webkit-scrollbar-thumb {
  background: #c8d8ea;
  border-radius: 3px;
}

.preview-text-container::-webkit-scrollbar-thumb:hover {
  background: #7db0e8;
}

.image-prompt-section {
  margin: 10px 0;
  text-align: center;
}

.image-prompt-content {
  padding: 0;
  max-width: 90%;
  margin: 0 auto;
}

.custom-prompt-box {
  background-color: #f0f7ff;
  border-radius: 8px;
  padding: 12px 15px;
  border: 1px solid #b3d6f7;
  width: 100%;
  margin: 0 auto;
  text-align: left;
}

.prompt-header {
  font-size: 15px;
  font-weight: 600;
  color: #4a90e2;
  margin-bottom: 8px;
  text-align: left;
  display: inline-block;
}

.prompt-body {
  margin: 0;
  padding: 0;
  text-align: left;
  font-size: 14px;
  line-height: 1.6;
  color: #555;
}

/* AI润色相关样式 */
.analyzing-status {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px 0;
  text-align: center;
}

.analyzing-status p {
  margin-top: 15px;
  color: #4a90e2;
  font-size: 14px;
}

.analyzing-tip {
  color: #666;
  font-size: 13px;
  margin-top: 5px;
}

.analysis-result {
  padding: 20px 10px;
  margin-top: -20px; /* 减少与分割线的距离 */
}

.analysis-result h3 {
  text-align: center;
  font-size: 18px;
  color: #4a90e2;
  margin-bottom: 20px;
  font-weight: 600;
}

.analysis-section {
  margin-bottom: 20px;
}

.analysis-section h4 {
  font-size: 15px;
  color: #555;
  margin-bottom: 10px;
  font-weight: 600;
}

.analysis-card {
  background-color: #f7fafd;
  border-radius: 10px;
  padding: 15px;
  box-shadow: 0 2px 8px rgba(125, 176, 232, 0.1);
  display: flex;
  align-items: flex-start;
  gap: 12px;
  border: 1px solid #e3eaf2;
}

.suggestion-card {
  background-color: #f0f7ff;
  border: 1px solid #b3d6f7;
}

.analysis-icon {
  font-size: 18px;
  color: #4a90e2;
  margin-top: 2px;
}

.analysis-card p {
  margin: 0;
  font-size: 14px;
  line-height: 1.6;
  color: #333;
  flex: 1;
}

.image-suggestion {
  display: block;
  margin-top: 5px;
  color: #4a90e2;
  background-color: #f0f7ff;
  padding: 8px 12px;
  border-radius: 6px;
  border-left: 3px solid #7db0e8;
}

.image-suggestion strong {
  color: #4a90e2;
  font-weight: 600;
}

.content-suggestion {
  display: block;
  margin-top: 8px;
  color: #4a90e2;
  background-color: #f0f7ff;
  padding: 8px 12px;
  border-radius: 6px;
  border-left: 3px solid #7db0e8;
}

.tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 10px;
  margin-bottom: 0;
}

.suggested-tag {
  cursor: pointer;
  background-color: #f0f7ff;
  border-color: #b3d6f7;
  color: #4a90e2;
  font-size: 13px;
  padding: 6px 12px;
  transition: all 0.2s;
}

.suggested-tag:hover {
  background-color: #4a90e2;
  color: white;
  transform: translateY(-2px);
  box-shadow: 0 2px 5px rgba(74, 144, 226, 0.3);
}
</style>