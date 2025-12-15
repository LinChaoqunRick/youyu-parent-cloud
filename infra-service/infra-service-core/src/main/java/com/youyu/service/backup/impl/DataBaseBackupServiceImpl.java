package com.youyu.service.backup.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.PutObjectRequest;
import com.youyu.config.DataBaseBackupProperties;
import com.youyu.config.OssProperties;
import com.youyu.dto.backup.DataBaseBackupInput;
import com.youyu.dto.backup.DataBaseBackupOutput;
import com.youyu.enums.ResultCode;
import com.youyu.exception.SystemException;
import com.youyu.factory.OssClientFactory;
import com.youyu.service.backup.DataBaseBackupService;
import com.youyu.service.mail.MailService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MySQL备份服务实现
 */
@Slf4j
@Service
public class DataBaseBackupServiceImpl implements DataBaseBackupService {

    private final DataBaseBackupProperties backupProperties;
    private final OssProperties ossProperties;
    private final OssClientFactory ossClientFactory;

    @Resource
    private MailService mailService;

    private static final DateTimeFormatter FILENAME_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    public DataBaseBackupServiceImpl(
            DataBaseBackupProperties backupProperties,
            OssProperties ossProperties,
            OssClientFactory ossClientFactory) {
        this.backupProperties = backupProperties;
        this.ossProperties = ossProperties;
        this.ossClientFactory = ossClientFactory;
    }

    @Override
    public DataBaseBackupOutput backup(DataBaseBackupInput input) {
        long startTime = System.currentTimeMillis();
        LocalDateTime backupTime = LocalDateTime.now();

        try {
            // 确保备份目录存在
            File backupDir = new File(backupProperties.getLocalBackupDir());
            if (!backupDir.exists()) {
                backupDir.mkdirs();
            }

            // 生成备份文件名
            String timestamp = backupTime.format(FILENAME_DATE_FORMAT);
            String fileName = "mysql_backup_" + timestamp + ".sql.gz";
            String localPath = backupProperties.getLocalBackupDir() + "/" + fileName;

            // 执行备份
            executeMysqlDump(input, localPath);

            // 获取文件信息
            File backupFile = new File(localPath);
            if (!backupFile.exists()) {
                throw new SystemException(ResultCode.OTHER_ERROR.getCode(), "备份文件创建失败");
            }

            long fileSize = backupFile.length();
            String fileSizeReadable = formatFileSize(fileSize);

            // 上传到OSS
            String ossPath = null;
            boolean uploadedToOss = false;
            if (input.getUploadToOss()) {
                ossPath = uploadToOss(backupFile, fileName);
                uploadedToOss = true;
                log.info("备份文件已上传到OSS: {}", ossPath);
            }

            // 异步清理过期备份
            cleanExpiredBackupsAsync();

            long duration = System.currentTimeMillis() - startTime;

            DataBaseBackupOutput result = DataBaseBackupOutput.builder()
                    .fileName(fileName)
                    .localPath(localPath)
                    .ossPath(ossPath)
                    .fileSize(fileSize)
                    .fileSizeReadable(fileSizeReadable)
                    .backupTime(backupTime)
                    .uploadedToOss(uploadedToOss)
                    .duration(duration)
                    .remark(input.getRemark())
                    .build();

            // 发送邮件通知
            sendEmailNotification(result);

            return result;

        } catch (Exception e) {
            log.error("MySQL备份失败", e);
            throw new SystemException(ResultCode.OTHER_ERROR.getCode(), "MySQL备份失败: " + e.getMessage());
        }
    }

