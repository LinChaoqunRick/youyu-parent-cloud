package com.youyu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youyu.entity.user.Visitor;

/**
 * (Visitor)表服务接口
 *
 * @author makejava
 * @since 2025-09-16 17:27:20
 */
public interface VisitorService extends IService<Visitor> {
    Visitor getVisitorByEmail(String email);
    Visitor saveOrUpdateByEmail(Visitor visitor);
}

