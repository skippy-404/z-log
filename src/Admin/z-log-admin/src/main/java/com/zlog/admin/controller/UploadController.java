package com.zlog.admin.controller;

import com.zlog.admin.entity.ResponseEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = "http://localhost:5173", maxAge = 3600, allowCredentials = "true")  // 明确指定前端地址
public class UploadController {

    // 上传文件保存的目录（可在application.properties中配置）
    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;

    // 服务器访问地址前缀（根据实际环境配置）
    @Value("${server.address:localhost}")
    private String serverAddress;
    
    @Value("${server.port:8080}")
    private String serverPort;

    @PostMapping("/image")
    public ResponseEntity uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.error("上传的文件为空");
        }

        try {
            // 确保上传目录存在
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = UUID.randomUUID().toString() + extension;
            
            // 保存文件
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath);
            
            // 构建可访问的URL
            String fileUrl = String.format("http://%s:%s/uploads/%s", serverAddress, serverPort, filename);
            
            System.out.println("图片上传成功: " + fileUrl);
            
            // 返回文件URL
            return ResponseEntity.success(fileUrl);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.error("文件上传失败: " + e.getMessage());
        }
    }
} 