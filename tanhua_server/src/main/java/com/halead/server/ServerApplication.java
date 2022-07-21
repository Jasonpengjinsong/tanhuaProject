package com.halead.server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

/**
 * @title: ServerApplication
 * @Author ppjjss
 * @Date: 2022/6/29 23:44
 * @Version 1.0
 */
@MapperScan("com.halead.server.mapper")
@SpringBootApplication(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
public class ServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class,args);
    }
}
