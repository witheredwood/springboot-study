package com.withered.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RouterController {
    // 测试thymeleaf页面请求
    @GetMapping("/test")
    public String test() {
        return "test";
    }
}
