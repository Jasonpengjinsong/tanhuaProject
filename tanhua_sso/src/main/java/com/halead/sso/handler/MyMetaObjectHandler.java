package com.halead.sso.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @title: MyMetaObjectHandler
 * @Author ppjjss
 * @Date: 2022/6/28 20:18
 * @Version 1.0
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        Object created = getFieldValByName("created", metaObject);
        if(null == created){
            setFieldValByName("created",new Date(),metaObject);
        }
        Object updated = getFieldValByName("updated", metaObject);
        if(null == updated){
            setFieldValByName("updated",new Date(),metaObject);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
          setFieldValByName("updated",new Date(),metaObject);
    }
}
