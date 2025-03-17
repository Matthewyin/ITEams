package com.iteams.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类
 * <p>
 * 该配置类用于定义Spring MVC的全局行为，主要包括：
 * <ul>
 *   <li>跨域资源共享(CORS)策略配置</li>
 *   <li>定义允许的请求源、HTTP方法和请求头</li>
 *   <li>设置认证凭证的处理方式</li>
 * </ul>
 * 通过实现WebMvcConfigurer接口，我们可以自定义Spring MVC的默认行为，
 * 而不需要完全接管Spring MVC的配置。
 * </p>
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 配置跨域资源共享(CORS)
     * <p>
     * 跨域资源共享是一种安全机制，允许不同源的网页访问本服务的资源。
     * 在前后端分离的架构中，这个配置至关重要，因为前端应用通常运行在
     * 不同的域名或端口上。
     * </p>
     * <p>
     * 本配置具体定义了：
     * <ul>
     *   <li>允许的来源域名 - 目前配置为本地开发环境</li>
     *   <li>允许的HTTP方法 - GET, POST, PUT, DELETE等</li>
     *   <li>允许的请求头 - 所有请求头</li>
     *   <li>暴露的响应头 - 常用的内容类型和跨域相关头</li>
     *   <li>允许发送认证信息 - 如Cookies</li>
     *   <li>预检请求缓存时间 - 1小时</li>
     * </ul>
     * </p>
     * 
     * @param registry CORS配置注册表
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