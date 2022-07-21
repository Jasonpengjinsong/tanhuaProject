package com.halead.sso.service;

import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.halead.sso.config.HuanXinConfig;
import org.apache.commons.lang3.StringUtils;
import org.omg.CORBA.PRIVATE_MEMBER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @title: HuanXinTokenService
 * @Author ppjjss
 * @Date: 2022/7/3 18:41
 * @Version 1.0
 */
@Service
public class HuanXinTokenService {

    @Autowired
    private HuanXinConfig huanXinConfig;

    @Autowired
    private RestTemplate restTemplate;

    private static final ObjectMapper MAPPER = new ObjectMapper();
    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    private static final String tokenRedisKey ="HUANXIN_TOKEN";

    public String getToken(){

        //先从redis中命中
        String cacheDate = this.redisTemplate.opsForValue().get(tokenRedisKey);
        if(StringUtils.isNotEmpty(cacheDate)){
            return cacheDate;
        }
        String url = this.huanXinConfig.getUrl()+this.huanXinConfig.getOrgName()+"/"+this.huanXinConfig.getAppName()+"/token";
        Map<String, Object> param = new HashMap<>();
        param.put("grant_type","clent_credential");
        param.put("client_id",this.huanXinConfig.getClientId());
        param.put("client_secret",this.huanXinConfig.getClientSecret());

        ResponseEntity<String> responseEntity= this.restTemplate.postForEntity(url,param,String.class);
        if(responseEntity.getStatusCodeValue()!=200){
           return null;
        }
        String body = responseEntity.getBody();
        try {
            JsonNode jsonNode = MAPPER.readTree(body);
            String accessToken = jsonNode.get("access_token").asText();
            //过期时间
            long expireIn = jsonNode.get("expire_in").asLong() - 86400;
            this.redisTemplate.opsForValue().set(tokenRedisKey,accessToken,expireIn, TimeUnit.SECONDS);
            return accessToken;
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }
}
