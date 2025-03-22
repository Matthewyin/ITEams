package com.iteams.controller;

import com.iteams.model.dto.ApiResponse;
import com.iteams.model.dto.LoginRequestDTO;
import com.iteams.model.dto.LoginResponseDTO;
import com.iteams.model.dto.UserInfoDTO;
import com.iteams.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// 删除未使用的导入

/**
 * 认证控制器
 * <p>
 * 处理用户登录、登出和获取用户信息的请求
 * </p>
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 用户登录
     *
     * @param loginRequest 登录请求
     * @return 登录响应
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> login(
            @Valid @RequestBody LoginRequestDTO loginRequest,
            HttpServletRequest request) {
        log.info("用户登录请求: {}", loginRequest.getUsername());
        LoginResponseDTO response = authService.login(loginRequest);
        return ResponseEntity.ok(ApiResponse.success("登录成功", response));
    }

    /**
     * 获取当前用户信息
     *
     * @return 用户信息
     */
    @GetMapping("/user")
    public ResponseEntity<ApiResponse<UserInfoDTO>> getUserInfo() {
        UserInfoDTO userInfo = authService.getCurrentUserInfo();
        return ResponseEntity.ok(ApiResponse.success("获取用户信息成功", userInfo));
    }

    /**
     * 用户登出
     *
     * @return 登出响应
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        authService.logout();
        return ResponseEntity.ok(ApiResponse.success("登出成功", null));
    }
}
