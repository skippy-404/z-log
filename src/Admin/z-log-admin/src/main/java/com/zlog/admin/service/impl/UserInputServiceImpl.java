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
        String ContentAnalise = DeepSeekfilter(userInput);
        String ImageDescription = OpenAIfliter(userInput);
        String BlogTheme = DeepSeekfilter(ContentAnalise,ImageDescription);
        return new FilterByAI(ImageDescription,ContentAnalise,BlogTheme);
    }
    @Override
    public String DeepSeekfilter(UserInput userInput) {
        String DEEPSEEK_API_URL = "https://api.deepseek.com/v1/chat/completions";
        String API_KEY = "sk-1a44e77dbbcf4331844c6dbfc3ed2ad1";
        String UserInputContent = userInput.getContent();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS)
                .writeTimeout(100, TimeUnit.SECONDS)
                .build();
        String jsonBody = String.format("""
                {
                    \"model\": \"deepseek-chat\",
                    \"messages\": [
                        {
                            \"role\": \"user\",
                            \"content\": \"%s /n这是用户想要发布小红书的文字内容帮我分析，他的要点\"
                        }
                    ],
                    \"stream\": false
                }
                """, UserInputContent);
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
                System.err.println("DeepSeek请求失败: " + response.code() + " - " + response.message());
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
    public String OpenAIfliter(UserInput userInput) {
        String OPENAI_API_URL = "https://api.openai-hub.com/v1/chat/completions";
        String API_KEY = "sk-0PyW2HPVPe3S8WEgYIwqwHUPaXduOKEyYAdJuyDIJ7B2v8V6";
        String imageUrl = userInput.getImage_url();
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
                return null;
            }
            byte[] imageBytes = imageResponse.body().bytes();
            base64Image = java.util.Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            System.err.println("图片下载或编码失败: " + e.getMessage());
            return null;
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
                                \"text\": \"一句话告诉我图像里面有什么\"
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
    public String DeepSeekfilter(String ConnentAnalise,String ImageDescription) {
        // 修改为正确的 API 地址
        String DEEPSEEK_API_URL = "https://api.deepseek.com/v1/chat/completions"; 
        String API_KEY = "sk-1a44e77dbbcf4331844c6dbfc3ed2ad1";
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(200, TimeUnit.SECONDS)
                .readTimeout(5000, TimeUnit.SECONDS)
                .writeTimeout(200, TimeUnit.SECONDS)
                .build();
        // 1. 构造 JSON 请求体（DeepSeek API 格式）

        //这里我还没写完 这块有点小bug 我先写死了
        String jsonBody = String.format("""
                {
                    "model": "deepseek-chat",
                    "messages": [
                        {
                            "role": "user",
                            "content": "家人们，谁懂啊，偶遇吉林大学下头男 图片内容为：图像中有一个穿黑色背心的人。 总结这些内容帮我分析一下这篇博客的主题是什么，一句话给我主题"
                        }
                    ],
                    "stream": false
                }
                """,ConnentAnalise,ImageDescription);

        // 2. 创建 RequestBody
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(jsonBody, JSON);
        // 3. 构造 HTTP 请求
        Request request = new Request.Builder()
                .url(DEEPSEEK_API_URL)
                .addHeader("Authorization", "Bearer " + API_KEY) // 认证头
                .addHeader("Content-Type", "application/json")
                .post(requestBody)
                .build();

        // 4. 发送请求并处理响应
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.err.println("All请求失败: " + response.code() + " - " + response.message());
                return null;
            }

            // 打印 API 返回的 JSON 数据
            String responseBody = response.body().string();
            System.out.println("API 返回结果:");
            System.out.println(responseBody);
            return responseBody; // 返回 API 响应内容
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
