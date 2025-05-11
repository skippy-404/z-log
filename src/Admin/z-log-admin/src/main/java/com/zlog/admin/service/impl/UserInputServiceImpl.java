package com.zlog.admin.service.impl;

import okhttp3.*;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import java.io.IOException;

import com.zlog.admin.entity.FilterByAI;
import com.zlog.admin.entity.UserInput;
import com.zlog.admin.service.UserInputService;
import org.apache.catalina.User;
import org.springframework.stereotype.Service;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Service
public class UserInputServiceImpl implements UserInputService {
    @Override
    public FilterByAI totalFilter(UserInput userInput) {
        try {
            // 防止空值异常
            if (userInput == null) {
                System.err.println("警告: userInput为null");
                return new FilterByAI("无图片信息", "无内容分析", "无法分析主题");
            }
            
            // 1. 如果有图片URL，先通过OpenAI分析图片内容
            String imageDescription = "";
            if (userInput.getImage_url() != null && !userInput.getImage_url().isEmpty()) {
                imageDescription = OpenAIfliter(userInput);
                System.out.println("图片分析结果: " + imageDescription);
            } else {
                System.out.println("没有图片URL，跳过图片分析");
                imageDescription = "没有图片信息";
            }
            
            // 2. 直接生成结构化分析结果（一步到位）
            return generateStructuredAnalysis(userInput, imageDescription);
        } catch (Exception e) {
            System.err.println("totalFilter方法异常: " + e.getMessage());
            e.printStackTrace();
            return new FilterByAI("处理异常", "处理异常", "处理过程中发生错误");
        }
    }
    
    /**
     * 生成结构化分析结果（直接一步到位）
     * @param userInput 用户输入对象
     * @param imageDescription 图片描述（如有）
     * @return 结构化的分析结果
     */
    private FilterByAI generateStructuredAnalysis(UserInput userInput, String imageDescription) {
        String DEEPSEEK_API_URL = "https://api.deepseek.com/v1/chat/completions";
        String API_KEY = "sk-1a44e77dbbcf4331844c6dbfc3ed2ad1";
        
        // 防止null值引起的错误
        String title = userInput.getTitle() != null ? userInput.getTitle() : "无标题";
        String content = userInput.getContent() != null ? userInput.getContent() : "无内容";
        String imageUrl = userInput.getImage_url() != null ? userInput.getImage_url() : "";
        
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS)
                .writeTimeout(100, TimeUnit.SECONDS)
                .build();
        
        // 构造更详细的提示，要求返回结构化的JSON结果
        String prompt;
        if (imageDescription != null && !imageDescription.isEmpty() && !"没有图片信息".equals(imageDescription)) {
            // 有图片描述时的提示
            prompt = String.format("""
                    你是小红书内容优化专家。请基于以下内容提供优化建议。
                    
                    标题：%s
                    内容：%s
                    图片描述：%s
                    
                    要求：
                    1. 返回一个完整的小红书风格内容，使用活泼、真诚的语气。
                    2. 必须在优化内容中合理添加emoji表情，每段至少使用1-2个相关emoji。
                    3. 内容必须包含精美排版，包括适当的分段、空行、标题和强调。
                    4. 优化内容的长度应该适中，不少于200字，确保内容丰富但不冗长。
                    5. 加入一些互动元素，如提问、邀请评论等增加互动性。
                    
                    请提供以下格式的JSON分析结果：
                    {
                      "imageDescription": "简要描述图片内容", 
                      "contentTheme": "对文章内容的主题分析",
                      "imageSuggestion": "对图片的优化建议，包括构图、清晰度、元素等方面",
                      "contentSuggestion": "对内容的优化建议，提高吸引力和互动性",
                      "titleSuggestion": "对标题的优化建议，使其更吸引眼球",
                      "optimizedTitle": "完整的优化后标题（加入emoji和吸引力元素）",
                      "optimizedContent": "完整的小红书风格优化内容（包含emoji、段落、排版等）",
                      "suggestedTags": ["建议话题1", "建议话题2", "建议话题3"]
                    }
                    
                    请确保返回完整且有效的JSON格式。将建议和修改融入小红书平台的风格，增加情感共鸣和互动元素。记住，优化后的内容应该像小红书上最受欢迎的博主写的一样有吸引力和感染力。
                    """, title, content, imageDescription);
        } else {
            // 没有图片时的提示
            prompt = String.format("""
                    你是小红书内容优化专家。请基于以下内容提供优化建议。
                    
                    标题：%s
                    内容：%s
                    
                    要求：
                    1. 返回一个完整的小红书风格内容，使用活泼、真诚的语气。
                    2. 必须在优化内容中合理添加emoji表情，每段至少使用1-2个相关emoji。
                    3. 内容必须包含精美排版，包括适当的分段、空行、标题和强调。
                    4. 优化内容的长度应该适中，不少于200字，确保内容丰富但不冗长。
                    5. 加入一些互动元素，如提问、邀请评论等增加互动性。
                    
                    请提供以下格式的JSON分析结果：
                    {
                      "imageDescription": "无图片", 
                      "contentTheme": "对文章内容的主题分析",
                      "imageSuggestion": "建议添加什么类型的配图",
                      "contentSuggestion": "对内容的优化建议，提高吸引力和互动性",
                      "titleSuggestion": "对标题的优化建议，使其更吸引眼球",
                      "optimizedTitle": "完整的优化后标题（加入emoji和吸引力元素）",
                      "optimizedContent": "完整的小红书风格优化内容（包含emoji、段落、排版等）",
                      "suggestedTags": ["建议话题1", "建议话题2", "建议话题3"]
                    }
                    
                    请确保返回完整且有效的JSON格式。将建议和修改融入小红书平台的风格，增加情感共鸣和互动元素。记住，优化后的内容应该像小红书上最受欢迎的博主写的一样有吸引力和感染力。
                    """, title, content);
        }
        
