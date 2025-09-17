package com.youyu.service.message.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youyu.entity.Visitor;
import com.youyu.mapper.message.VisitorMapper;
import com.youyu.service.message.VisitorService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * (Visitor)表服务实现类
 *
 * @author makejava
 * @since 2025-09-16 17:27:20
 */
@Service("visitorService")
public class VisitorServiceImpl extends ServiceImpl<VisitorMapper, Visitor> implements VisitorService {

    @Resource
    private VisitorMapper visitorMapper;

    @Override
    public Visitor getVisitorByEmail(String email) {
        LambdaQueryWrapper<Visitor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Visitor::getEmail, email);
        return visitorMapper.selectOne(queryWrapper);
    }
}

