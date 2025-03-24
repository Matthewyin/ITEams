package com.iteams.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 登录安全配置类
 * <p>
 * 用于配置登录失败次数限制和账户锁定时间
 * </p>
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "iteams.security.login")
public class LoginSecurityConfig {

    /**
     * 最大登录失败次数，超过后锁定账户
     */
    private int maxFailAttempts = 5;

    /**
     * 账户锁定时长，单位分钟
     */
    private int lockDurationMinutes = 30;
}
