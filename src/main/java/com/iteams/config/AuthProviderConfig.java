package com.iteams.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 认证提供者配置
 * <p>
 * 用于配置DaoAuthenticationProvider，添加详细的日志输出
 * </p>
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class AuthProviderConfig {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    /**
     * 配置DaoAuthenticationProvider
     *
     * @return DaoAuthenticationProvider实例
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider() {
            @Override
            protected void additionalAuthenticationChecks(org.springframework.security.core.userdetails.UserDetails userDetails,
                                                        org.springframework.security.authentication.UsernamePasswordAuthenticationToken authentication) {
                log.info("开始进行密码验证...");
                log.info("用户名: {}", userDetails.getUsername());
                log.info("用户密码哈希值: {}", userDetails.getPassword());
                log.info("输入的凭证是否为空: {}", authentication.getCredentials() == null);
                
                if (authentication.getCredentials() != null) {
                    String presentedPassword = authentication.getCredentials().toString();
                    log.info("输入的密码: {}", presentedPassword);
                    
                    // 使用密码编码器手动验证密码
                    boolean matches = passwordEncoder.matches(presentedPassword, userDetails.getPassword());
                    log.info("密码是否匹配: {}", matches);
                }
                
                try {
                    super.additionalAuthenticationChecks(userDetails, authentication);
                    log.info("密码验证成功");
                } catch (Exception e) {
                    log.error("密码验证失败: {}", e.getMessage(), e);
                    throw e;
                }
            }
        };
        
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }
}
