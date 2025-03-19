package com.iteams.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 用户信息工具类
 * <p>
 * 提供获取当前登录用户信息的功能
 * </p>
 */
public class UserInfoUtil {
    
    /**
     * 获取当前登录用户ID
     *
     * @return 用户ID
     */
    public static String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                return ((UserDetails) principal).getUsername();
            } else {
                return principal.toString();
            }
        }
        return "system"; // 默认值，表示系统操作
    }
    
    /**
     * 获取当前登录用户名称
     *
     * @return 用户名称
     */
    public static String getCurrentUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return "系统"; // 默认值，表示系统操作
    }
} 