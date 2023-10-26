package com.youyu.controller;

import com.youyu.result.ResponseResult;
import com.youyu.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.mail.MessagingException;

@RestController
@RequestMapping("/mail")
public class MailController {
    @Resource
    private MailService mailService;

    @ResponseBody
    @RequestMapping("/sendRegisterCode")
    public ResponseResult<Boolean> sendRegisterCode(@RequestParam String target,
                                                    @RequestParam(required = false, defaultValue = "false") boolean repeat)
            throws MessagingException {
        Boolean result = mailService.sendRegisterCode(target, repeat);
        return ResponseResult.success(result);
    }

    @ResponseBody
    @RequestMapping("/testError")
    public ResponseResult<Boolean> testError() {
        int a = 1 / 0;
        return ResponseResult.success(false);
    }

    @ResponseBody
    @RequestMapping("/testAccess")
    @PreAuthorize("hasAuthority('test')")
    public ResponseResult<Boolean> testAccess() {
        return ResponseResult.success(true);
    }

    @ResponseBody
    @RequestMapping("/testAccess2")
    @PreAuthorize("hasAuthority('test2')")
    public ResponseResult<Boolean> testAccess2() {
        return ResponseResult.success(true);
    }

    @ResponseBody
    @RequestMapping("/testAccess3")
    public ResponseResult<Boolean> testAccess3() {
        return ResponseResult.success(true);
    }
}
