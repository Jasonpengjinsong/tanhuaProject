package com.halead.server.utils;

import com.halead.server.pojo.User;

/**
 * @title: UserThreadLocal
 * @Author ppjjss
 * @Date: 2022/6/30 18:26
 * @Version 1.0
 */
public class UserThreadLocal {

    private static final ThreadLocal<User> LOCAL =new ThreadLocal<>();

    public UserThreadLocal() {
    }

    public static void set(User user){
        LOCAL.set(user);
    }
    public static User get(){
       return LOCAL.get();
    }

}
