package com.withered.controller;

import com.withered.mapper.AccountMapper;
import com.withered.pojo.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class AccountController {
    @Autowired
    private AccountMapper accountMapper;

    // 查询数据库所有数据
    @GetMapping("/get")
    public List<Account> get() {
        System.out.println("get() ------------ ");
        List<Account> list = accountMapper.get();
        return list;
    }

    @GetMapping("/getByName")
    public Account getAccountByName() {
        System.out.println("getById() ------------ ");
        Account acc = accountMapper.getAccountByName("root");
        return acc;
    }
}
