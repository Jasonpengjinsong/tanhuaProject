package com.halead.server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.halead.server.pojo.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

/**
 * @title: UserService
 * @Author ppjjss
 * @Date: 2022/6/30 23:44
 * @Version 1.0
 */
@Service
public class UserService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${tanhua.sso.url}")
    private String url;

    private static final ObjectMapper MAPPER =new ObjectMapper();

    public User queryUserByToken(String token) {
        String jsonData = this.restTemplate.getForObject(url + "/user/" + token, String.class);
        if(StringUtils.isNotEmpty(jsonData)){

            try {
                return MAPPER.readValue(jsonData,User.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
