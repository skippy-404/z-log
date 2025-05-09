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
    //deepseek分析文本内容和标题
    //openAI分析图片内容
    String DeepSeekfilter(UserInput userInput);
    String OpenAIfliter(UserInput userInput);
    //deepseek 分析结果与openAI分析结果 推出文章主题
    String DeepSeekfilter(String ContentAnalise,String ImageDescription);
    //总过滤器 调用以上三种办法
    FilterByAI totalFilter(UserInput userInput);
    FilterByAI filter(UserInput userInput);
}
