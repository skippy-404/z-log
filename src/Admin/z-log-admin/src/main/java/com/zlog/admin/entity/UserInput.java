package com.zlog.admin.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInput {
    private String content; // 文字内容
    private String image_url;  // 图片
    private String prompt;  // 提示
    private String title;  // 标题
}
