package com.youyu.dto.backup;

import lombok.Data;

import java.util.Arrays;
import java.util.List;

/**
 * MySQL备份请求参数
 */
@Data
public class DataBaseBackupInput {
    /**
     * 是否上传到OSS（默认true）
     */
    private Boolean uploadToOss = true;

    /**
     * 要备份的数据库名称列表（默认备份 youyu 和 nacos 数据库）
     */
    private List<String> databases = Arrays.asList("youyu", "nacos");

    /**
     * 备份备注
     */
    private String remark;
}