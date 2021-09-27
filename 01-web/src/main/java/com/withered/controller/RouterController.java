package com.withered.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class RouterController {
    // 测试页面请求
    @GetMapping("/test")
    public String test() {
        return "test";
    }

    @RequestMapping({"/", "/index"})
    public String toLogin() {
        return  "index";
    }

    @RequestMapping("add")
    public String add() {
        return  "user/add";
    }
}
