package com.halead.server.handler;

import com.halead.server.pojo.User;
import com.halead.server.service.UserService;
import com.halead.server.utils.NoAuthorization;
import com.halead.server.utils.UserThreadLocal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @title: TokenInterceptor
 * @Author ppjjss
 * @Date: 2022/6/30 23:43
 * @Version 1.0
 */
@Component
public class TokenInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
       //判断请求的方法是否包含了NoAuthorization，如果包含了，就不需要做处理
        if(handler instanceof HandlerMethod){
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            NoAuthorization annotation = handlerMethod.getMethod().getAnnotation(NoAuthorization.class);
            if(annotation != null){
                return  true;
            }
        }
        String token = request.getHeader("Authorization");
        User user= this.userService.queryUserByToken(token);
        if(null == user){
            response.setStatus(401);
            return false;
        }
        UserThreadLocal.set(user);

        return true;
    }
}
