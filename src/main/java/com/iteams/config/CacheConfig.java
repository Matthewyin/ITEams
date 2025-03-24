package com.iteams.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 缓存配置类
 * <p>
 * 配置缓存管理器和缓存相关设置
 * </p>
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * 缓存管理器
     * <p>
     * 使用ConcurrentMapCacheManager作为默认的缓存实现
     * 在生产环境中，可以考虑使用Redis等分布式缓存
     * </p>
     *
     * @return 缓存管理器
     */
    @Bean
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        // 预先创建权限缓存
        cacheManager.setCacheNames(java.util.Arrays.asList("permissions"));
        return cacheManager;
    }
}
