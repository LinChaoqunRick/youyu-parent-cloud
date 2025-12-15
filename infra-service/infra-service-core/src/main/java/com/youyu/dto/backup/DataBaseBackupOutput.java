package com.youyu.dto.backup;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * MySQL备份响应结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataBaseBackupOutput {
    /**
     * 备份文件名
     */
    private String fileName;

    /**
     * 本地文件路径
     */
    private String localPath;

    /**
     * OSS文件路径
     */
    private String ossPath;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 文件大小（可读格式）
     */
    private String fileSizeReadable;

    /**
     * 备份时间
     */
    private LocalDateTime backupTime;

    /**
     * 是否上传到OSS
     */
    private Boolean uploadedToOss;

    /**
     * 备份耗时（毫秒）
     */
    private Long duration;

    /**
     * 备份备注
     */
    private String remark;
}