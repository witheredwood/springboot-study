package com.withered.service;

import com.withered.mapper.AccountMapper;
import com.withered.pojo.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    AccountMapper accountMapper;
    @Override
    public List<Account> get() {
        return accountMapper.get();
    }

    @Override
    public Account getAccountByName(String name) {
        return accountMapper.getAccountByName(name);
    }
}
