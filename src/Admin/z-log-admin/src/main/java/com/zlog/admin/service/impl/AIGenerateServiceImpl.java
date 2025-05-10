package com.zlog.admin.service.impl;

import com.zlog.admin.entity.AIGenerateRequest;
import com.zlog.admin.entity.AIGenerateResponse;
import com.zlog.admin.service.AIGenerateService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * AI内容生成服务实现类
 */
@Service
public class AIGenerateServiceImpl implements AIGenerateService {

    // DeepSeek API配置
    private static final String DEEPSEEK_API_URL = "https://api.deepseek.com/v1/chat/completions";
    private static final String API_KEY = "sk-1a44e77dbbcf4331844c6dbfc3ed2ad1"; // 与现有接口使用相同的API KEY
    
    @Override
    public AIGenerateResponse generateContent(AIGenerateRequest request) {
        // 构建提示词
        String prompt = buildPrompt(request);
        
        // 调用DeepSeek API
        String apiResponse = callDeepSeekAPI(prompt);
        
        // 解析响应
        if (apiResponse != null) {
            return parseResponse(apiResponse);
        } else {
            // 如果API调用失败，返回默认内容
            return createDefaultResponse(request);
        }
    }
    
    /**
     * 根据请求构建提示词
     */
    private String buildPrompt(AIGenerateRequest request) {
        String contentType = request.getContentType();
        String keyword = request.getKeyword();
        String chineseType = getChineseContentType(contentType);
        
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("请你帮我生成一篇小红书风格的")
                .append(chineseType)
                .append("内容。");
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            promptBuilder.append("关键词是：").append(keyword).append("。");
        }
        
        promptBuilder.append("\n请按照以下格式返回：\n")
                .append("标题：[这里是生成的标题]\n")
                .append("图片建议：[这里是详细的图片描述或建议，用于指导用户选择什么样的图片]\n")
                .append("正文：[这里是生成的正文内容，保持小红书风格，有emoji表情，分段清晰]\n\n")
                .append("生成的内容需要符合小红书的风格特点：")
                .append("1. 标题吸引人，带有一定的情感或悬念")
                .append("2. 文字中适当使用emoji表情")
                .append("3. 段落短小精悍，易于阅读")
                .append("4. 内容真实、有个人体验，不要过于营销");
        
