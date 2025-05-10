package com.zlog.admin.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AI一键生成内容响应实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AIGenerateResponse {
    /**
     * 生成的标题
     */
    private String title;
    
    /**
     * 生成的正文内容
     */
    private String content;
    
    /**
     * 图片建议/提示
     */
    private String imagePrompt;
} 