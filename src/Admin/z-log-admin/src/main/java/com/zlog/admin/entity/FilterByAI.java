package com.zlog.admin.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterByAI {
    private String imageDescription; //分析图片的主要内容
    private String contentTheme; // 主题
    private String mainContent; //主体内容

    // 新增结构化字段
    private String imageSuggestion; // 图片优化建议
    private String contentSuggestion; // 内容优化建议
    private String titleSuggestion; // 标题优化建议
    private String optimizedTitle; // 优化后的完整标题
    private String optimizedContent; // 优化后的完整内容
    private String[] suggestedTags; // 建议的标签/话题
    
    // 简化构造函数，兼容原有代码
    public FilterByAI(String imageDescription, String contentTheme, String mainContent) {
        this.imageDescription = imageDescription;
        this.contentTheme = contentTheme;
        this.mainContent = mainContent;
    }
}
