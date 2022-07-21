package com.halead.server.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.crypto.Data;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

/**
 * @title: RedisCacheInterceptor
 * @Author ppjjss
 * @Date: 2022/6/30 20:46
 * @Version 1.0
 */
@Component
public class RedisCacheInterceptor implements HandlerInterceptor {

    private static ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Value("${tanhua.cache.enable}")
    private Boolean enable;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!enable) {
            return true;
        }
        String method = request.getMethod();
        if (!StringUtils.equalsAnyIgnoreCase(method, "GET", "POST")) {
            return true;
        }

        String redisKey = createRedisKey(request);
        String data = this.redisTemplate.opsForValue().get(redisKey);
        if(StringUtils.isEmpty(data)){
            return true;
        }
        //将data数据进行响应
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(data);
        return true;
    }

    private static String createRedisKey(HttpServletRequest request) throws IOException {
       String paramStr= request.getRequestURI();
        Map<String, String[]> parameterMap = request.getParameterMap();
        if(parameterMap.isEmpty()){
           paramStr += IOUtils.toString(request.getInputStream(),"UTF-8");
        }else {
            paramStr += mapper.writeValueAsString(request.getParameterMap());
        }
       String authorization = request.getHeader("Authorization");
        if(StringUtils.isNotEmpty(authorization)){
            paramStr +="_"+authorization;
        }
        return "SERVER_DATA_"+ DigestUtils.md5Hex(paramStr);
    }
}
