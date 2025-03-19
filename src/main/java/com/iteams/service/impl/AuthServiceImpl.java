package com.iteams.service.impl;

import com.iteams.model.dto.LoginRequestDTO;
import com.iteams.model.dto.LoginResponseDTO;
import com.iteams.model.dto.UserInfoDTO;
import com.iteams.model.entity.User;
import com.iteams.repository.UserRepository;
import com.iteams.service.AuthService;
import com.iteams.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 认证服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    /**
     * 用户登录
     *
     * @param loginRequest 登录请求
     * @return 登录响应
     */
    @Override
    @Transactional
    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        try {
            log.info("开始认证用户: {}", loginRequest.getUsername());
            // 使用Spring Security的认证管理器进行认证
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );
            log.info("用户认证成功: {}", loginRequest.getUsername());
            
            // 设置认证信息到上下文
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // 获取用户详情
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            
            // 生成JWT令牌
            String token = jwtUtil.generateToken(userDetails, loginRequest.isRemember());
            
            // 更新最后登录时间
            userRepository.updateLastLoginTime(loginRequest.getUsername(), LocalDateTime.now());
            
            // 获取用户信息
            User user = userRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("用户不存在"));
            
            // 转换为DTO
            UserInfoDTO userInfoDTO = convertToUserInfoDTO(user);
            
            // 返回登录响应
            return LoginResponseDTO.builder()
                    .token(token)
                    .userInfo(userInfoDTO)
                    .build();
            
        } catch (BadCredentialsException e) {
            log.error("登录失败：用户名或密码错误", e);
            throw new BadCredentialsException("用户名或密码错误");
        } catch (Exception e) {
            log.error("登录失败: {}", e.getMessage(), e);
            throw new RuntimeException("登录失败：" + e.getMessage());
        }
    }

    /**
     * 获取当前用户信息
     *
     * @return 用户信息
     */
    @Override
    public UserInfoDTO getCurrentUserInfo() {
        // 获取当前认证信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("用户未登录");
        }
        
        // 获取用户名
        String username = authentication.getName();
        
        // 获取用户信息
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在"));
        
        // 转换为DTO
        return convertToUserInfoDTO(user);
    }

    /**
     * 用户登出
     */
    @Override
    public void logout() {
        // 清除认证信息
        SecurityContextHolder.clearContext();
    }

    /**
     * 将用户实体转换为DTO
     *
     * @param user 用户实体
     * @return 用户信息DTO
     */
    private UserInfoDTO convertToUserInfoDTO(User user) {
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setId(user.getId());
        userInfoDTO.setUsername(user.getUsername());
        userInfoDTO.setRealName(user.getRealName());
        userInfoDTO.setEmail(user.getEmail());
        userInfoDTO.setPhone(user.getPhone());
        userInfoDTO.setAvatarUrl(user.getAvatarUrl());
        userInfoDTO.setLastLoginTime(user.getLastLoginTime());
        userInfoDTO.setRoles(user.getRoles());
        userInfoDTO.setPermissions(user.getPermissions());
        return userInfoDTO;
    }
}
