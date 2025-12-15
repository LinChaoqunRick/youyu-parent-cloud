package com.youyu.controller.backup;

import com.youyu.annotation.Log;
import com.youyu.dto.backup.DataBaseBackupInput;
import com.youyu.dto.backup.DataBaseBackupOutput;
import com.youyu.enums.LogType;
import com.youyu.result.ResponseResult;
import com.youyu.service.backup.DataBaseBackupService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MySQL备份管理接口
 */
@Slf4j
@RefreshScope
@RestController
@RequestMapping("/backup")
public class DataBaseBackupController {

    @Resource
    private DataBaseBackupService dataBaseBackupService;

    /**
     * 手动备份MySQL数据库
     *
     * @param input 备份参数
     * @return 备份结果
     */
    @PostMapping("/database")
    @Log(title = "MySQL手动备份", type = LogType.BACKUP_DATABASE)
    public ResponseResult<DataBaseBackupOutput> backup(DataBaseBackupInput input) {
        DataBaseBackupOutput result = dataBaseBackupService.backup(input);
        return ResponseResult.success(result);
    }

    /**
     * 获取备份列表
     *
     * @return 备份文件列表
     */
    @GetMapping("/database/list")
    public ResponseResult<List<DataBaseBackupOutput>> listBackups() {
        List<DataBaseBackupOutput> backups = dataBaseBackupService.listBackups();
        return ResponseResult.success(backups);
    }

    /**
     * 清理过期备份
     *
     * @return 清理的文件数量
     */
    @DeleteMapping("/database/clean")
    @Log(title = "清理过期备份", type = LogType.DELETE)
    public ResponseResult<Map<String, Object>> cleanExpiredBackups() {
        int count = dataBaseBackupService.cleanExpiredBackups();

        Map<String, Object> result = new HashMap<>();
        result.put("deletedCount", count);
        result.put("message", "已清理 " + count + " 个过期备份文件");

        return ResponseResult.success(result);
    }
}