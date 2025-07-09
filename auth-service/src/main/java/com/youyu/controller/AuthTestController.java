package com.youyu.controller;

import com.youyu.result.ResponseResult;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/test")
public class AuthTestController {

    @RequestMapping("/testAccess")
    public ResponseResult<String> testAccess() {
        return ResponseResult.success("testAccess");
    }

    @PreAuthorize("hasAuthority('test')")
    @RequestMapping("/hasAuthTest")
    public ResponseResult<String> hasAuthTest() {
        return ResponseResult.success("OK Success");
    }

    @PreAuthorize("hasAuthority('test123')")
    @RequestMapping("/noAuthTest")
    public ResponseResult<String> noAuthTest() {
        return ResponseResult.success("OK Success");
    }
}
