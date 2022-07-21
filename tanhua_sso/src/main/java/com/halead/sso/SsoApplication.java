package com.halead.sso;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @title: SsoApplication
 * @Author ppjjss
 * @Date: 2022/6/28 20:00
 * @Version 1.0
 */
@MapperScan("com.halead.sso.mapper")
@SpringBootApplication
public class SsoApplication {
    public static void main(String[] args) {
        SpringApplication.run(SsoApplication.class,args);
    }
}
