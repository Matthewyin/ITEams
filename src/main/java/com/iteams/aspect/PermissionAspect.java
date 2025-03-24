package com.iteams.aspect;

import com.iteams.annotation.RequirePermission;
import com.iteams.exception.AccessDeniedException;
import com.iteams.model.entity.User;
import com.iteams.service.PermissionService;
import com.iteams.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;

/**
 * 权限验证切面
 * 用于拦截带有@RequirePermission注解的方法，验证当前用户是否具有所需权限
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class PermissionAspect {

    private final UserService userService;
    private final PermissionService permissionService;

    /**
     * 在执行带有@RequirePermission注解的方法前进行权限验证
     *
     * @param joinPoint 连接点
     * @param requirePermission 权限注解
     */
    @Before("@annotation(requirePermission)")
    public void checkPermission(JoinPoint joinPoint, RequirePermission requirePermission) {
        // 获取当前认证信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("用户未认证");
        }

        // 获取用户名
        String username = authentication.getName();
        log.debug("权限验证 - 用户: {}, 方法: {}", username, joinPoint.getSignature().getName());

        // 获取所需权限
        String[] requiredPermissions = requirePermission.value();
        if (requiredPermissions.length == 0) {
            // 如果没有指定权限，则直接放行
            return;
        }

        // 获取用户
        User user = userService.getUserByUsername(username);
        
        // 获取用户权限编码
        Set<String> userPermissions = permissionService.getUserPermissionCodes(user.getId());
        
        // 验证权限
        boolean hasPermission = false;
        RequirePermission.LogicalType logicalType = requirePermission.logical();
        
        if (logicalType == RequirePermission.LogicalType.ANY) {
            // 任意一个权限满足即可
            hasPermission = Arrays.stream(requiredPermissions)
                    .anyMatch(userPermissions::contains);
        } else {
            // 必须满足所有权限
            hasPermission = Arrays.stream(requiredPermissions)
                    .allMatch(userPermissions::contains);
        }
        
        // 记录权限验证结果
        if (hasPermission) {
            log.debug("权限验证通过 - 用户: {}, 方法: {}", username, joinPoint.getSignature().getName());
        } else {
            log.warn("权限验证失败 - 用户: {}, 方法: {}, 所需权限: {}", 
                    username, joinPoint.getSignature().getName(), Arrays.toString(requiredPermissions));
            throw new AccessDeniedException("没有足够的权限执行此操作");
        }
    }
}
