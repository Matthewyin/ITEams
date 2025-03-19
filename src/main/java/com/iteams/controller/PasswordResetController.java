package com.iteams.controller;

import com.iteams.model.dto.ApiResponse;
import com.iteams.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 密码重置控制器
 * <p>
 * 用于重置用户密码（临时接口，仅用于开发环境）
 * </p>
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public/reset")
public class PasswordResetController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 重置用户密码
     *
     * @param username 用户名
     * @param password 新密码
     * @return 重置结果
     */
    @PostMapping("/password")
    public ApiResponse<Map<String, Object>> resetPassword(
            @RequestParam String username,
            @RequestParam String password) {
        
        log.info("尝试重置用户密码: {}", username);
        
        return userRepository.findByUsername(username)
                .map(user -> {
                    // 获取原始密码哈希值
                    String oldPasswordHash = user.getPassword();
                    
                    // 生成新的密码哈希值
                    String newPasswordHash = passwordEncoder.encode(password);
                    
                    // 检查新密码是否与原始密码匹配
                    boolean matches = passwordEncoder.matches(password, oldPasswordHash);
                    
                    // 更新用户密码
                    user.setPassword(newPasswordHash);
                    userRepository.save(user);
                    
                    Map<String, Object> result = new HashMap<>();
                    result.put("username", username);
                    result.put("oldPasswordHash", oldPasswordHash);
                    result.put("newPasswordHash", newPasswordHash);
                    result.put("matches", matches);
                    
                    log.info("用户密码重置成功: {}", username);
                    log.info("原始密码哈希: {}", oldPasswordHash);
                    log.info("新密码哈希: {}", newPasswordHash);
                    log.info("密码匹配结果: {}", matches);
                    
                    return ApiResponse.success("密码重置成功", result);
                })
                .orElseGet(() -> {
                    log.error("用户不存在: {}", username);
                    return ApiResponse.error("用户不存在");
                });
    }
}
