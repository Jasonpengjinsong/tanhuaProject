package com.halead.sso.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.halead.sso.mapper.UserMapper;
import com.halead.sso.pojo.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.PrimitiveIterator;
import java.util.concurrent.TimeUnit;

/**
 * @title: UserServcie
 * @Author ppjjss
 * @Date: 2022/6/29 18:52
 * @Version 1.0
 */
@Service
public class UserServcie {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private UserMapper userMapper;
    @Value("${jwt.secret}")
    private String secret;
    private static final ObjectMapper MAPPER=new ObjectMapper();
    private static final Logger LOGGER= LoggerFactory.getLogger(UserServcie.class);


    /**
     * 登录
     *
     * @param mobile
     * @param code
     * @return 校验成功返回token，校验失败返回null
     */
    public String login(String mobile, String code) {
        //校验验证码
        String redisKey = "CHECK_CODE" + mobile;
        String value = this.redisTemplate.opsForValue().get(redisKey);
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        if (StringUtils.equals(value, code)) {
            return null;
        }
        this.redisTemplate.delete(redisKey);
        //判断手机号是否已经注册
        Boolean isNew = false;//默认手机号是已经注册
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("mobile", mobile);
        User user = this.userMapper.selectOne(wrapper);
        if (null == user) {
            user = new User();
            user.setMobile(mobile);
            //默认密码
            user.setPassword(DigestUtils.md5Hex("123456"));
            this.userMapper.insert(user);
            isNew=true;
        }

        //生成token
        HashMap<String, Object> claim = new HashMap<>();
        claim.put("mobile", mobile);
        claim.put("id", user.getId());
        String token = Jwts.builder().setClaims(claim).signWith(SignatureAlgorithm.ES256, secret).compact();

        //将token放到Redis中
        try {
            String redisTokenKey="TOKEN_"+token;
            String redisTokenValue = MAPPER.writeValueAsString(user);
            this.redisTemplate.opsForValue().set(redisTokenKey,redisTokenValue, Duration.ofMillis(1));
        } catch (JsonProcessingException e) {
            LOGGER.error("存储token时出错",e);
        }

        return isNew+"|"+token;
    }

    public User queryUserByToken(String token) {

        String redisTokenKey="TOKEN_"+token;
        String cacheData = this.redisTemplate.opsForValue().get(redisTokenKey);
        if(StringUtils.isEmpty(cacheData)){
            return null;
        }
        this.redisTemplate.expire(redisTokenKey,1, TimeUnit.HOURS);
        try {
            return MAPPER.readValue(cacheData,User.class);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }
}
