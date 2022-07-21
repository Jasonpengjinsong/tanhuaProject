package com.halead.server.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * @title: MyResponseBodyAdvice
 * @Author ppjjss
 * @Date: 2022/6/30 21:33
 * @Version 1.0
 */
@ControllerAdvice
public class MyResponseBodyAdvice implements ResponseBodyAdvice {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    private ObjectMapper mapper = new ObjectMapper();


    @Override
    public boolean supports(MethodParameter returnType, Class aClass) {
        //支持的处理类型，这里仅对get和post进行处理
        return returnType.hasMethodAnnotation(GetMapping.class) || returnType.hasMethodAnnotation(PostMapping.class);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {

       RedisCacheInterceptor

        return null;
    }
}
