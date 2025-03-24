package com.iteams.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import java.time.Duration;

/**
 * Web配置类
 * <p>
 * 该配置类用于定义Spring MVC的全局行为，主要包括：
 * <ul>
 *   <li>跨域资源共享(CORS)策略配置</li>
 *   <li>定义允许的请求源、HTTP方法和请求头</li>
 *   <li>设置认证凭证的处理方式</li>
 *   <li>配置Spring Data Web支持，处理分页序列化</li>
 * </ul>
 * 通过实现WebMvcConfigurer接口，我们可以自定义Spring MVC的默认行为，
* 而不需要完全接管Spring MVC的配置。
 * </p>
 */
@Configuration
@EnableSpringDataWebSupport
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
                // 允许多个前端开发环境的域名
                .allowedOrigins("http://localhost:3000", "http://127.0.0.1:3000", "http://localhost:8080") 
                // Spring Boot 2.4+ 支持的通配符模式
                .allowedOriginPatterns("*") 
                // 允许的HTTP方法
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH") 
                // 允许所有请求头
                .allowedHeaders("*") 
                // 暴露这些响应头给客户端，增加Authorization头
                .exposedHeaders("Content-Type", "X-Requested-With", "accept", "Origin", 
                                "Access-Control-Request-Method", "Access-Control-Request-Headers",
                                "Access-Control-Allow-Origin", "Authorization") 
                // 允许携带凭证信息（如cookies）
                .allowCredentials(true) 
                // 预检请求的有效期（秒）
                .maxAge(3600); 
    }

    /**
     * 配置RestTemplate用于远程API调用
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
            .setConnectTimeout(Duration.ofSeconds(5))
            .setReadTimeout(Duration.ofSeconds(5))
            .build();
    }

    /**
     * 配置静态资源处理器
     * 
     * @param registry 资源处理器注册表
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .addResourceLocations("classpath:/public/")
                .addResourceLocations("classpath:/resources/")
                .addResourceLocations("classpath:/META-INF/resources/");
    }
} 