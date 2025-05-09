package com.zlog.admin.service;

import com.zlog.admin.entity.LoginInfo;
import com.zlog.admin.entity.UserInfo;

public interface LoginInfoService
{
    //用户登陆
    LoginInfo login(UserInfo userInfo);
}
