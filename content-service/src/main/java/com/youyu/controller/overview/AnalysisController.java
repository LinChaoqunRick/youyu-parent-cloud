package com.youyu.controller.overview;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.youyu.dto.analysis.AnalysisOverview;
import com.youyu.dto.overview.AreaAccessInput;
import com.youyu.entity.Logs;
import com.youyu.enums.LogType;
import com.youyu.enums.ResultCode;
import com.youyu.feign.UserServiceClient;
import com.youyu.result.ResponseResult;
import com.youyu.service.LogsService;
import com.youyu.service.album.AlbumService;
import com.youyu.service.message.MessageService;
import com.youyu.service.moment.MomentService;
import com.youyu.service.note.NoteService;
import com.youyu.service.post.PostService;
import com.youyu.utils.SecurityUtils;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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
    private LogsService logsService;

    @RequestMapping("/overview")
    public ResponseResult<?> overview() {
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
                albumCountFuture, todayVisitCountFuture, monthVisitCountFuture
        ).join();

        try {
            AnalysisOverview overview = new AnalysisOverview();
            overview.setUserNumber(userCountFuture.get());
            overview.setPostNumber(postCountFuture.get());
            overview.setMomentNumber(momentCountFuture.get());
            overview.setNoteNumber(noteCountFuture.get());
            overview.setMessageNumber(messageCountFuture.get());
            overview.setAlbumNumber(albumCountFuture.get());
            overview.setTodayVisitNumber(todayVisitCountFuture.get());
            overview.setMonthVisitNumber(monthVisitCountFuture.get());
            return ResponseResult.success(overview);
        } catch (Exception e) {
            // 可以返回失败响应或处理异常
            return ResponseResult.error(ResultCode.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("/areaAccess")
    public ResponseResult<Map<String, Long>> areaAccess(@Valid AreaAccessInput input) {
        QueryWrapper<Logs> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("adcode", "COUNT(*) AS cnt")
                .eq("type", LogType.ACCESS.getCode())
                .ge("create_time", input.getStartTime())
                .lt("create_time", input.getEndTime())
                .groupBy("adcode");

        List<Map<String, Object>> result = logsService.listMaps(queryWrapper);

        Map<String, Long> areaCountMap = result.stream()
                .collect(Collectors.toMap(
                        m -> String.valueOf(m.get("adcode")),
                        m -> ((Number) m.get("cnt")).longValue()
                ));
        return ResponseResult.success(areaCountMap);
    }
}
