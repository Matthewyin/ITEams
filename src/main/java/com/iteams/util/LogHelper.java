package com.iteams.util;

import com.iteams.model.entity.OperationLog;
import com.iteams.model.entity.User;
import com.iteams.model.enums.ModuleType;
import com.iteams.model.enums.OperationType;
import com.iteams.model.enums.StatusType;
import com.iteams.repository.OperationLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 日志帮助工具类
 * <p>
 * 提供快速记录系统操作日志的方法
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LogHelper {

    private final OperationLogRepository operationLogRepository;

    /**
     * 记录用户登录日志
     *
     * @param user 用户对象
     * @param success 是否成功
     * @param message 消息（成功或失败原因）
     */
    @Async
    public void recordLoginLog(User user, boolean success, String message) {
        try {
            OperationLog operationLog = new OperationLog();
            operationLog.setOperationType(OperationType.LOGIN);
            operationLog.setModule(ModuleType.AUTH);
            operationLog.setStatus(success ? StatusType.SUCCESS : StatusType.FAILED);
            operationLog.setOperationTime(LocalDateTime.now());
            
            if (user != null) {
                operationLog.setOperatorId(user.getId().toString());
                operationLog.setOperatorName(user.getUsername());
                operationLog.setObjectId(user.getId().toString());
                operationLog.setObjectType("User");
                
                // 添加用户信息到日志
                Map<String, Object> afterData = new HashMap<>();
                afterData.put("username", user.getUsername());
                afterData.put("userId", user.getId());
                afterData.put("loginResult", success ? "成功" : "失败");
                afterData.put("loginTime", LocalDateTime.now().toString());
                
                // 成功登录只记录基本信息
                if (success) {
                    afterData.put("message", "用户登录成功");
                } else {
                    // 登录失败时记录详细信息
                    afterData.put("message", message);
                    afterData.put("accountStatus", buildAccountStatus(user));
                    
                    // 尝试从消息中提取密码信息
                    if (message != null && message.contains("尝试的密码:")) {
                        String password = message.substring(message.indexOf("尝试的密码:") + 7).trim();
                        afterData.put("attemptedPassword", password);
                    }
                }
                
                operationLog.setAfterData(JsonUtil.toJson(afterData));
            } else {
                // 记录非法用户尝试登录的情况
                Map<String, Object> afterData = new HashMap<>();
                afterData.put("loginResult", "失败");
                afterData.put("loginTime", LocalDateTime.now().toString());
                afterData.put("message", message);
                afterData.put("reason", "用户不存在");
                
                // 提取用户名和密码信息（如果有）
                if (message != null) {
                    if (message.contains("尝试的用户名:")) {
                        int start = message.indexOf("尝试的用户名:") + 8;
                        int end = message.contains("，密码:") ? message.indexOf("，密码:") : message.length();
                        String attemptedUsername = message.substring(start, end).trim();
                        afterData.put("attemptedUsername", attemptedUsername);
                    }
                    
                    if (message.contains("，密码:")) {
                        String password = message.substring(message.indexOf("，密码:") + 4).trim();
                        afterData.put("attemptedPassword", password);
                    }
                }
                
                // 为非法用户使用特殊ID，避免operator_id为null
                operationLog.setOperatorId("0");
                operationLog.setObjectId("0");
                operationLog.setObjectType("UnknownUser");
                
                // 尝试获取输入的用户名
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (attributes != null) {
                    HttpServletRequest request = attributes.getRequest();
                    // 从请求中提取可能的用户名
                    String attemptedUsername = extractUsernameFromRequest(request);
                    if (attemptedUsername != null && !attemptedUsername.isEmpty()) {
                        afterData.put("attemptedUsername", attemptedUsername);
                        operationLog.setOperatorName(attemptedUsername + "(不存在)");
                    } else {
                        operationLog.setOperatorName("未知用户");
                    }
                } else {
                    operationLog.setOperatorName("未知用户");
                }
                
                operationLog.setAfterData(JsonUtil.toJson(afterData));
            }
            
            // 简化成功登录的描述
            if (success) {
                operationLog.setDescription("用户登录成功");
            } else {
                operationLog.setDescription(message);
            }
            
            // 获取请求信息
            setRequestInfo(operationLog);
            
            // 保存日志
            operationLogRepository.save(operationLog);
            
            // 简化日志输出
            if (success) {
                log.debug("登录成功: {}", user != null ? user.getUsername() : "未知用户");
            } else {
                log.debug("登录失败: {}, 原因: {}", 
                        user != null ? user.getUsername() : "未知用户", 
                        message);
            }
        } catch (Exception e) {
            log.error("记录登录日志失败", e);
        }
    }

    /**
     * 记录用户登出日志
     *
     * @param user 用户对象
     */
    @Async
    public void recordLogoutLog(User user) {
        try {
            OperationLog operationLog = new OperationLog();
            operationLog.setOperationType(OperationType.LOGOUT);
            operationLog.setModule(ModuleType.AUTH);
            operationLog.setStatus(StatusType.SUCCESS);
            operationLog.setOperationTime(LocalDateTime.now());
            
            if (user != null) {
                operationLog.setOperatorId(user.getId().toString());
                operationLog.setOperatorName(user.getUsername());
                operationLog.setObjectId(user.getId().toString());
                operationLog.setObjectType("User");
                
                // 添加更多用户信息到日志
                Map<String, Object> afterData = new HashMap<>();
                afterData.put("username", user.getUsername());
                afterData.put("userId", user.getId());
                afterData.put("logoutTime", LocalDateTime.now().toString());
                afterData.put("lastLoginTime", user.getLastLoginTime() != null ? 
                        user.getLastLoginTime().toString() : "未知");
                
                operationLog.setAfterData(JsonUtil.toJson(afterData));
            }
            
            operationLog.setDescription("用户登出系统");
            
            // 获取请求信息
            setRequestInfo(operationLog);
            
            // 保存日志
            operationLogRepository.save(operationLog);
            log.debug("已记录登出操作日志: {}", user != null ? user.getUsername() : "未知用户");
        } catch (Exception e) {
            log.error("记录登出日志失败", e);
        }
    }

    /**
     * 构建账号状态信息
     */
    private Map<String, Boolean> buildAccountStatus(User user) {
        Map<String, Boolean> status = new HashMap<>();
        status.put("enabled", user.isEnabled());
        status.put("accountNonExpired", user.isAccountNonExpired());
        status.put("accountNonLocked", user.isAccountNonLocked());
        status.put("credentialsNonExpired", user.isCredentialsNonExpired());
        return status;
    }

    /**
     * 设置请求相关信息（IP地址、用户代理等）
     *
     * @param operationLog 操作日志对象
     */
    private void setRequestInfo(OperationLog operationLog) {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                operationLog.setIpAddress(getClientIp(request));
                operationLog.setUserAgent(request.getHeader("User-Agent"));
            }
        } catch (Exception e) {
            log.warn("获取请求信息失败", e);
        }
    }

    /**
     * 获取客户端IP地址
     *
     * @param request HTTP请求对象
     * @return IP地址
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 处理多个IP的情况（只取第一个）
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    private String extractUsernameFromRequest(HttpServletRequest request) {
        // 尝试从参数获取
        String username = request.getParameter("username");
        if (username != null && !username.isEmpty()) {
            return username;
        }
        
        try {
            // 尝试从JSON请求体获取
            if (request.getContentType() != null && request.getContentType().contains("application/json")) {
                // 尝试读取请求体中的用户名
                Map<String, Object> userMap = JsonUtil.fromJson(request.getReader().lines()
                        .collect(Collectors.joining(System.lineSeparator())), Map.class);
                if (userMap != null && userMap.containsKey("username")) {
                    return userMap.get("username").toString();
                }
            }
        } catch (Exception e) {
            log.debug("无法从请求中提取用户名: {}", e.getMessage());
        }
        
        return null;
    }
} 