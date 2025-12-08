package com.youyu.controller.overview;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.youyu.dto.analysis.AnalysisOverview;
import com.youyu.dto.overview.AreaAccessInput;
import com.youyu.dto.overview.AreaAccessOutput;
import com.youyu.dto.overview.ServerInfo;
import com.youyu.entity.Logs;
import com.youyu.enums.LogType;
import com.youyu.enums.ResultCode;
import com.youyu.exception.SystemException;
import com.youyu.feign.UserServiceClient;
import com.youyu.result.ResponseResult;
import com.youyu.service.LogsService;
import com.youyu.service.album.AlbumService;
import com.youyu.service.message.MessageService;
import com.youyu.service.moment.MomentCommentService;
import com.youyu.service.moment.MomentService;
import com.youyu.service.note.NoteService;
import com.youyu.service.post.CommentService;
import com.youyu.service.post.PostService;
import com.youyu.utils.LocateUtils;
import com.youyu.utils.SecurityUtils;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@RestController
@RequestMapping("/manage/monitor")
public class MonitorController {

    @Resource
    private LogsService logsService;

    @Resource
    private Executor executor;

    @Resource
    private UserServiceClient userServiceClient;

    @Resource
    private PostService postService;

    @Resource
    private MomentService momentService;

    @Resource
    private NoteService noteService;

    @Resource
    private MessageService messageService;

    @Resource
    private AlbumService albumService;

    @Resource
    private CommentService commentService;

    @Resource
    private MomentCommentService momentCommentService;

    @RequestMapping("/contentOverview")
    public ResponseResult<AnalysisOverview> contentOverview() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();

        LocalDate firstDayOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate firstDayOfNextMonth = firstDayOfMonth.plusMonths(1);
        LocalDateTime startOfMonth = firstDayOfMonth.atStartOfDay();
        LocalDateTime endOfMonth = firstDayOfNextMonth.atStartOfDay();

        // 获取用户数量
        CompletableFuture<Long> userCountFuture = SecurityUtils.supplyAsyncWithSecurityContext(
                () -> userServiceClient.getUserTotal().getData(), executor);
        // 获取文章数量
        CompletableFuture<Long> postCountFuture = SecurityUtils.supplyAsyncWithSecurityContext(
                postService::count, executor);
        // 获取时刻数量
        CompletableFuture<Long> momentCountFuture = SecurityUtils.supplyAsyncWithSecurityContext(
                momentService::count, executor);
        // 获取笔记数量
        CompletableFuture<Long> noteCountFuture = SecurityUtils.supplyAsyncWithSecurityContext(
                noteService::count, executor);
        // 获取留言数量
        CompletableFuture<Long> messageCountFuture = SecurityUtils.supplyAsyncWithSecurityContext(
                messageService::count, executor);
        // 获取相册数量
        CompletableFuture<Long> albumCountFuture = SecurityUtils.supplyAsyncWithSecurityContext(
                albumService::count, executor);
        // 获取文章评论数量
        CompletableFuture<Long> postCommentCountFuture = SecurityUtils.supplyAsyncWithSecurityContext(
                commentService::count, executor);
        // 获取时刻评论数量
        CompletableFuture<Long> momentCommentCountFuture = SecurityUtils.supplyAsyncWithSecurityContext(
                momentCommentService::count, executor);
        // 获取总游客数
        CompletableFuture<Long> totalVisitorCountFuture = SecurityUtils.supplyAsyncWithSecurityContext(
                () -> userServiceClient.getVisitorTotal().getData(), executor);
        // 获取总访问量
        CompletableFuture<Long> totalVisitCountFuture = SecurityUtils.supplyAsyncWithSecurityContext(
                () -> logsService.count(new LambdaQueryWrapper<Logs>()
                        .eq(Logs::getType, LogType.ACCESS.getCode())), executor);

        // 获取今日访问数量
        CompletableFuture<Long> todayVisitCountFuture = SecurityUtils.supplyAsyncWithSecurityContext(
                () -> logsService.count(new LambdaQueryWrapper<Logs>()
                        .eq(Logs::getType, LogType.ACCESS.getCode())
                        .ge(Logs::getCreateTime, startOfDay)
                        .lt(Logs::getCreateTime, endOfDay)), executor);

        // 获取本月访问数量
        CompletableFuture<Long> monthVisitCountFuture = SecurityUtils.supplyAsyncWithSecurityContext(
                () -> logsService.count(new LambdaQueryWrapper<Logs>()
                        .eq(Logs::getType, LogType.ACCESS.getCode())
                        .ge(Logs::getCreateTime, startOfMonth)
                        .lt(Logs::getCreateTime, endOfMonth)), executor);

        CompletableFuture.allOf(
                userCountFuture, postCountFuture, momentCountFuture, noteCountFuture, messageCountFuture,
                albumCountFuture, postCommentCountFuture, momentCommentCountFuture,
                totalVisitorCountFuture, totalVisitCountFuture,
                todayVisitCountFuture, monthVisitCountFuture
        ).join();

