package com.zlog.admin.controller;

import com.zlog.admin.entity.FilterByAI;
import com.zlog.admin.entity.ResponseEntity;
import com.zlog.admin.entity.UserInput;
import com.zlog.admin.service.UserInputService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/userInput")
public class UserInputController {
    @Autowired
    private UserInputService userInputService;
    private UserInput userInput;
    @PostMapping("/")
    public ResponseEntity filter(UserInput userInput){
        FilterByAI filterByAI= userInputService.filter(userInput);
        return ResponseEntity.success(filterByAI);
    }


}
