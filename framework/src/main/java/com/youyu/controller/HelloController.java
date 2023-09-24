package com.youyu.controller;

import com.youyu.result.ResponseResult;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
public class HelloController {

    @RequestMapping("/test1")
//    @PreAuthorize("hasAnyAuthority('test')")
    public ResponseResult test1() {
        return ResponseResult.success("666666");
    }

    @RequestMapping("/test2")
    @PreAuthorize("hasAnyAuthority('test2')")
    public ResponseResult test2() {
//        int i = 1 / 0;
        return ResponseResult.success("666666");
    }

    @RequestMapping("/test3")
    public ResponseResult test3() {
        throw new RuntimeException("AAAAAA_RuntimeException");
    }

    @RequestMapping("/test4")
    @PreAuthorize("@ex.hasAuthority('test')")
    public ResponseResult test4() {
        return ResponseResult.success("666666");
    }
}
