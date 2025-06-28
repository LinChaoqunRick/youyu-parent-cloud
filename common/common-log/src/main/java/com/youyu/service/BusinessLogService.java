package com.youyu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youyu.entity.BusinessLog;

/**
 * (Logs)表服务接口
 *
 * @author makejava
 * @since 2025-06-27 23:34:16
 */
public interface BusinessLogService extends IService<BusinessLog> {
    void saveLog(BusinessLog log);
}