        return promptBuilder.toString();
    }
    
    /**
     * 获取内容类型的中文名称
     */
    private String getChineseContentType(String contentType) {
        switch (contentType) {
            case "travel":
                return "旅行游记";
            case "food":
                return "美食分享";
            case "book":
                return "读书心得";
            case "movie":
                return "影视剧评";
            case "tech":
                return "数码评测";
            default:
                return "博客";
        }
    }
    
    /**
     * 调用DeepSeek API
     */
    private String callDeepSeekAPI(String prompt) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
        
        String jsonBody = String.format("""
                {
                    "model": "deepseek-chat",
                    "messages": [
                        {
                            "role": "user",
                            "content": "%s"
                        }
                    ],
                    "stream": false,
                    "temperature": 0.7
                }
                """, prompt.replace("\"", "\\\"").replace("\n", "\\n"));
        
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(jsonBody, JSON);
        
        Request request = new Request.Builder()
                .url(DEEPSEEK_API_URL)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .post(requestBody)
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.err.println("DeepSeek请求失败: " + response.code() + " - " + response.message());
                return null;
            }
            
            String responseBody = response.body().string();
            JSONObject json = new JSONObject(responseBody);
            JSONArray choices = json.getJSONArray("choices");
            JSONObject message = choices.getJSONObject(0).getJSONObject("message");
            String content = message.getString("content");
            
            return content;
        } catch (IOException e) {
            System.err.println("DeepSeek API调用出错: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 解析API响应
     */
    private AIGenerateResponse parseResponse(String apiResponse) {
        try {
            String title = "";
            String imagePrompt = "";
            String content = "";
            
            // 按行分割内容
            String[] lines = apiResponse.split("\n");
            
            boolean inContent = false;
            StringBuilder contentBuilder = new StringBuilder();
            
            for (String line : lines) {
                line = line.trim();
                
                if (line.startsWith("标题：")) {
                    title = line.substring("标题：".length()).trim();
                } else if (line.startsWith("图片建议：")) {
                    imagePrompt = line.substring("图片建议：".length()).trim();
                } else if (line.startsWith("正文：")) {
                    inContent = true;
                    // 跳过"正文："这行
                } else if (inContent) {
                    contentBuilder.append(line).append("\n");
                }
            }
            
            content = contentBuilder.toString().trim();
            
            // 如果没有提取到标题或内容，处理整个响应
            if (title.isEmpty() || content.isEmpty()) {
                String[] parts = apiResponse.split("(?i)标题:|图片建议:|正文:", 4);
                if (parts.length >= 2) {
                    title = parts[1].trim();
                }
                if (parts.length >= 3) {
                    imagePrompt = parts[2].trim();
                }
                if (parts.length >= 4) {
                    content = parts[3].trim();
                }
            }
            
            return new AIGenerateResponse(title, content, imagePrompt);
        } catch (Exception e) {
            System.err.println("解析API响应出错: " + e.getMessage());
            e.printStackTrace();
            
            // 如果解析失败，返回原始内容
            return new AIGenerateResponse(
                    "AI生成的标题",
                    apiResponse,
                    "请选择与内容相关的图片"
            );
        }
    }
    
    /**
     * 创建默认响应（API调用失败时使用）
     */
    private AIGenerateResponse createDefaultResponse(AIGenerateRequest request) {
        String contentType = request.getContentType();
        String keyword = request.getKeyword() != null ? request.getKeyword() : "";
        
        String title = "";
        String content = "";
        String imagePrompt = "";
        
        switch (contentType) {
            case "travel":
                title = keyword.isEmpty() ? "旅行日记：心灵的探索之旅" : keyword + "之旅：心灵的探索与发现";
                content = "✨今天终于踏上了期待已久的旅程\n\n这里的风景如画，空气中弥漫着大自然的清新。远处的山峦起伏，近处的小溪潺潺，构成了一幅完美的画卷。\n\n🌈在这里，我感受到了内心的平静，远离城市的喧嚣，寻找到了久违的宁静。\n\n旅行不仅是身体的移动，更是心灵的探索。每一次出发，都是一次成长。\n\n📷分享一些照片，记录下这美好的瞬间，希望也能带给大家一些感动和启发。";
                imagePrompt = "一张展示旅行目的地的自然风景照，最好有山水画面，光线明亮，色彩鲜明，人物可以背对镜头远眺风景";
                break;
            case "food":
                title = keyword.isEmpty() ? "舌尖上的美味：家常菜里的小确幸" : keyword + "：舌尖上的美味体验";
                content = "🍜今天给大家分享一道超级下饭的家常菜\n\n这道菜色香味俱全，而且做法简单，新手也能轻松驾驭！\n\n👨‍🍳主要食材：\n- 食材1\n- 食材2\n- 调料若干\n\n📝制作步骤：\n1. 第一步\n2. 第二步\n3. 最后一步\n\n🌟小贴士：注意火候的控制，这是决定成败的关键！\n\n吃完真的太满足了！大家一定要试试看！";
                imagePrompt = "一张美食成品图，拍摄角度俯视，光线明亮，食物色彩鲜明诱人，摆盘精致，可以加入一些餐具点缀";
                break;
            case "book":
                title = keyword.isEmpty() ? "读完这本书，我的世界观被重塑了" : "《" + keyword + "》：重塑我的世界观";
                content = "📚最近读完了这本书，不得不说，它真的改变了我看世界的方式\n\n作者通过独特的视角，讲述了一个关于成长、挑战与救赎的故事。\n\n💡这本书最打动我的，是书中主角面对困境时的勇气和智慧。在现实生活中，我们何尝不是需要这样的品质？\n\n🌈推荐给同样在人生路上寻找答案的你们，相信读完后，你也会有不一样的感悟。\n\n有读过的朋友可以在评论区分享你们的感受~";
                imagePrompt = "一张书籍照片，可以是书籍摆放在舒适的阅读环境中，如咖啡桌或书架旁，可以搭配一些阅读氛围物品如咖啡杯、眼镜等";
                break;
            case "movie":
                title = keyword.isEmpty() ? "这部电影，看完后我沉默了很久..." : "《" + keyword + "》：看完后的一次灵魂触动";
                content = "🎬昨晚熬夜看完了这部电影，久久不能平静\n\n导演通过精妙的镜头语言和叙事手法，呈现了一个发人深省的故事。\n\n✨影片中最打动我的是那个情节，眼泪不自觉地流了下来。演员的表演太真实了，仿佛在诉说我们每个人的故事。\n\n🌟推荐指数：⭐⭐⭐⭐⭐\n\n如果你也看过，欢迎在评论区留言你的感受~一起交流！";
                imagePrompt = "一张电影相关的图片，可以是电影海报、剧照，或是观影环境的照片，灯光氛围感强，画面构图精致";
                break;
            case "tech":
                title = keyword.isEmpty() ? "入手一周后，这款数码产品真的惊艳到我了" : keyword + "使用体验：真的惊艳到我了";
                content = "🔥最近入手了这款期待已久的产品，使用一周后来分享真实体验\n\n📱外观设计：简约大气，手感舒适，质感很好\n\n💻性能表现：运行流畅，多任务处理毫无压力\n\n🔋续航能力：完全满足一天的重度使用需求\n\n📸拍照体验：样张锐度高，色彩还原准确\n\n💰性价比：这个价位绝对值得\n\n🌟总体评分：9.5/10\n\n🤔还在考虑要不要入手的朋友们，希望我的分享能帮到你们！";
                imagePrompt = "一张产品展示照，背景简洁，产品为主体，可以搭配一些使用场景的物品，光线明亮，产品细节清晰";
                break;
            default:
                title = "AI生成的精彩内容分享";
                content = "今天想和大家分享一些有趣的经历和感受...\n\n希望我的分享能给你带来一些启发或共鸣。\n\n期待在评论区看到你们的想法和回应！";
                imagePrompt = "请选择与内容主题相关的高质量图片，画面构图完整，光线充足";
        }
        
        return new AIGenerateResponse(title, content, imagePrompt);
    }
} 