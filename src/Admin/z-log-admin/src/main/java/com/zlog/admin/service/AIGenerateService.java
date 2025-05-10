package com.zlog.admin.service;

import com.zlog.admin.entity.AIGenerateRequest;
import com.zlog.admin.entity.AIGenerateResponse;

/**
 * AI内容生成服务接口
 */
public interface AIGenerateService {
    
    /**
     * 根据内容类型和关键词生成内容
     * @param request 包含内容类型和关键词的请求对象
     * @return 生成的标题、内容和图片建议
     */
    AIGenerateResponse generateContent(AIGenerateRequest request);
} 