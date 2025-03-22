package com.iteams.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 身份验证配置类
 * <p>
 * 提供与身份验证相关的bean配置，用于解决循环依赖问题
 * </p>
 */
@Configuration
public class AuthConfig {

    /**
     * 认证提供者
     *
     * @param userDetailsService 用户详情服务
     * @param passwordEncoder 密码编码器
     * @return 认证提供者
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    /**
     * 认证管理器
     *
     * @param authenticationProvider 认证提供者
     * @return 认证管理器
     */
    @Bean
    @Primary
    public AuthenticationManager authenticationManager(DaoAuthenticationProvider authenticationProvider) {
        return new ProviderManager(authenticationProvider);
    }
} 