    /**
     * 发送邮件通知
     */
    private void sendEmailNotification(DataBaseBackupOutput result) {
        try {
            if (Boolean.TRUE.equals(backupProperties.getEnableEmailNotify())
                    && StringUtils.hasText(backupProperties.getNotifyEmail())) {

                String[] emails = backupProperties.getNotifyEmail().split(",");
                for (String email : emails) {
                    email = email.trim();
                    if (StringUtils.hasText(email)) {
                        try {
                            mailService.sendDatabaseBackupMail(email, result);
                        } catch (Exception e) {
                            log.error("发送备份通知邮件失败，收件人: {}", email, e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("邮件通知发送异常", e);
        }
    }

    /**
     * 执行mysqldump命令
     */
    private void executeMysqlDump(DataBaseBackupInput input, String outputPath) throws Exception {
        // 构建mysqldump命令
        StringBuilder command = new StringBuilder("docker exec ");
        command.append(backupProperties.getContainerName());
        command.append(" mysqldump -u").append(backupProperties.getUsername());
        command.append(" -p").append(backupProperties.getPassword());

        // 判断是备份所有数据库还是指定数据库
        if (CollectionUtils.isEmpty(input.getDatabases())) {
            command.append(" --all-databases");
        } else {
            command.append(" --databases ").append(String.join(" ", input.getDatabases()));
        }

        // 添加mysqldump参数
        command.append(" --single-transaction")
                .append(" --quick")
                .append(" --lock-tables=false")
                .append(" --routines")
                .append(" --triggers")
                .append(" --events")
                .append(" --default-character-set=utf8mb4");

        // 添加gzip压缩
        command.append(" | gzip > ").append(outputPath);

        log.info("执行备份命令: {}", maskPassword(command.toString()));

        // 执行命令
        ProcessBuilder processBuilder = new ProcessBuilder("sh", "-c", command.toString());
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        // 读取输出
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                log.debug("备份输出: {}", line);
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("mysqldump执行失败，退出码: " + exitCode);
        }
    }

    /**
     * 上传备份文件到OSS
     */
    private String uploadToOss(File file, String fileName) {
        OSS ossClient = null;
        try {
            ossClient = ossClientFactory.getClient();
            String objectKey = backupProperties.getOssBackupPath() + "/" + fileName;

            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    ossProperties.getBucket(),
                    objectKey,
                    file
            );

            ossClient.putObject(putObjectRequest);

            return objectKey;
        } catch (Exception e) {
            log.error("上传OSS失败", e);
            throw new SystemException(ResultCode.OTHER_ERROR.getCode(), "上传OSS失败: " + e.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    @Override
    public int cleanExpiredBackups() {
        try {
            File backupDir = new File(backupProperties.getLocalBackupDir());
            if (!backupDir.exists() || !backupDir.isDirectory()) {
                return 0;
            }

            long currentTime = System.currentTimeMillis();
            long retentionMillis = backupProperties.getRetentionDays() * 24L * 60 * 60 * 1000;

            File[] files = backupDir.listFiles((dir, name) -> name.startsWith("mysql_backup_") && name.endsWith(".sql.gz"));
            if (files == null || files.length == 0) {
                return 0;
            }

            int deletedCount = 0;
            for (File file : files) {
                long fileAge = currentTime - file.lastModified();
                if (fileAge > retentionMillis) {
                    if (file.delete()) {
                        deletedCount++;
                        log.info("删除过期备份文件: {}", file.getName());
                    }
                }
            }

            return deletedCount;
        } catch (Exception e) {
            log.error("清理过期备份失败", e);
            return 0;
        }
    }

    @Override
    public List<DataBaseBackupOutput> listBackups() {
        try {
            File backupDir = new File(backupProperties.getLocalBackupDir());
            if (!backupDir.exists() || !backupDir.isDirectory()) {
                return new ArrayList<>();
            }

            File[] files = backupDir.listFiles((dir, name) -> name.startsWith("mysql_backup_") && name.endsWith(".sql.gz"));
            if (files == null || files.length == 0) {
                return new ArrayList<>();
            }

            List<DataBaseBackupOutput> backupList = new ArrayList<>();
            for (File file : files) {
                try {
                    Path path = file.toPath();
                    BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
                    LocalDateTime createTime = LocalDateTime.ofInstant(
                            Instant.ofEpochMilli(attrs.creationTime().toMillis()),
                            ZoneId.systemDefault()
                    );

                    backupList.add(DataBaseBackupOutput.builder()
                            .fileName(file.getName())
                            .localPath(file.getAbsolutePath())
                            .fileSize(file.length())
                            .fileSizeReadable(formatFileSize(file.length()))
                            .backupTime(createTime)
                            .build());
                } catch (Exception e) {
                    log.warn("读取备份文件信息失败: {}", file.getName(), e);
                }
            }

            // 按时间倒序排列
            return backupList.stream()
                    .sorted(Comparator.comparing(DataBaseBackupOutput::getBackupTime).reversed())
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("获取备份列表失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 异步清理过期备份
     */
    private void cleanExpiredBackupsAsync() {
        new Thread(() -> {
            try {
                int count = cleanExpiredBackups();
                if (count > 0) {
                    log.info("已清理 {} 个过期备份文件", count);
                }
            } catch (Exception e) {
                log.error("异步清理过期备份失败", e);
            }
        }).start();
    }

    /**
     * 格式化文件大小
     */
    private String formatFileSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", size / (1024.0 * 1024));
        } else {
            return String.format("%.2f GB", size / (1024.0 * 1024 * 1024));
        }
    }

    /**
     * 隐藏命令中的密码
     */
    private String maskPassword(String command) {
        return command.replaceAll("-p\\S+", "-p****");
    }
}