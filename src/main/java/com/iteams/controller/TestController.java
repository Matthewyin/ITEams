package com.iteams.controller;

import com.iteams.model.dto.ApiResponse;
import com.iteams.model.dto.UserInfoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试控制器
 * <p>
 * 用于测试系统功能，仅在开发环境使用
 * </p>
 */
@Slf4j
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    private final PasswordEncoder passwordEncoder;

    /**
     * 健康检查接口
     * 无需认证即可访问
     */
    @GetMapping("/health")
    public ApiResponse<String> healthCheck() {
        log.info("健康检查请求");
        return ApiResponse.success("系统运行正常");
    }

    /**
     * 模拟系统用户接口
     * 提供用于测试的用户登录时间更新和用户信息获取端点
     */
    @GetMapping("/mock-user-api")
    public ApiResponse<Map<String, Object>> mockUserApi() {
        log.info("模拟系统用户API请求");
        
        Map<String, Object> result = new HashMap<>();
        result.put("api_status", "可用");
        result.put("endpoints", new String[]{
                "/api/system/user/login-time",
                "/api/system/user/info"
        });
        
        // 获取当前认证信息（如果有）
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            result.put("authenticated", true);
            result.put("principal", auth.getName());
        } else {
            result.put("authenticated", false);
        }
        
        return ApiResponse.success("模拟用户API测试", result);
    }
    
    /**
     * 模拟用户信息获取接口
     * 无需实际调用远程服务，直接返回模拟数据
     */
    @GetMapping("/mock-user-info")
    public ApiResponse<UserInfoDTO> mockUserInfo() {
        log.info("模拟获取用户信息");
        
        UserInfoDTO userInfo = new UserInfoDTO();
        userInfo.setId(1L);
        userInfo.setUsername("supadmin");
        userInfo.setRealName("系统管理员");
        userInfo.setEmail("admin@iteams.com");
        
        return ApiResponse.success("模拟用户信息", userInfo);
    }

    /**
     * 测试密码匹配
     *
     * @param rawPassword 原始密码
     * @param storedHash 存储的哈希
     * @return 匹配结果
     */
    @GetMapping("/password-match")
    public ApiResponse<Map<String, Object>> testPasswordMatch(
            @RequestParam(defaultValue = "Admin@123") String rawPassword,
            @RequestParam(required = false) String storedHash) {
        
        if (storedHash == null) {
            storedHash = "$2a$10$0WPWBaXQvZ7SuUViXN5vSevqGLP8/i3rCsB6jyB8g78qH1emPukmK";
        }
        
        boolean matches = passwordEncoder.matches(rawPassword, storedHash);
        String newHash = passwordEncoder.encode(rawPassword);
        
        log.info("密码验证测试:");
        log.info("原始密码: {}", rawPassword);
        log.info("存储的哈希: {}", storedHash);
        log.info("密码是否匹配: {}", matches);
        log.info("新生成的哈希: {}", newHash);
        log.info("新哈希自检: {}", passwordEncoder.matches(rawPassword, newHash));
        
        Map<String, Object> result = new HashMap<>();
        result.put("rawPassword", rawPassword);
        result.put("storedHash", storedHash);
        result.put("matches", matches);
        result.put("newHash", newHash);
        result.put("newHashSelfCheck", passwordEncoder.matches(rawPassword, newHash));
        result.put("passwordEncoderClass", passwordEncoder.getClass().getName());
        
        return ApiResponse.success("密码验证测试", result);
    }

    /**
     * 生成密码哈希
     *
     * @param password 密码
     * @return 哈希结果
     */
    @GetMapping("/generate-hash")
    public ApiResponse<Map<String, Object>> generateHash(
            @RequestParam(defaultValue = "Admin@123") String password) {
        
        String hash = passwordEncoder.encode(password);
        
        log.info("密码哈希生成:");
        log.info("密码: {}", password);
        log.info("生成的哈希: {}", hash);
        
        Map<String, Object> result = new HashMap<>();
        result.put("password", password);
        result.put("hash", hash);
        result.put("passwordEncoderClass", passwordEncoder.getClass().getName());
        
        return ApiResponse.success("密码哈希生成成功", result);
    }
} 