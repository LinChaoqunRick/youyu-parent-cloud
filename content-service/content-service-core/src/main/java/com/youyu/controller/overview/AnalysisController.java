package com.youyu.controller.overview;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.youyu.dto.analysis.*;
import com.youyu.entity.Logs;
import com.youyu.entity.moment.MomentComment;
import com.youyu.entity.post.Comment;
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
import com.youyu.utils.SecurityUtils;
import com.youyu.utils.LocateUtils;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/manage/analysis")
public class AnalysisController {

    @Resource
    private Executor executor;

    @Resource
    private UserServiceClient userServiceClient;

    @Resource
    private LogsService logsService;

    @Resource
    private CommentService commentService;

    @Resource
    private MomentCommentService momentCommentService;

    @RequestMapping("/detail")
    public ResponseResult<AnalysisDetail> analysis() {
        try {
            // 异步并发执行所有查询任务
            CompletableFuture<List<VisitData>> monthlyVisitsFuture = SecurityUtils.supplyAsyncWithSecurityContext(
                    this::getMonthlyVisits, executor);

            CompletableFuture<List<VisitData>> dailyVisitsFuture = SecurityUtils.supplyAsyncWithSecurityContext(
                    this::getDailyVisits, executor);

            CompletableFuture<List<RegionData>> monthlyRegionVisitsFuture = SecurityUtils.supplyAsyncWithSecurityContext(
                    this::getMonthlyRegionVisits, executor);

            CompletableFuture<List<RegionData>> dailyRegionVisitsFuture = SecurityUtils.supplyAsyncWithSecurityContext(
                    this::getDailyRegionVisits, executor);

            CompletableFuture<List<CommentData>> monthlyCommentsFuture = SecurityUtils.supplyAsyncWithSecurityContext(
                    this::getMonthlyComments, executor);

            // 新增：按月统计新增游客趋势
            CompletableFuture<List<VisitData>> monthlyNewVisitorsFuture = SecurityUtils.supplyAsyncWithSecurityContext(
                    () -> userServiceClient.getMonthlyNewVisitors().getData(), executor);

            // 新增：按省份统计游客地域分布
            CompletableFuture<List<RegionData>> visitorsByProvinceFuture = SecurityUtils.supplyAsyncWithSecurityContext(
                    () -> userServiceClient.getVisitorsByProvince().getData(), executor);

            // 等待所有异步任务完成
            CompletableFuture.allOf(
                    monthlyVisitsFuture, dailyVisitsFuture, monthlyRegionVisitsFuture,
                    dailyRegionVisitsFuture, monthlyCommentsFuture, monthlyNewVisitorsFuture,
                    visitorsByProvinceFuture
            ).join();

            // 组装结果
            AnalysisDetail analysisDetail = new AnalysisDetail();
            analysisDetail.setMonthlyVisits(monthlyVisitsFuture.get());
            analysisDetail.setDailyVisits(dailyVisitsFuture.get());
            analysisDetail.setMonthlyRegionVisits(monthlyRegionVisitsFuture.get());
            analysisDetail.setDailyRegionVisits(dailyRegionVisitsFuture.get());
            analysisDetail.setMonthlyComments(monthlyCommentsFuture.get());
            analysisDetail.setMonthlyNewVisitors(monthlyNewVisitorsFuture.get());
            analysisDetail.setVisitorsByProvince(visitorsByProvinceFuture.get());

            return ResponseResult.success(analysisDetail);
        } catch (Exception e) {
            throw new SystemException(ResultCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 获取本年每月访问量
     */
    private List<VisitData> getMonthlyVisits() {
        List<VisitData> result = new ArrayList<>();
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

        for (int i = 0; i < 12; i++) {
            YearMonth yearMonth = YearMonth.now().minusMonths(11 - i);
            LocalDate firstDay = yearMonth.atDay(1);
            LocalDate lastDay = yearMonth.atEndOfMonth();

            LocalDateTime startOfMonth = firstDay.atStartOfDay();
            LocalDateTime endOfMonth = lastDay.plusDays(1).atStartOfDay();

            long count = logsService.count(new LambdaQueryWrapper<Logs>()
                    .eq(Logs::getType, LogType.ACCESS.getCode())
                    .ge(Logs::getCreateTime, startOfMonth)
                    .lt(Logs::getCreateTime, endOfMonth));

            VisitData data = new VisitData();
            data.setTime(yearMonth.format(formatter));
            data.setVisitCount(count);
            result.add(data);
        }

        return result;
    }

    /**
     * 获取本月每天访问量
     */
    private List<VisitData> getDailyVisits() {
        List<VisitData> result = new ArrayList<>();
        LocalDate now = LocalDate.now();
        LocalDate firstDay = now.withDayOfMonth(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (int i = 0; i < now.lengthOfMonth(); i++) {
            LocalDate date = firstDay.plusDays(i);
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

            long count = logsService.count(new LambdaQueryWrapper<Logs>()
                    .eq(Logs::getType, LogType.ACCESS.getCode())
                    .ge(Logs::getCreateTime, startOfDay)
                    .lt(Logs::getCreateTime, endOfDay));

            VisitData data = new VisitData();
            data.setTime(date.format(formatter));
            data.setVisitCount(count);
            result.add(data);
        }

        return result;
    }

    /**
     * 获取本月区域访问量前10
     */
    private List<RegionData> getMonthlyRegionVisits() {
        LocalDate now = LocalDate.now();
        LocalDate firstDay = now.withDayOfMonth(1);
        LocalDate lastDay = now.withDayOfMonth(now.lengthOfMonth());

        LocalDateTime startOfMonth = firstDay.atStartOfDay();
        LocalDateTime endOfMonth = lastDay.plusDays(1).atStartOfDay();

        List<Logs> logs = logsService.list(new LambdaQueryWrapper<Logs>()
                .eq(Logs::getType, LogType.ACCESS.getCode())
                .ge(Logs::getCreateTime, startOfMonth)
                .lt(Logs::getCreateTime, endOfMonth)
                .select(Logs::getAdcode));

        // 按省份统计访问量（将 adcode 转换为省份级别）
        Map<Integer, Long> regionMap = logs.stream()
                .filter(log -> log.getAdcode() != null)
                .collect(Collectors.groupingBy(
                        log -> getProvinceAdcode(log.getAdcode()),
                        Collectors.counting()));

        // 排序取前10
        return regionMap.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(10)
                .map(entry -> {
                    RegionData data = new RegionData();
                    data.setAdcode(entry.getKey());
                    data.setAdName(LocateUtils.getShortNameByCode(String.valueOf(entry.getKey())));
                    data.setVisitCount(entry.getValue());
                    return data;
                })
                .collect(Collectors.toList());
    }

    /**
     * 获取本日区域访问量前10
     */
    private List<RegionData> getDailyRegionVisits() {
        LocalDate now = LocalDate.now();
        LocalDateTime startOfDay = now.atStartOfDay();
        LocalDateTime endOfDay = now.plusDays(1).atStartOfDay();

        List<Logs> logs = logsService.list(new LambdaQueryWrapper<Logs>()
                .eq(Logs::getType, LogType.ACCESS.getCode())
                .ge(Logs::getCreateTime, startOfDay)
                .lt(Logs::getCreateTime, endOfDay)
                .select(Logs::getAdcode));

        // 按省份统计访问量（将 adcode 转换为省份级别）
        Map<Integer, Long> regionMap = logs.stream()
                .filter(log -> log.getAdcode() != null)
                .collect(Collectors.groupingBy(
                        log -> getProvinceAdcode(log.getAdcode()),
                        Collectors.counting()));

        // 排序取前10
        return regionMap.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(10)
                .map(entry -> {
                    RegionData data = new RegionData();
                    data.setAdcode(entry.getKey());
                    data.setAdName(LocateUtils.getShortNameByCode(String.valueOf(entry.getKey())));
                    data.setVisitCount(entry.getValue());
                    return data;
                })
                .collect(Collectors.toList());
    }

    /**
     * 将行政编码转换为省份级别（前6位数字表示省份）
     * @param adcode 原始行政编码
     * @return 省份级别的 adcode（最后4位为00）
     */
    private Integer getProvinceAdcode(Integer adcode) {
        if (adcode == null) {
            return null;
        }
        // 将 adcode 转换为 6 位数字，然后取前 4 位，后 2 位设为 00
        String addcodeStr = String.format("%06d", adcode);
        if (addcodeStr.length() >= 4) {
            // 取前4位，后面补00（即删除后两位，保留前四位）
            return Integer.parseInt(addcodeStr.substring(0, 4) + "00");
        }
        return adcode;
    }

    /**
     * 获取每月评论量（文章评论和时刻评论分开）
     */
    private List<CommentData> getMonthlyComments() {
        List<CommentData> result = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

        for (int i = 0; i < 12; i++) {
            YearMonth yearMonth = YearMonth.now().minusMonths(11 - i);
            LocalDate firstDay = yearMonth.atDay(1);
            LocalDate lastDay = yearMonth.atEndOfMonth();

            LocalDateTime startOfMonth = firstDay.atStartOfDay();
            LocalDateTime endOfMonth = lastDay.plusDays(1).atStartOfDay();

            // 统计文章评论数量
            long postCommentCount = commentService.count(new LambdaQueryWrapper<Comment>()
                    .ge(Comment::getCreateTime, startOfMonth)
                    .lt(Comment::getCreateTime, endOfMonth)
                    .eq(Comment::getDeleted, 0));

            // 统计时刻评论数量
            long momentCommentCount = momentCommentService.count(new LambdaQueryWrapper<MomentComment>()
                    .ge(MomentComment::getCreateTime, startOfMonth)
                    .lt(MomentComment::getCreateTime, endOfMonth)
                    .eq(MomentComment::getDeleted, 0));

            CommentData data = new CommentData();
            data.setMonth(yearMonth.format(formatter));
            data.setPostCommentCount(postCommentCount);
            data.setMomentCommentCount(momentCommentCount);
            result.add(data);
        }

        return result;
    }
}
