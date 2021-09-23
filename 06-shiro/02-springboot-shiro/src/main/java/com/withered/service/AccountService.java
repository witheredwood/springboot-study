package com.withered.service;

import com.withered.pojo.Account;

import java.util.List;

public interface AccountService {
    List<Account> get();
    Account getAccountByName(String name);
}
