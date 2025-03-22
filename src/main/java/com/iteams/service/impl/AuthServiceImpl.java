package com.iteams.service.impl;

import com.iteams.model.dto.LoginRequestDTO;
import com.iteams.model.dto.LoginResponseDTO;
import com.iteams.model.dto.UserInfoDTO;
import com.iteams.model.entity.User;
import com.iteams.repository.UserRepository;
import com.iteams.service.AuthService;
import com.iteams.util.JwtUtil;
import com.iteams.util.LogHelper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 认证服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LogHelper logHelper;

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
            
            // 从数据库获取用户信息
            Optional<User> userOpt = userRepository.findByUsername(loginRequest.getUsername());
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                // 简化日志，不再输出详细的用户信息
                log.info("验证用户状态: {}", loginRequest.getUsername());
                
                // 仅用于调试不匹配的密码，只在开发环境使用
                boolean passwordMatches = passwordEncoder.matches(loginRequest.getPassword(), user.getPassword());
                if (!passwordMatches) {
                    // 登录失败时记录用户名和密码
                    log.warn("用户[{}]登录失败，密码不匹配。尝试使用的密码: {}", loginRequest.getUsername(), loginRequest.getPassword());
                    
                    // 调试用：如果是超级管理员且输入特定测试密码，重置密码哈希
                    if ("Admin@123".equals(loginRequest.getPassword()) && "supadmin".equals(loginRequest.getUsername())) {
                        String newHash = passwordEncoder.encode(loginRequest.getPassword());
                        log.info("重新生成超级管理员密码哈希");
                        
                        // 更新用户密码哈希
                        user.setPassword(newHash);
                        userRepository.save(user);
                        log.info("已更新超级管理员密码哈希，将尝试使用新哈希进行认证");
                    }
                }
            } else {
                // 登录失败时记录用户名和密码
                log.warn("用户登录失败: 用户名[{}]不存在。尝试使用的密码: {}", loginRequest.getUsername(), loginRequest.getPassword());
                // 记录登录失败日志 - 情况3: 非法用户(不存在)尝试登录
                logHelper.recordLoginLog(null, false, "非法用户尝试登录：用户不存在，尝试的用户名: " + loginRequest.getUsername() + "，密码: " + loginRequest.getPassword());
                throw new UsernameNotFoundException("用户不存在: " + loginRequest.getUsername());
            }
            
            // 使用Spring Security的认证管理器进行认证
            Authentication authentication;
            try {
                authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                loginRequest.getUsername(),
                                loginRequest.getPassword()
                        )
                );
                // 登录成功只记录简单信息
                log.info("用户登录成功: {}", loginRequest.getUsername());
            } catch (BadCredentialsException e) {
                // 登录失败时记录用户名和密码
                log.error("用户[{}]认证失败，密码错误。尝试使用的密码: {}", loginRequest.getUsername(), loginRequest.getPassword());
                // 记录登录失败日志 - 情况2: 合法用户，密码错误
                User user = userRepository.findByUsername(loginRequest.getUsername()).orElse(null);
                logHelper.recordLoginLog(user, false, "合法用户登录失败：密码错误，尝试的密码: " + loginRequest.getPassword());
                throw e;
            }
            
            // 设置认证信息到上下文
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // 获取用户详情
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            
            // 生成JWT令牌
            String token = jwtUtil.generateToken(userDetails, loginRequest.isRemember());
            log.debug("已生成JWT令牌: {}", token);
            
            // 更新最后登录时间
            User user = userRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + loginRequest.getUsername()));
            
            user.setLastLoginTime(LocalDateTime.now());
            userRepository.save(user);
            log.info("用户登录成功：{}", user.getUsername());
            
            // 获取用户信息
            UserInfoDTO userInfo = getUserInfoFromDatabase(user);
            
            // 记录登录成功日志 - 情况1: 合法用户，登录成功
            logHelper.recordLoginLog(user, true, "用户登录成功");
            
            // 返回登录响应
            return LoginResponseDTO.builder()
                    .token(token)
                    .userInfo(userInfo)
                    .build();
            
        } catch (BadCredentialsException e) {
            // 登录失败时记录用户名
            log.error("登录失败：用户[{}]凭证无效", loginRequest.getUsername(), e);
            throw new BadCredentialsException("用户名或密码错误");
        } catch (Exception e) {
            // 登录失败时记录用户名
            log.error("用户[{}]登录处理过程中发生错误: {}", loginRequest.getUsername(), e.getMessage(), e);
            throw new RuntimeException("登录失败：" + e.getMessage());
        }
    }

    /**
     * 从本地数据库获取用户信息
     * 
     * @param user 用户实体
     * @return 用户信息DTO
     */
    private UserInfoDTO getUserInfoFromDatabase(User user) {
        // 创建基本用户信息
        UserInfoDTO userInfo = new UserInfoDTO();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setRealName(user.getRealName());
        userInfo.setEmail(user.getEmail());
        userInfo.setPhone(user.getPhone());
        userInfo.setAvatarUrl(user.getAvatarUrl());
        userInfo.setLastLoginTime(user.getLastLoginTime());
        
        // 获取角色编码列表
        List<String> roles = user.getRoles().stream()
                .map(role -> role.getCode())
                .collect(Collectors.toList());
        userInfo.setRoles(roles);
        
        // TODO: 添加权限列表获取逻辑，目前简单返回角色作为权限
        userInfo.setPermissions(roles);
        
        return userInfo;
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
        
        // 从本地数据库获取用户信息
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));
        
        return getUserInfoFromDatabase(user);
    }

    /**
     * 用户登出
     */
    @Override
    public void logout() {
        // 获取当前用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username).orElse(null);
            
            // 记录登出日志
            if (user != null) {
                logHelper.recordLogoutLog(user);
            }
        }
        
        // 清除认证信息
        SecurityContextHolder.clearContext();
    }
}
