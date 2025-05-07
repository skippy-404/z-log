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
}
