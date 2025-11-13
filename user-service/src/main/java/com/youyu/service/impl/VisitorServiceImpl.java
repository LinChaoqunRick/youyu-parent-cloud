package com.youyu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youyu.entity.user.Visitor;
import com.youyu.mapper.VisitorMapper;
import com.youyu.service.VisitorService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Date;

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

    @Override
    public Visitor saveOrUpdateByEmail(Visitor visitor) {
        LambdaQueryWrapper<Visitor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Visitor::getEmail, visitor.getEmail());
        Visitor selectOne = visitorMapper.selectOne(queryWrapper);
        if (selectOne != null) {
            visitor.setUpdateTime(new Date());
            visitor.setId(selectOne.getId());
            visitorMapper.updateById(visitor);
        } else {
            visitorMapper.insert(visitor);
        }
        return visitor;
    }
}

