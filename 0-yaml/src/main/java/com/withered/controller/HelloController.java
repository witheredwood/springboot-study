package com.withered.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

//    @GetMapping("/h1")
    public String h1() {
        System.out.println("-------------");
        System.out.println("spring boot 程序");
        return "spring boot 程序";
    }
}
