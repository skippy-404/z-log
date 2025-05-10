package com.zlog.admin.controller;

import com.zlog.admin.entity.AIGenerateRequest;
import com.zlog.admin.entity.AIGenerateResponse;
import com.zlog.admin.entity.ResponseEntity;
import com.zlog.admin.service.AIGenerateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AI内容生成控制器
 */
@RestController
@RequestMapping("/api/ai")
public class AIGenerateController {
    
    @Autowired
    private AIGenerateService aiGenerateService;
    
    /**
     * AI内容生成接口
     * @param request 包含内容类型和关键词的请求对象
     * @return 生成的标题、内容和图片建议
     */
    @PostMapping("/generate")
    public ResponseEntity generateContent(@RequestBody AIGenerateRequest request) {
        try {
            AIGenerateResponse response = aiGenerateService.generateContent(request);
            return ResponseEntity.success(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.error();
        }
    }
} 