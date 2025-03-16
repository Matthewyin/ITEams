package com.iteams.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类
 * 处理跨域请求等全局Web配置
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 配置跨域请求
     * 允许前端应用访问后端API
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 应用到所有路径
                .allowedOrigins("http://localhost:3000") // 允许前端应用的域名
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH") // 允许的HTTP方法
                .allowedHeaders("*") // 允许所有请求头
                .exposedHeaders("Content-Type", "X-Requested-With", "accept", "Origin", 
                                "Access-Control-Request-Method", "Access-Control-Request-Headers",
                                "Access-Control-Allow-Origin") // 暴露这些响应头给客户端
                .allowCredentials(true) // 允许携带凭证信息（如cookies）
                .maxAge(3600); // 预检请求的有效期（秒）
    }
} 