package com.youyu.dto.overview;

import lombok.Data;

/**
 * 服务器信息
 */
@Data
public class ServerInfo {
    /**
     * CPU信息
     */
    private CpuInfo cpu;

    /**
     * 内存信息
     */
    private MemoryInfo memory;

    /**
     * JVM内存信息
     */
    private JvmInfo jvm;

    /**
     * 磁盘信息
     */
    private DiskInfo disk;

    /**
     * CPU信息
     */
    @Data
    public static class CpuInfo {
        /**
         * CPU核心数
         */
        private int cores;

        /**
         * CPU使用率（百分比）
         */
        private double usage;

        /**
         * 系统负载
         */
        private double systemLoad;
    }

    /**
     * 内存信息
     */
    @Data
    public static class MemoryInfo {
        /**
         * 总内存（字节）
         */
        private long total;

        /**
         * 已使用内存（字节）
         */
        private long used;

        /**
         * 空闲内存（字节）
         */
        private long free;

        /**
         * 使用率（百分比）
         */
        private double usage;
    }

    /**
     * JVM内存信息
     */
    @Data
    public static class JvmInfo {
        /**
         * JVM最大内存（字节）
         */
        private long max;

        /**
         * JVM总内存（字节）
         */
        private long total;

        /**
         * JVM已使用内存（字节）
         */
        private long used;

        /**
         * JVM空闲内存（字节）
         */
        private long free;

        /**
         * 使用率（百分比）
         */
        private double usage;
    }

    /**
     * 磁盘信息
     */
    @Data
    public static class DiskInfo {
        /**
         * 总空间（字节）
         */
        private long total;

        /**
         * 已使用空间（字节）
         */
        private long used;

        /**
         * 空闲空间（字节）
         */
        private long free;

        /**
         * 使用率（百分比）
         */
        private double usage;
    }
}