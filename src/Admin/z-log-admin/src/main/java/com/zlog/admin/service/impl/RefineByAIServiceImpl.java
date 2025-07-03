package com.zlog.admin.service.impl;

import com.zlog.admin.service.RefineByAIService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Service
public class RefineByAIServiceImpl implements RefineByAIService {

    private final ChatClient chatClient;

    @Autowired
    public RefineByAIServiceImpl(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public String refineAll(String content, String image_url, String title, String prompt) {
        return "";
    }

    @Override
    public String refineIMG(String image_url, String prompt) {
        try {
            // 从URL下载图片到临时文件
            Resource imageResource = downloadImageFromUrl(image_url);
            
            // 使用ChatClient处理图片
            String response = ChatClient.create(chatModel).prompt()
                    .user(u -> u.text(prompt != null && !prompt.isEmpty() ? prompt : "描述这张图片中的内容")
                            .media(MimeTypeUtils.IMAGE_JPEG, imageResource))
                    .call()
                    .content();
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return "图像分析出错: " + e.getMessage();
        }
    }

    private Resource downloadImageFromUrl(String imageUrl) throws IOException {
        URL url = new URL(imageUrl);
        URLConnection connection = url.openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        
        // 创建临时文件
        Path tempFile = Files.createTempFile("image-", ".jpg");
        
        // 下载图片到临时文件
        Files.copy(connection.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);
        
        // 读取临时文件内容到字节数组
        byte[] imageBytes = Files.readAllBytes(tempFile);
        
        // 删除临时文件
        Files.delete(tempFile);
        
        // 返回ByteArrayResource
        return new ByteArrayResource(imageBytes);
    }

    @Override
    public String refineContent(String content, String prompt) {
        try {
            String response = chatClient.prompt()
                    .user(prompt != null && !prompt.isEmpty() ? prompt : "优化以下内容：" + content)
                    .call()
                    .content();
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return "内容优化出错: " + e.getMessage();
        }
    }

    @Override
    public String refineTitle(String title, String prompt) {
        try {
            String response = chatClient.prompt()
                    .user(prompt != null && !prompt.isEmpty() ? prompt : "优化以下标题：" + title)
                    .call()
                    .content();
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return "标题优化出错: " + e.getMessage();
        }
    }
}
