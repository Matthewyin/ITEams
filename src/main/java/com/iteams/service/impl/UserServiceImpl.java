package com.iteams.service.impl;

import com.iteams.exception.ResourceNotFoundException;
import com.iteams.model.dto.PasswordDTO;
import com.iteams.model.dto.UserDTO;
import com.iteams.model.entity.Role;
import com.iteams.model.entity.User;
import com.iteams.repository.RoleRepository;
import com.iteams.repository.UserRepository;
import com.iteams.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final @Lazy PasswordEncoder passwordEncoder;

    /**
     * Spring Security认证方法
     *
     * @param username 用户名
     * @return UserDetails
     * @throws UsernameNotFoundException 用户不存在异常
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));

        // 获取用户权限
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        
        // 添加角色作为权限
        user.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getCode()));
            
            // 添加角色中的权限
            role.getPermissions().forEach(permission -> 
                authorities.add(new SimpleGrantedAuthority(permission.getCode()))
            );
        });

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.isEnabled(),
                user.isAccountNonExpired(),
                user.isCredentialsNonExpired(),
                user.isAccountNonLocked(),
                authorities);
    }

    /**
     * 获取用户分页列表
     */
    @Override
    public Page<UserDTO> getUsers(String username, String realName, String department, Pageable pageable) {
        Page<User> userPage = userRepository.findByConditions(username, realName, department, pageable);
        return userPage.map(this::convertToDto);
    }

    /**
     * 根据ID获取用户
     */
    @Override
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在: " + id));
        return convertToDto(user);
    }

    /**
     * 根据用户名获取用户
     */
    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在: " + username));
    }

    /**
     * 创建用户
     */
    @Override
    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        // 检查用户名是否存在
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new IllegalArgumentException("用户名已存在: " + userDTO.getUsername());
        }

        // 创建用户
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRealName(userDTO.getRealName());
        user.setEmail(userDTO.getEmail());
        user.setDepartment(userDTO.getDepartment());
        user.setCreatedAt(LocalDateTime.now());

        // 设置默认角色为普通用户
        Role userRole = roleRepository.findByCode("USER")
                .orElseThrow(() -> new ResourceNotFoundException("角色不存在: USER"));
        user.setRoles(Collections.singleton(userRole));

        // 保存用户
        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }

    /**
     * 更新用户
     */
    @Override
    @Transactional
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在: " + id));

        // 更新用户信息
        user.setRealName(userDTO.getRealName());
        user.setEmail(userDTO.getEmail());
        user.setDepartment(userDTO.getDepartment());
        user.setUpdatedAt(LocalDateTime.now());

        // 保存用户
        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }

    /**
     * 更新当前用户信息
     */
    @Override
    @Transactional
    public UserDTO updateCurrentUser(UserDTO userDTO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在: " + username));

        // 更新用户信息
        user.setRealName(userDTO.getRealName());
        user.setEmail(userDTO.getEmail());
        user.setDepartment(userDTO.getDepartment());
        user.setUpdatedAt(LocalDateTime.now());

        // 保存用户
        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }

    /**
     * 删除用户
     */
    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("用户不存在: " + id);
        }
        userRepository.deleteById(id);
    }

    /**
     * 重置用户密码
     */
    @Override
    @Transactional
    public String resetPassword(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在: " + id));

        // 生成随机密码
        String randomPassword = generateRandomPassword();
        
        // 更新密码
        user.setPassword(passwordEncoder.encode(randomPassword));
        user.setCredentialsNonExpired(false); // 要求用户下次登录时修改密码
        userRepository.save(user);

        return randomPassword;
    }

    /**
     * 修改当前用户密码
     */
    @Override
    @Transactional
    public void changePassword(PasswordDTO passwordDTO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在: " + username));

        // 验证原密码
        if (!passwordEncoder.matches(passwordDTO.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("原密码不正确");
        }

        // 验证新密码与确认密码是否一致
        if (!passwordDTO.getNewPassword().equals(passwordDTO.getConfirmPassword())) {
            throw new IllegalArgumentException("新密码与确认密码不一致");
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(passwordDTO.getNewPassword()));
        user.setCredentialsNonExpired(true);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    /**
     * 分配用户角色
     */
    @Override
    @Transactional
    public void assignRoles(Long userId, List<Long> roleIds) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在: " + userId));

        // 获取角色列表
        List<Role> roles = roleRepository.findAllById(roleIds);
        if (roles.isEmpty()) {
            throw new ResourceNotFoundException("未找到指定角色");
        }

        // 设置用户角色
        user.setRoles(new HashSet<>(roles));
        userRepository.save(user);
    }

    /**
     * 转换用户实体为DTO
     */
    private UserDTO convertToDto(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setRealName(user.getRealName());
        dto.setEmail(user.getEmail());
        dto.setDepartment(user.getDepartment());
        
        // 设置角色
        Set<String> roles = user.getRoles().stream()
                .map(Role::getCode)
                .collect(Collectors.toSet());
        dto.setRoles(roles);
        
        return dto;
    }

    /**
     * 生成随机密码
     */
    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            int index = random.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }

    /**
     * 重置用户密码（开发调试使用）
     * 
     * @param username 用户名
     * @param newPassword 新密码
     * @return 是否成功
     */
    @Transactional
    public boolean resetPasswordForDebug(String username, String newPassword) {
        try {
            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                user.setPassword(passwordEncoder.encode(newPassword));
                userRepository.save(user);
                log.info("已重置用户{}的密码", username);
                return true;
            } else {
                log.warn("找不到用户: {}", username);
                return false;
            }
        } catch (Exception e) {
            log.error("重置密码失败: {}", e.getMessage(), e);
            return false;
        }
    }
} 