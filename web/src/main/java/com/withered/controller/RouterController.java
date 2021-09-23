package com.withered.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class RouterController {
    @RequestMapping({"/", "/index"})
    public String toLogin() {
        return  "index";
    }

    @RequestMapping("add")
    public String add() {
        return  "user/add";
    }
}
