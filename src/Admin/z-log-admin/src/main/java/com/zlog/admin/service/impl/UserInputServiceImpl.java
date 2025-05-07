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

import java.io.IOException;

@Service
public class UserInputServiceImpl implements UserInputService {
    @Override
    public FilterByAI filter(UserInput userInput) {
        FilterByAI filterByAI;
        String DEEPSEEK_API_URL = "https://api.deepseek.com/v1/chat/completions";
        String API_KEY = "sk-1a44e77dbbcf4331844c6dbfc3ed2ad1"; // 替换成你的 API Key

        OkHttpClient client = new OkHttpClient();
        // 1. 构造 JSON 请求体（DeepSeek API 格式）
        String jsonBody = """
                {
                    "model": "deepseek-chat",
                    "messages": [
                        {
                            "role": "user",
                            "content": "你好，介绍一下你自己"
                        }
                    ],
                    "temperature": 0.7
                }
                """;

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
                System.err.println("请求失败: " + response.code() + " - " + response.message());
                return null;
            }

            // 打印 API 返回的 JSON 数据
            String responseBody = response.body().string();
            System.out.println("API 返回结果:");
            System.out.println(responseBody);
        } catch (IOException e) {
            System.err.println("请求出错: " + e.getMessage());
        }
        return new FilterByAI("123", "contentTheme", "mainContent");

    }
}
