package com.zlog.admin.service;

import com.zlog.admin.entity.FilterByAI;
import com.zlog.admin.entity.UserInput;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public interface UserInputService {
    FilterByAI filterByAI = new FilterByAI();
    //openAI分析图片内容
    String OpenAIfliter(UserInput userInput);
    //总过滤器 一步到位获取结构化分析结果
    FilterByAI totalFilter(UserInput userInput);
    FilterByAI filter(UserInput userInput);
}
