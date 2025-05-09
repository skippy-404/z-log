package com.zlog.admin.controller;

import com.zlog.admin.entity.ResponseEntity;
import com.zlog.admin.entity.UserInfo;
import com.zlog.admin.service.UserRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegisterController {
    @Autowired
    private UserRegisterService userRegisterService;
    @PostMapping("/register")
    public ResponseEntity register(@RequestBody UserInfo userInfo) {
        try {
            userRegisterService.register(userInfo);
            return ResponseEntity.success();
        } catch (RuntimeException e) {
            return ResponseEntity.error();
        }
    }
}
