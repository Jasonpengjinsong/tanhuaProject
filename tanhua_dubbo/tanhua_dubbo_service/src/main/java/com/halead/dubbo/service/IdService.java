package com.halead.dubbo.service;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @title: IdService
 * @Author ppjjss
 * @Date: 2022/6/30 22:33
 * @Version 1.0
 */

@Service
public class IdService {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    public Long createId(String type, String objectId) {

        type = StringUtils.upperCase(type);
        String hashKey ="TANHUA_HASH_ID_"+type;
        if(this.redisTemplate.opsForHash().hasKey(hashKey,objectId )){
            return Long.valueOf(this.redisTemplate.opsForHash().get(hashKey,objectId).toString());
        }
        String key ="TANHUA_ID_"+type;
        Long id = this.redisTemplate.opsForValue().increment(key);
        //将生成的id写入到hash表中
        this.redisTemplate.opsForHash().put(hashKey,objectId,id.toString());
        return id;
    }
}
