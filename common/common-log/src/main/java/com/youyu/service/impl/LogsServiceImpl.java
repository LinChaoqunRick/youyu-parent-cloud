package com.youyu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youyu.entity.Logs;
import com.youyu.mapper.LogsMapper;
import com.youyu.service.LogsService;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;



/**
 * (Logs)表服务实现类
 *
 * @author makejava
 * @since 2025-06-27 23:34:17
 */
@Service("businessLogsService")
public class LogsServiceImpl extends ServiceImpl<LogsMapper, Logs> implements LogsService {

    @Resource
    private LogsMapper logsMapper;

    @Async
    @Override
    public void saveLog(Logs log) {
        logsMapper.insert(log);
    }
}

