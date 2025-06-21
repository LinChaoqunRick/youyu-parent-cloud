package com.youyu.controller.moment;


import com.youyu.entity.moment.MomentCommentLike;
import com.youyu.result.ResponseResult;
import com.youyu.service.moment.MomentCommentLikeService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * (MomentCommentLike)表控制层
 *
 * @author makejava
 * @since 2023-07-10 22:25:25
 */
@RestController
@RequestMapping("/momentCommentLike")
public class MomentCommentLikeController {
    @Resource
    private MomentCommentLikeService momentCommentLikeService;

    @RequestMapping("/setMomentCommentLike")
    public ResponseResult<Boolean> setMomentCommentLike(@Valid MomentCommentLike input) {
        boolean save = momentCommentLikeService.setMomentCommentLike(input);
        return ResponseResult.success(save);

    }

    @RequestMapping("/cancelMomentCommentLike")
    public ResponseResult<Boolean> cancelMomentCommentLike(@Valid MomentCommentLike input) {
        boolean remove = momentCommentLikeService.cancelMomentCommentLike(input);
        return ResponseResult.success(remove);

    }

    @RequestMapping("/rectifySupportCount")
    public ResponseResult<String> rectifySupportCount(){
        momentCommentLikeService.rectifySupportCount();
        return ResponseResult.success("校正完成！");
    }
}

