package com.withered.mapper;

import com.withered.pojo.Account;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper  // 表明这是一个mabatis 的 mapper 类
public interface AccountMapper {
    List<Account> get();
    Account getAccountByName(String name);
}
