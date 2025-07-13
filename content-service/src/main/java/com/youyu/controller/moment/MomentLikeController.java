package com.youyu.controller.moment;


import com.youyu.entity.moment.MomentLike;
import com.youyu.enums.ResultCode;
import com.youyu.exception.SystemException;
import com.youyu.result.ResponseResult;
import com.youyu.service.moment.MomentLikeService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;


/**
 * (MomentLike)表控制层
 *
 * @author makejava
 * @since 2023-07-02 11:20:02
 */
@RestController
@RequestMapping("/momentLike")
public class MomentLikeController {
    /**
     * 服务对象
     */
    @Resource
    private MomentLikeService momentLikeService;

    @RequestMapping("/setMomentLike")
    public ResponseResult<Boolean> setMomentLike(@Valid MomentLike input) {
        boolean like = momentLikeService.setMomentLike(input);
        return ResponseResult.success(like);
    }

    @RequestMapping("/isMomentLike")
    public ResponseResult<Boolean> isMomentLike(@Valid MomentLike input) {
        boolean like = momentLikeService.isMomentLike(input);
        return ResponseResult.success(like);
    }

    @RequestMapping("/cancelMomentLike")
    public ResponseResult<Boolean> cancelPostLike(@Valid MomentLike input) {
        boolean remove = momentLikeService.cancelMomentLike(input);
        if (remove) {
            return ResponseResult.success(remove);
        } else {
            throw new SystemException(ResultCode.OPERATION_FAIL);
        }
    }

    @RequestMapping("/rectifySupportCount")
    public ResponseResult<String> rectifySupportCount() {
        momentLikeService.rectifySupportCount();
        return ResponseResult.success("校正完成！");
    }
}

