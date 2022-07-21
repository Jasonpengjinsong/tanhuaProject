package com.halead.sso.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.omg.CORBA.PRIVATE_MEMBER;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * @title: SmsService
 * @Author ppjjss
 * @Date: 2022/6/28 21:38
 * @Version 1.0
 */
public class SmsService {

    @Autowired
    private RestTemplate restTemplate;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static final String REDIS_KEY_PREFIX = "CHECK_CODE";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final Logger LOGGER = LoggerFactory.getLogger(SmsService.class);

    /**
     * 发送验证码短信
     *
     * @return
     */
    public String sendSms(String mobile) {
        String url = "https://open.ucpaas.com/ol/sms/sendSms";
        HashMap<String, Object> params = new HashMap<>();
        params.put("sid", "a663a906913df5725721104e1b98acb3");
        params.put("token", "260d6f489288bea45607a57aed18a7e6");
        params.put("appid", "43a075747b124752aff59b98a29aa44a");
        params.put("templateid", "");
        params.put("mobile", mobile);
        //生成6位数的验证码
        params.put("param", RandomUtils.nextInt(100000, 999999));
        ResponseEntity<String> responseEntity = this.restTemplate.postForEntity(url, params, String.class);
        String body = responseEntity.getBody();
        try {
            JsonNode jsonNode = MAPPER.readTree(body);
            //000000 表示发送成功
            if (StringUtils.equals(jsonNode.get("code").textValue(), "000000")) {
                return String.valueOf(params.get("param"));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<String, Object> sendCheckCode(String phone) {

        HashMap<String, Object> result = new HashMap<>();
        try {
            String redisKey = REDIS_KEY_PREFIX + phone;
            String value = this.redisTemplate.opsForValue().get(redisKey);
            if (StringUtils.isNoneEmpty(value)) {
                result.put("code", 1);
                result.put("msg", "上一次发送的验证码还没有失效");
                return result;
            }
            String code = this.sendSms(phone);
            if (null == code) {
                result.put("code", 2);
                result.put("msg", "发送验证码失败");
                return result;
            }
            result.put("code", 3);
            result.put("msg", "ok");
            this.redisTemplate.opsForValue().set(redisKey, code, Duration.ofMillis(2));
            return result;

        } catch (Exception e) {
            LOGGER.error("发送验证码错误" + phone, e);
            result.put("code", 4);
            result.put("msg", "发送验证码出现异常");
            return result;
        }
    }
}