        try {
            String jsonBody = String.format("""
                    {
                        "model": "deepseek-chat",
                        "messages": [
                            {
                                "role": "user",
                                "content": "%s"
                            }
                        ],
                        "stream": false
                    }
                    """, prompt.replace("\"", "\\\"").replace("\n", "\\n"));
            
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(jsonBody, JSON);
            Request request = new Request.Builder()
                    .url(DEEPSEEK_API_URL)
                    .addHeader("Authorization", "Bearer " + API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .post(requestBody)
                    .build();
            
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    System.err.println("分析请求失败: " + response.code() + " - " + response.message());
                    return null;
                }
                
                String responseBody = response.body().string();
                // 提取AI返回的内容
                JSONObject json = new JSONObject(responseBody);
                JSONArray choices = json.getJSONArray("choices");
                JSONObject message = choices.getJSONObject(0).getJSONObject("message");
                String responseContent = message.getString("content");
                
                // 从内容中提取JSON字符串
                responseContent = responseContent.trim();
                
                // 如果内容被包在```json和```之间，提取出来
                if (responseContent.startsWith("```json") && responseContent.endsWith("```")) {
                    responseContent = responseContent.substring(7, responseContent.length() - 3).trim();
                } else if (responseContent.startsWith("```") && responseContent.endsWith("```")) {
                    responseContent = responseContent.substring(3, responseContent.length() - 3).trim();
                }
                
                try {
                    // 解析AI返回的JSON格式数据
                    JSONObject resultJson = new JSONObject(responseContent);
                    
                    // 创建FilterByAI对象
                    FilterByAI result = new FilterByAI();
                    
                    // 设置基本字段
                    if (resultJson.has("imageDescription")) {
                        result.setImageDescription(resultJson.getString("imageDescription"));
                    } else {
                        result.setImageDescription(imageDescription);
                    }
                    
                    if (resultJson.has("contentTheme")) {
                        result.setContentTheme(resultJson.getString("contentTheme"));
                    }
                    
                    if (resultJson.has("mainContent")) {
                        result.setMainContent(resultJson.getString("mainContent"));
                    }
                    
                    // 设置新增字段
                    if (resultJson.has("imageSuggestion")) {
                        result.setImageSuggestion(resultJson.getString("imageSuggestion"));
                    }
                    
                    if (resultJson.has("contentSuggestion")) {
                        result.setContentSuggestion(resultJson.getString("contentSuggestion"));
                    }
                    
                    if (resultJson.has("titleSuggestion")) {
                        result.setTitleSuggestion(resultJson.getString("titleSuggestion"));
                    }
                    
                    if (resultJson.has("optimizedTitle")) {
                        result.setOptimizedTitle(resultJson.getString("optimizedTitle"));
                    }
                    
                    if (resultJson.has("optimizedContent")) {
                        result.setOptimizedContent(resultJson.getString("optimizedContent"));
                    }
                    
                    if (resultJson.has("suggestedTags") && resultJson.get("suggestedTags") instanceof JSONArray) {
                        JSONArray tagsArray = resultJson.getJSONArray("suggestedTags");
                        String[] tags = new String[tagsArray.length()];
                        for (int i = 0; i < tagsArray.length(); i++) {
                            tags[i] = tagsArray.getString(i);
                        }
                        result.setSuggestedTags(tags);
                    }
                    
                    return result;
                } catch (Exception e) {
                    System.err.println("解析JSON失败: " + e.getMessage());
                    e.printStackTrace();
                    return null;
                }
            }
        } catch (Exception e) {
            System.err.println("请求出错: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    @Override
    public String OpenAIfliter(UserInput userInput) {
        String OPENAI_API_URL = "https://api.openai-hub.com/v1/chat/completions";
        String API_KEY = "sk-0PyW2HPVPe3S8WEgYIwqwHUPaXduOKEyYAdJuyDIJ7B2v8V6";
        String imageUrl = userInput.getImage_url();
        
        // 检查图片URL是否为空
        if (imageUrl == null || imageUrl.isEmpty()) {
            System.out.println("图片URL为空，无法进行图片分析");
            return "没有提供图片";
        }
        
        // 检查URL格式
        if (!imageUrl.startsWith("http://") && !imageUrl.startsWith("https://")) {
            System.out.println("图片URL格式不正确：" + imageUrl);
            return "图片URL格式不正确，需要http或https开头的URL";
        }
        
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(200, TimeUnit.SECONDS)
                .readTimeout(5000, TimeUnit.SECONDS)
                .writeTimeout(200, TimeUnit.SECONDS)
                .build();
        String base64Image = null;
        try {
            Request imageRequest = new Request.Builder()
                    .url(imageUrl)
                    .build();
            Response imageResponse = client.newCall(imageRequest).execute();
            if (!imageResponse.isSuccessful()) {
                System.err.println("下载图片失败: " + imageResponse.code() + " - " + imageResponse.message());
                return "图片下载失败";
            }
            byte[] imageBytes = imageResponse.body().bytes();
            base64Image = java.util.Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            System.err.println("图片下载或编码失败: " + e.getMessage());
            return "图片处理失败: " + e.getMessage();
        }
        String jsonBody = String.format("""
            {
                \"model\": \"gpt-4o-2024-05-13\",
                \"messages\": [
                    {
                        \"role\": \"user\",
                        \"content\": [
                            {
                                \"type\": \"text\",
                                \"text\": \"详细描述这张图片的内容，包括主体对象、颜色、场景、氛围和画面感受等方面。描述应该生动形象，不超过50字。\"
                            },
                            {
                                \"type\": \"image_url\",
                                \"image_url\": {
                                    \"url\": \"data:image/jpeg;base64,%s\" 
                                }
                            }
                        ]
                    }
                ]
            }
            """, base64Image);
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(jsonBody, JSON);
        Request request = new Request.Builder()
                .url(OPENAI_API_URL)
                .addHeader("Authorization", API_KEY)
                .addHeader("Content-Type", "application/json")
                .post(requestBody)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.err.println("OpenAI请求失败: " + response.code() + " - " + response.message());
                return null;
            }
            String responseBody = response.body().string();
            // 只提取 content 字段
            JSONObject json = new JSONObject(responseBody);
            JSONArray choices = json.getJSONArray("choices");
            JSONObject message = choices.getJSONObject(0).getJSONObject("message");
            String content = message.getString("content");
            return content;
        } catch (IOException e) {
            System.err.println("请求出错: " + e.getMessage());
        }
        return null;
    }

    @Override
    public FilterByAI filter(UserInput userInput) {
        String DEEPSEEK_API_URL = "https://api.deepseek.com/v1/chat/completions";
        String API_KEY = "你的API_KEY";

        OkHttpClient client = new OkHttpClient();
        String jsonBody = """
                {
                    "model": "deepseek-chat",
                    "messages": [
                        {
                            "role": "user",
                            "content": "%s"
                        }
                    ],
                    "temperature": 0.7
                }
                """.formatted(userInput.getContent());

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(jsonBody, JSON);
        Request request = new Request.Builder()
                .url(DEEPSEEK_API_URL)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.err.println("请求失败: " + response.code() + " - " + response.message());
                return null;
            }

            String responseBody = response.body().string();
            // 只提取 content 字段
            JSONObject json = new JSONObject(responseBody);
            JSONArray choices = json.getJSONArray("choices");
            JSONObject message = choices.getJSONObject(0).getJSONObject("message");
            String content = message.getString("content");

            // 只返回 content 字段内容，其它字段可为空或自定义
            return new FilterByAI(content, "", "");
        } catch (IOException e) {
            System.err.println("请求出错: " + e.getMessage());
        }
        return null;
    }
}
