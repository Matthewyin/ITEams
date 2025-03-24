package com.iteams.service;

import java.util.Set;

/**
 * 权限缓存服务接口
 * 用于缓存用户权限信息，提高权限验证性能
 */
public interface PermissionCacheService {

    /**
     * 获取用户权限编码集合
     *
     * @param userId 用户ID
     * @return 权限编码集合
     */
    Set<String> getUserPermissionCodes(Long userId);

    /**
     * 缓存用户权限编码集合
     *
     * @param userId 用户ID
     * @param permissionCodes 权限编码集合
     */
    void cacheUserPermissionCodes(Long userId, Set<String> permissionCodes);

    /**
     * 清除用户权限缓存
     *
     * @param userId 用户ID
     */
    void clearUserPermissionCache(Long userId);

    /**
     * 清除所有权限缓存
     */
    void clearAllPermissionCache();
}
