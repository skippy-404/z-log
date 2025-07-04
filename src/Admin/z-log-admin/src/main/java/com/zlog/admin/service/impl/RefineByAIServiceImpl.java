package com.zlog.admin.service.impl;

import com.zlog.admin.service.RefineByAIService;
import org.springframework.stereotype.Service;

@Service
public class RefineByAIServiceImpl implements RefineByAIService {

    @Override
    public String refineAll(String content, String image_url, String title, String prompt) {
        return "";
    }

    @Override
    public String refineIMG(String image_url, String prompt) {
        return "";
    }

    @Override
    public String refineContent(String content, String prompt) {
        return "";
    }

    @Override
    public String refineTitle(String title, String prompt) {
        return "";
    }
}
