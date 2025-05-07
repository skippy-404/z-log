package com.zlog.admin.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Service
public class UserInput {
    private String content; // 文字内容
    private String image;  // 图片
    private String prompt;  // 提示
    private String title;  // 标题
}
