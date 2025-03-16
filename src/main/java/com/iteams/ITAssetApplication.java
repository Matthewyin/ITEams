package com.iteams;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * IT资产管理系统启动类
 */
@EnableAsync
@SpringBootApplication
@EnableTransactionManagement
public class ITAssetApplication {

    public static void main(String[] args) {
        SpringApplication.run(ITAssetApplication.class, args);
    }
} 