package com.zlog.admin.service;

public interface RefineByAIService {

    String refineAll(String content, String image_url, String title,String prompt);
    String refineIMG(String image_url,  String prompt);
    String refineContent(String content,  String prompt);
    String refineTitle(String title,  String prompt);
}
