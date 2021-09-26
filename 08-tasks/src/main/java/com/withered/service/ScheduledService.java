package com.withered.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ScheduledService {
    // 在一个特定的时间执行。每隔5秒执行一次
    @Scheduled(cron = "0/5 * * * * ?")  // 秒 分 时 日 月 周几
    public void hello() {
        System.out.println("这个hello service 方法被执行了");
    }
}
