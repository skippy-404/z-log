package com.zlog.admin.controller;

import com.zlog.admin.entity.LoginInfo;
import com.zlog.admin.entity.ResponseEntity;
import com.zlog.admin.entity.UserInfo;
import com.zlog.admin.service.impl.LoginInfoServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class LoginController {
    @Autowired
    LoginInfoServiceImpl loginInfoService;
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody UserInfo userInfo) {
        log.info("login", userInfo);
        LoginInfo info = loginInfoService.login(userInfo);
        if (info != null) {
            return ResponseEntity.success(info);
        }
        return ResponseEntity.error();
    }
}