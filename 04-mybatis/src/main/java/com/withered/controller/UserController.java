package com.withered.controller;

import com.withered.mapper.UserMapper;
import com.withered.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class UserController {
    @Autowired
    private UserMapper userMapper;

    // 查询数据库所有数据
    @GetMapping("/get")
    public List<User> get() {
        System.out.println("get() ------------ ");
        List<User> list = userMapper.get();
        return list;
    }

    @GetMapping("/getById")
    public User getById() {
        System.out.println("getById() ------------ ");
        User user = userMapper.getUserById(2);
        return user;
    }
}
