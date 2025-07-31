package com.yefeng.yefengaicode.controller;

import com.yefeng.yefengaicode.common.BaseResponse;
import com.yefeng.yefengaicode.common.ResultUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthController {
    @GetMapping("/get")
    public BaseResponse health(String name) {
        return ResultUtils.success("ok");
    }
}
