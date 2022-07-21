package com.halead.sso.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.halead.sso.config.HuanXinConfig;
import com.halead.sso.vo.HuanXinUser;
import com.sun.net.httpserver.Headers;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.xml.ws.spi.http.HttpHandler;
import java.util.ArrayList;

/**
 * @title: HuanxinService
 * @Author ppjjss
 * @Date: 2022/7/3 19:13
 * @Version 1.0
 */
@Service
public class HuanxinService {

    @Autowired
    private HuanXinConfig huanXinConfig;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private HuanXinTokenService huanXinTokenService;

    private static final ObjectMapper MAPPER =new ObjectMapper();

    /**
     * 注册环信用户
     * @return
     */
    public Boolean register(Long userId){
        String url = this.huanXinConfig.getUrl() + this.huanXinConfig.getOrgName() + "/" + this.huanXinConfig.getAppName() + "/user";
        String token = this.huanXinTokenService.getToken();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type","application/json");
        headers.add("Authorization","Bearer"+token);
        ArrayList<Object> huanxinUsers = new ArrayList<>();
        huanxinUsers.add(new HuanXinUser(userId.toString(), DigestUtils.md5Hex(userId+"_tan_ppjjss")));
        try {
            HttpEntity<String> httpEntity = new HttpEntity<>(MAPPER.writeValueAsString(huanxinUsers), headers);
            //发起请求
            ResponseEntity<String> responseEntity = this.restTemplate.postForEntity(url, httpEntity, String.class);
            return responseEntity.getStatusCodeValue() == 200;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return false;
    }
}
