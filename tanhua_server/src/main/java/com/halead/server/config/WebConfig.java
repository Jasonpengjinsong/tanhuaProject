package com.halead.server.config;

import com.halead.server.interceptor.RedisCacheInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @title: WebConfig
 * @Author ppjjss
 * @Date: 2022/6/30 21:13
 * @Version 1.0
 */
@Configuration
public class WebConfig  implements WebMvcConfigurer {

    @Autowired
    private RedisCacheInterceptor redisCacheInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

       registry.addInterceptor(this.redisCacheInterceptor).addPathPatterns("/**");

    }
}