        try {
            AnalysisOverview overview = new AnalysisOverview();
            overview.setUserNumber(userCountFuture.get());
            overview.setPostNumber(postCountFuture.get());
            overview.setMomentNumber(momentCountFuture.get());
            overview.setNoteNumber(noteCountFuture.get());
            overview.setMessageNumber(messageCountFuture.get());
            overview.setAlbumNumber(albumCountFuture.get());
            overview.setPostCommentNumber(postCommentCountFuture.get());
            overview.setMomentCommentNumber(momentCommentCountFuture.get());
            overview.setTotalVisitorNumber(totalVisitorCountFuture.get());
            overview.setTotalVisitNumber(totalVisitCountFuture.get());
            overview.setTodayVisitNumber(todayVisitCountFuture.get());
            overview.setMonthVisitNumber(monthVisitCountFuture.get());
            return ResponseResult.success(overview);
        } catch (Exception e) {
            // 可以返回失败响应或处理异常
            throw new SystemException(ResultCode.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("/areaAccess")
    public ResponseResult<List<AreaAccessOutput>> areaAccess(@Valid AreaAccessInput input) {
        // 处理结束时间（左闭右开：endTime +1 天）
        LocalDate endDate = input.getEndTime().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .plusDays(1);
        Date endTimePlusOne = Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        QueryWrapper<Logs> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("adcode", "COUNT(*) AS cnt")
                .eq("type", LogType.ACCESS.getCode())
                .ne("adcode", "-1")
                .ge("create_time", input.getStartTime())
                .lt("create_time", endTimePlusOne)
                .groupBy("adcode");

        List<Map<String, Object>> result = logsService.listMaps(queryWrapper);

        List<AreaAccessOutput> accessOutputs = result.stream()
                .map((item) -> {
                    String adcode = item.get("adcode").toString();
                    String adName = LocateUtils.getNameByCode(adcode);
                    Long count = (Long) item.get("cnt");
                    return new AreaAccessOutput(adcode, adName, count);
                })
                .sorted(Comparator.comparing(AreaAccessOutput::getCount).reversed())
                .toList();

        return ResponseResult.success(accessOutputs);
    }

    /**
     * 获取服务器信息（CPU、内存、磁盘使用情况）
     */
    @RequestMapping("/serverInfo")
    public ResponseResult<ServerInfo> getServerInfo() {
        ServerInfo serverInfo = new ServerInfo();

        // 获取操作系统MXBean
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();

        // CPU信息
        ServerInfo.CpuInfo cpuInfo = new ServerInfo.CpuInfo();
        cpuInfo.setCores(Runtime.getRuntime().availableProcessors());
        cpuInfo.setSystemLoad(osBean.getSystemLoadAverage());

        // 尝试获取CPU使用率（需要使用com.sun.management.OperatingSystemMXBean）
        if (osBean instanceof com.sun.management.OperatingSystemMXBean sunOsBean) {
            double cpuUsage = sunOsBean.getCpuLoad() * 100;
            cpuInfo.setUsage(Math.round(cpuUsage * 100.0) / 100.0);
        } else {
            cpuInfo.setUsage(-1); // 无法获取
        }
        serverInfo.setCpu(cpuInfo);

        // 系统内存信息
        ServerInfo.MemoryInfo memoryInfo = new ServerInfo.MemoryInfo();
        if (osBean instanceof com.sun.management.OperatingSystemMXBean sunOsBean) {
            long totalMemory = sunOsBean.getTotalMemorySize();
            long freeMemory = sunOsBean.getFreeMemorySize();
            long usedMemory = totalMemory - freeMemory;

            memoryInfo.setTotal(totalMemory);
            memoryInfo.setFree(freeMemory);
            memoryInfo.setUsed(usedMemory);
            memoryInfo.setUsage(Math.round((double) usedMemory / totalMemory * 10000.0) / 100.0);
        }
        serverInfo.setMemory(memoryInfo);

        // JVM内存信息
        Runtime runtime = Runtime.getRuntime();
        ServerInfo.JvmInfo jvmInfo = new ServerInfo.JvmInfo();
        long jvmMax = runtime.maxMemory();
        long jvmTotal = runtime.totalMemory();
        long jvmFree = runtime.freeMemory();
        long jvmUsed = jvmTotal - jvmFree;

        jvmInfo.setMax(jvmMax);
        jvmInfo.setTotal(jvmTotal);
        jvmInfo.setFree(jvmFree);
        jvmInfo.setUsed(jvmUsed);
        jvmInfo.setUsage(Math.round((double) jvmUsed / jvmTotal * 10000.0) / 100.0);
        serverInfo.setJvm(jvmInfo);

        // 磁盘信息（获取根目录的磁盘空间）
        File disk = new File("/");
        ServerInfo.DiskInfo diskInfo = new ServerInfo.DiskInfo();
        long totalSpace = disk.getTotalSpace();
        long freeSpace = disk.getFreeSpace();
        long usedSpace = totalSpace - freeSpace;

        diskInfo.setTotal(totalSpace);
        diskInfo.setFree(freeSpace);
        diskInfo.setUsed(usedSpace);
        diskInfo.setUsage(Math.round((double) usedSpace / totalSpace * 10000.0) / 100.0);
        serverInfo.setDisk(diskInfo);

        return ResponseResult.success(serverInfo);
    }

    /**
     * 获取最近的访问记录
     */
    @RequestMapping("/recentAccess")
    public ResponseResult<List<Logs>> getRecentAccess() {
        // 查询最近10条访问记录
        List<Logs> logs = logsService.list(new LambdaQueryWrapper<Logs>()
                .eq(Logs::getType, LogType.ACCESS.getCode())
                .orderByDesc(Logs::getCreateTime)
                .last("LIMIT 20"));

        // 转换为输出对象
        List<Logs> result = logs.stream()
                .map(log -> {
                    if (log.getAdcode() != null) {
                        log.setAdName(LocateUtils.getNameByCode(String.valueOf(log.getAdcode())));
                    }
                    log.setDuration(log.getDuration());
                    log.setResult(log.getResult());
                    return log;
                })
                .toList();

        return ResponseResult.success(result);
    }
}
