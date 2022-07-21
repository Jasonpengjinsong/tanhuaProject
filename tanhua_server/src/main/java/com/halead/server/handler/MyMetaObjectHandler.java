package com.halead.server.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;

import java.util.Date;

/**
 * @title: MyMetaObjectHandler
 * @Author ppjjss
 * @Date: 2022/6/30 0:08
 * @Version 1.0
 */
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
