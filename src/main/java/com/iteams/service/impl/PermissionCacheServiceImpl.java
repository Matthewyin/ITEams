package com.iteams.service.impl;

import com.iteams.service.PermissionCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * 权限缓存服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "permissions")
public class PermissionCacheServiceImpl implements PermissionCacheService {

    private final CacheManager cacheManager;
    private static final String CACHE_NAME = "permissions";
    private static final String USER_PERMISSION_KEY_PREFIX = "user_permissions_";

    /**
     * 获取用户权限编码集合
     * 如果缓存中存在，则从缓存中获取
     * 如果缓存中不存在，则返回null（由调用方从数据库加载并缓存）
     *
     * @param userId 用户ID
     * @return 权限编码集合
     */
    @Override
    @Cacheable(key = "'user_permissions_' + #userId")
    public Set<String> getUserPermissionCodes(Long userId) {
        log.debug("从缓存获取用户权限, 用户ID: {}", userId);
        // 该方法不会被实际调用，因为@Cacheable注解会拦截方法调用
        // 如果缓存中存在对应的值，则直接返回缓存值
        // 如果缓存中不存在，则执行方法体，并将返回值放入缓存
        return null;
    }

    /**
     * 缓存用户权限编码集合
     *
     * @param userId 用户ID
     * @param permissionCodes 权限编码集合
     */
    @Override
    public void cacheUserPermissionCodes(Long userId, Set<String> permissionCodes) {
        log.debug("缓存用户权限, 用户ID: {}, 权限数量: {}", userId, permissionCodes.size());
        Cache cache = cacheManager.getCache(CACHE_NAME);
        if (cache != null) {
            cache.put(USER_PERMISSION_KEY_PREFIX + userId, permissionCodes);
        } else {
            log.warn("缓存管理器中未找到缓存: {}", CACHE_NAME);
        }
    }

    /**
     * 清除用户权限缓存
     *
     * @param userId 用户ID
     */
    @Override
    @CacheEvict(key = "'user_permissions_' + #userId")
    public void clearUserPermissionCache(Long userId) {
        log.debug("清除用户权限缓存, 用户ID: {}", userId);
        // 方法体为空，因为@CacheEvict注解会处理缓存清除
    }

    /**
     * 清除所有权限缓存
     */
    @Override
    @CacheEvict(allEntries = true)
    public void clearAllPermissionCache() {
        log.debug("清除所有权限缓存");
        // 方法体为空，因为@CacheEvict注解会处理缓存清除
    }
}
