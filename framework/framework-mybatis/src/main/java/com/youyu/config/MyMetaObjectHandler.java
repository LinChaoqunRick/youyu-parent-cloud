package com.youyu.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 *
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        // id createTime updateTime 字段与实体类中字段对应
        // this.setFieldValByName("id", UUID.randomUUID().toString().replaceAll("-", ""), metaObject);
        this.setFieldValByName("createTime", new Date(), metaObject);
        this.setFieldValByName("updateTime", new Date(), metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        // updateTime 字段与实体类中字段对应
        // this.setFieldValByName("updateTime", new Date(), metaObject);
    }
}

