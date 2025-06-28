package com.youyu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youyu.entity.BusinessLog;
import com.youyu.mapper.BusinessLogMapper;
import com.youyu.service.BusinessLogService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * (Logs)表服务实现类
 *
 * @author makejava
 * @since 2025-06-27 23:34:17
 */
@Service("logsService")
public class BusinessLogServiceImpl extends ServiceImpl<BusinessLogMapper, BusinessLog> implements BusinessLogService {

    @Resource
    private BusinessLogMapper businessLogMapper;

    @Async
    @Override
    public void saveLog(BusinessLog log) {
        businessLogMapper.insert(log);
    }
}

