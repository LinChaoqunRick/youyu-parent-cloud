package com.youyu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youyu.entity.BusinessLog;
import com.youyu.mapper.LogMapper;
import com.youyu.service.LogService;
import org.springframework.stereotype.Service;

/**
 * (Message)表服务实现类
 *
 * @author makejava
 * @since 2024-02-20 22:04:04
 */
@Service("logService")
public class LogServiceImpl extends ServiceImpl<LogMapper, BusinessLog> implements LogService {

}
