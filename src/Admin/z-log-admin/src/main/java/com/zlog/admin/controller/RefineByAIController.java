package com.zlog.admin.controller;

import com.zlog.admin.entity.RefineRequest;
import com.zlog.admin.entity.ResponseEntity;
import com.zlog.admin.service.RefineByAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/refine")
public class RefineByAIController {
    @Autowired
    RefineByAIService refineByAIService;
    @PostMapping("/image")
    public ResponseEntity refineIMG(@RequestBody RefineRequest request)
    {
        String image_url = request.getImage_url();
        String prompt = request.getPrompt();
        String result = refineByAIService.refineIMG(image_url, prompt);
        return ResponseEntity.success(result);
    }

    @PostMapping("/content")
    public ResponseEntity refineContent(@RequestBody RefineRequest request){
        String content = request.getContent();
        String prompt = request.getPrompt();
        String result = refineByAIService.refineContent(content, prompt);
        return ResponseEntity.success(result);
    }
    @PostMapping("/title")
    public ResponseEntity refineTitle(@RequestBody RefineRequest request){
        String title = request.getTitle();
        String prompt = request.getPrompt();
        String result = refineByAIService.refineTitle(title, prompt);
        return ResponseEntity.success(result);
    }
    @PostMapping("/all")
    public ResponseEntity refineAll(@RequestBody RefineRequest request){
        String content = request.getContent();
        String image_url = request.getImage_url();
        String title = request.getTitle();
        String prompt = request.getPrompt();
        String result = refineByAIService.refineAll(content, image_url, title, prompt);
        return ResponseEntity.success(result);
    }
}