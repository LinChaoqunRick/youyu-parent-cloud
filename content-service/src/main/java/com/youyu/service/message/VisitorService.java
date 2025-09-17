package com.youyu.service.message;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youyu.dto.message.MessageUserOutput;
import com.youyu.entity.Visitor;

/**
 * (Visitor)表服务接口
 *
 * @author makejava
 * @since 2025-09-16 17:27:20
 */
public interface VisitorService extends IService<Visitor> {
    Visitor getVisitorByEmail(String email);
}

