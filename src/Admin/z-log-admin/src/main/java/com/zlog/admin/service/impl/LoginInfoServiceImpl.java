package com.zlog.admin.service.impl;

import com.zlog.admin.entity.LoginInfo;
import com.zlog.admin.entity.UserInfo;
import com.zlog.admin.mapper.UserInfoMapper;
import com.zlog.admin.service.LoginInfoService;
import com.zlog.admin.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class LoginInfoServiceImpl implements LoginInfoService {
    //调用mapper接口，根据用户名和密码查询用户信息
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Override
    public LoginInfo login(UserInfo userInfo) {
        UserInfo u=userInfoMapper.selectUserByNameAndPassword(userInfo);

        //判断
        if(u!=null){
            //创建登录信息对象
            log.info("用户登录成功");
            Map<String, Object> claims=new HashMap<>();
            claims.put("id",u.getId());
            claims.put("username",u.getUsername());
            String token= JwtUtils.generateToken(claims);

            return new LoginInfo(u.getId(),u.getUsername(),u.getPassword(),token );
        }else{
            log.info("用户登录失败");
        }
        return null;
    }
}

