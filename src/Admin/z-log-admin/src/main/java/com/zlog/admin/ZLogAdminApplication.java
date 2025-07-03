package com.zlog.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@MapperScan("com.zlog.admin.mapper")
@EntityScan("com.zlog.admin.entity")
@EnableJpaRepositories("com.zlog.admin.repository")
public class ZLogAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZLogAdminApplication.class, args);
    }

}
