package com.iteams.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 定时任务配置类
 * <p>
 * 启用Spring定时任务功能，用于定期执行系统维护任务
 * </p>
 */
@Configuration
@EnableScheduling
public class SchedulingConfig {
    // 配置类，启用Spring的@Scheduled注解功能
}
