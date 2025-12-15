package com.youyu.service.backup;

import com.youyu.dto.backup.DataBaseBackupInput;
import com.youyu.dto.backup.DataBaseBackupOutput;

import java.util.List;

/**
 * MySQL备份服务
 */
public interface DataBaseBackupService {
    /**
     * 执行MySQL备份
     *
     * @param input 备份参数
     * @return 备份结果
     */
    DataBaseBackupOutput backup(DataBaseBackupInput input);

    /**
     * 清理过期的本地备份
     *
     * @return 清理的文件数量
     */
    int cleanExpiredBackups();

    /**
     * 获取备份列表
     *
     * @return 备份文件列表
     */
    List<DataBaseBackupOutput> listBackups();
}