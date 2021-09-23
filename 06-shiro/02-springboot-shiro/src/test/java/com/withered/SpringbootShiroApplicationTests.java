package com.withered;

import com.withered.pojo.Account;
import com.withered.service.AccountServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class SpringbootShiroApplicationTests {

    @Autowired
    AccountServiceImpl accountService;
    @Test
    void contextLoads() {
//        System.out.println(accountService.getAccountByName("root"));
        List<Account> list = accountService.get();
        for (int i = 0; i < list.size(); i++) {
            System.out.println("name: " + list.get(i).getName() + " pwd: " + list.get(i).getPwd());
        }
    }

    @Test
    void getAccountByName() {
        Account acc = accountService.getAccountByName("root");
        System.out.println("name: " + acc.getName() + " pwd: " + acc.getPwd());
    }

}
