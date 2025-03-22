package com.iteams.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 认证提供者配置
 * <p>
 * 用于配置已有的DaoAuthenticationProvider，添加详细的日志输出
 * </p>
 */
@Slf4j
@Configuration
public class AuthProviderConfig {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final DaoAuthenticationProvider authenticationProvider;

    /**
     * 在构造函数中配置日志增强的认证提供者
     */
    public AuthProviderConfig(UserDetailsService userDetailsService, 
                          PasswordEncoder passwordEncoder, 
                          DaoAuthenticationProvider authenticationProvider) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationProvider = authenticationProvider;
        
        // 增强认证提供者的密码验证功能，添加日志
        configureAuthProvider(authenticationProvider);
    }
    
    /**
     * 配置认证提供者，添加详细日志
     */
    private void configureAuthProvider(DaoAuthenticationProvider provider) {
        // 确保使用正确的服务和编码器
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        
        // 添加密码验证日志
        log.info("已配置认证提供者 [{}] 使用密码编码器 [{}]", 
                provider.getClass().getSimpleName(), 
                passwordEncoder.getClass().getSimpleName());
    }
}
