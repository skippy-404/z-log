package com.zlog.admin.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AI一键生成内容请求实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AIGenerateRequest {
    /**
     * 内容类型：travel(旅行游记), food(美食分享), book(读书心得), movie(影视剧评), tech(数码评测)
     */
    private String contentType;
    
    /**
     * 用户输入的关键词(可选)
     */
    private String keyword;
} 