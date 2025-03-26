package com.iteams.service.impl;

import com.iteams.exception.ResourceNotFoundException;
import com.iteams.model.dto.PasswordDTO;
import com.iteams.model.dto.UserDTO;
import com.iteams.model.entity.Department;
import com.iteams.model.entity.Role;
import com.iteams.model.entity.User;
import com.iteams.model.entity.UserGroup;
import com.iteams.repository.DepartmentRepository;
import com.iteams.repository.RoleRepository;
import com.iteams.repository.UserGroupRepository;
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
    private final DepartmentRepository departmentRepository;
    private final UserGroupRepository userGroupRepository;
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
        // 如果部门参数是数字，则按部门ID查询，否则按部门名称查询
        final Long[] departmentId = {null};
        if (department != null && !department.isEmpty()) {
            try {
                departmentId[0] = Long.parseLong(department);
            } catch (NumberFormatException e) {
                // 如果不是数字，则按名称查询部门
                departmentRepository.findByName(department)
                        .ifPresent(dept -> departmentId[0] = dept.getId());
            }
        }
        
        Page<User> userPage = userRepository.findByConditions(username, realName, departmentId[0], pageable);
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
        
        // 设置密码: 如果没有提供密码，使用默认密码 "12345678"，并设置首次登录需要修改密码
        String password = (userDTO.getPassword() == null || userDTO.getPassword().trim().isEmpty()) 
            ? "12345678" 
            : userDTO.getPassword();
            
        user.setPassword(passwordEncoder.encode(password));
        
        // 如果使用的是默认密码，或者明确指定需要修改密码，则设置为需要修改密码
        boolean needsPasswordChange = (password.equals("12345678") || Boolean.TRUE.equals(userDTO.getRequirePasswordChange()));
        user.setRequirePasswordChange(needsPasswordChange);
        
        user.setRealName(userDTO.getRealName());
        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        user.setCreatedAt(LocalDateTime.now());
        
        // 设置部门
        if (userDTO.getDepartmentId() != null) {
            Department department = departmentRepository.findById(userDTO.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("部门不存在: " + userDTO.getDepartmentId()));
            user.setDepartment(department);
        }
        
        // 设置用户组
        if (userDTO.getGroupIds() != null && !userDTO.getGroupIds().isEmpty()) {
            Set<UserGroup> groups = new HashSet<>(userGroupRepository.findAllById(userDTO.getGroupIds()));
            if (groups.isEmpty()) {
                throw new ResourceNotFoundException("未找到指定用户组");
            }
            user.setGroups(groups);
        }
        
        // 设置用户状态
        if (userDTO.getStatus() != null) {
            user.setEnabled(userDTO.getStatus() == 1);
        }

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
        user.setPhone(userDTO.getPhone());
        user.setUpdatedAt(LocalDateTime.now());
        
        // 更新部门
        if (userDTO.getDepartmentId() != null) {
            Department department = departmentRepository.findById(userDTO.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("部门不存在: " + userDTO.getDepartmentId()));
            user.setDepartment(department);
        } else {
            user.setDepartment(null);
        }
        
        // 更新用户组
        if (userDTO.getGroupIds() != null) {
            if (userDTO.getGroupIds().isEmpty()) {
                user.setGroups(new HashSet<>());
            } else {
                Set<UserGroup> groups = new HashSet<>(userGroupRepository.findAllById(userDTO.getGroupIds()));
                if (groups.isEmpty()) {
                    throw new ResourceNotFoundException("未找到指定用户组");
                }
                user.setGroups(groups);
            }
        }
        
        // 更新用户状态
        if (userDTO.getStatus() != null) {
            user.setEnabled(userDTO.getStatus() == 1);
        }

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
        user.setPhone(userDTO.getPhone());
        user.setUpdatedAt(LocalDateTime.now());
        
        // 普通用户不能修改自己的部门、用户组和状态，只有管理员可以

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
        user.setRequirePasswordChange(true); // 设置首次登录时需要修改密码标志
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

        // 验证新密码不能与默认密码相同
        if ("12345678".equals(passwordDTO.getNewPassword())) {
            throw new IllegalArgumentException("新密码不能与默认密码相同");
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(passwordDTO.getNewPassword()));
        user.setCredentialsNonExpired(true);
        user.setRequirePasswordChange(false); // 清除强制修改密码标记
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
        dto.setPhone(user.getPhone());
        dto.setAvatarUrl(user.getAvatarUrl());
        
        // 设置部门信息
        if (user.getDepartment() != null) {
            dto.setDepartmentId(user.getDepartment().getId());
            dto.setDepartmentName(user.getDepartment().getName());
        }
        
        // 设置用户组信息
        if (user.getGroups() != null && !user.getGroups().isEmpty()) {
            Set<Long> groupIds = user.getGroups().stream()
                    .map(UserGroup::getId)
                    .collect(Collectors.toSet());
            dto.setGroupIds(groupIds);
            
            Set<String> groupNames = user.getGroups().stream()
                    .map(UserGroup::getName)
                    .collect(Collectors.toSet());
            dto.setGroupNames(groupNames);
        }
        
        // 设置状态，将Boolean转换为Integer
        dto.setStatus(user.isEnabled() ? Integer.valueOf(1) : Integer.valueOf(0));
        
        // 设置账户锁定状态
        dto.setAccountNonLocked(user.isAccountNonLocked());
        
        // 设置是否需要首次登录修改密码
        dto.setRequirePasswordChange(user.getRequirePasswordChange());
        
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
    
    /**
     * 更新用户头像
     */
    @Override
    @Transactional
    public UserDTO updateUserAvatar(Long userId, String avatarUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在: " + userId));
        
        // 如果有旧头像，可以在这里添加删除旧头像的逻辑
        
        // 更新头像
        user.setAvatarUrl(avatarUrl);
        user.setUpdatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);
        
        return convertToDto(savedUser);
    }
    
    /**
     * 更新当前用户头像
     */
    @Override
    @Transactional
    public UserDTO updateCurrentUserAvatar(String avatarUrl) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在: " + username));
        
        // 如果有旧头像，可以在这里添加删除旧头像的逻辑
        
        // 更新头像
        user.setAvatarUrl(avatarUrl);
        user.setUpdatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);
        
        return convertToDto(savedUser);
    }
    
    /**
     * 批量创建用户
     */
    @Override
    @Transactional
    public List<UserDTO> batchCreateUsers(List<UserDTO> userDTOs) {
        List<UserDTO> createdUsers = new ArrayList<>();
        Set<String> existingUsernames = new HashSet<>();
        
        // 验证所有用户名是否重复
        for (UserDTO userDTO : userDTOs) {
            if (existingUsernames.contains(userDTO.getUsername()) || userRepository.existsByUsername(userDTO.getUsername())) {
                throw new IllegalArgumentException("用户名重复: " + userDTO.getUsername());
            }
            existingUsernames.add(userDTO.getUsername());
        }
        
        // 批量创建用户
        for (UserDTO userDTO : userDTOs) {
            try {
                // 创建用户
                User user = new User();
                user.setUsername(userDTO.getUsername());
                
                // 设置密码: 如果没有提供密码，使用默认密码 "12345678"，并设置首次登录需要修改密码
                String password = (userDTO.getPassword() == null || userDTO.getPassword().trim().isEmpty()) 
                    ? "12345678" 
                    : userDTO.getPassword();
                
                user.setPassword(passwordEncoder.encode(password));
                
                // 如果使用的是默认密码，或者明确指定需要修改密码，则设置为需要修改密码
                boolean needsPasswordChange = (password.equals("12345678") || Boolean.TRUE.equals(userDTO.getRequirePasswordChange()));
                user.setRequirePasswordChange(needsPasswordChange);
                
                user.setRealName(userDTO.getRealName());
                user.setEmail(userDTO.getEmail());
                user.setPhone(userDTO.getPhone());
                user.setCreatedAt(LocalDateTime.now());
                
                // 设置部门
                if (userDTO.getDepartmentId() != null) {
                    Department department = departmentRepository.findById(userDTO.getDepartmentId())
                            .orElseThrow(() -> new ResourceNotFoundException("部门不存在: " + userDTO.getDepartmentId()));
                    user.setDepartment(department);
                }
                
                // 设置用户组
                if (userDTO.getGroupIds() != null && !userDTO.getGroupIds().isEmpty()) {
                    Set<UserGroup> groups = new HashSet<>(userGroupRepository.findAllById(userDTO.getGroupIds()));
                    if (groups.isEmpty()) {
                        throw new ResourceNotFoundException("未找到指定用户组");
                    }
                    user.setGroups(groups);
                }
                
                // 设置用户状态
                if (userDTO.getStatus() != null) {
                    user.setEnabled(userDTO.getStatus() == 1);
                }
                
                // 设置角色
                if (userDTO.getRoles() != null && !userDTO.getRoles().isEmpty()) {
                    Set<Role> roles = new HashSet<>();
                    for (String roleCode : userDTO.getRoles()) {
                        Role role = roleRepository.findByCode(roleCode)
                                .orElseThrow(() -> new ResourceNotFoundException("角色不存在: " + roleCode));
                        roles.add(role);
                    }
                    user.setRoles(roles);
                } else {
                    // 设置默认角色为普通用户
                    Role userRole = roleRepository.findByCode("USER")
                            .orElseThrow(() -> new ResourceNotFoundException("角色不存在: USER"));
                    user.setRoles(Collections.singleton(userRole));
                }
                
                // 保存用户
                User savedUser = userRepository.save(user);
                createdUsers.add(convertToDto(savedUser));
            } catch (Exception e) {
                // 单个用户创建失败，继续处理下一个
                log.error("创建用户[{}]失败: {}", userDTO.getUsername(), e.getMessage(), e);
            }
        }
        
        return createdUsers;
    }
    
    /**
     * 批量删除用户
     */
    @Override
    @Transactional
    public int batchDeleteUsers(List<Long> ids) {
        log.info("批量删除用户, 数量: {}", ids.size());
        
        // 检查用户是否存在
        List<User> users = userRepository.findAllById(ids);
        if (users.size() != ids.size()) {
            // 找出不存在的用户ID
            Set<Long> existingIds = users.stream()
                    .map(User::getId)
                    .collect(Collectors.toSet());
            
            List<Long> nonExistingIds = ids.stream()
                    .filter(id -> !existingIds.contains(id))
                    .collect(Collectors.toList());
            
            log.warn("部分用户不存在: {}", nonExistingIds);
        }
        
        // 删除用户
        userRepository.deleteAllById(ids);
        log.info("批量删除用户成功, 数量: {}", users.size());
        
        return users.size();
    }
    
    /**
     * 批量启用用户
     */
    @Override
    @Transactional
    public int batchEnableUsers(List<Long> ids) {
        log.info("批量启用用户, 数量: {}", ids.size());
        
        // 获取用户列表
        List<User> users = userRepository.findAllById(ids);
        if (users.isEmpty()) {
            log.warn("未找到要启用的用户");
            return 0;
        }
        
        // 启用用户
        int count = 0;
        for (User user : users) {
            if (!user.isEnabled()) {
                user.setEnabled(true);
                user.setUpdatedAt(LocalDateTime.now());
                count++;
            }
        }
        
        // 保存用户
        userRepository.saveAll(users);
        log.info("批量启用用户成功, 实际启用数量: {}", count);
        
        return count;
    }
    
    /**
     * 批量禁用用户
     */
    @Override
    @Transactional
    public int batchDisableUsers(List<Long> ids) {
        log.info("批量禁用用户, 数量: {}", ids.size());
        
        // 获取用户列表
        List<User> users = userRepository.findAllById(ids);
        if (users.isEmpty()) {
            log.warn("未找到要禁用的用户");
            return 0;
        }
        
        // 禁用用户
        int count = 0;
        for (User user : users) {
            if (user.isEnabled()) {
                user.setEnabled(false);
                user.setUpdatedAt(LocalDateTime.now());
                count++;
            }
        }
        
        // 保存用户
        userRepository.saveAll(users);
        log.info("批量禁用用户成功, 实际禁用数量: {}", count);
        
        return count;
    }
    
    /**
     * 批量分配角色
     */
    @Override
    @Transactional
    public int batchAssignRoles(List<Long> userIds, List<Long> roleIds) {
        log.info("批量分配角色, 用户数量: {}, 角色数量: {}", userIds.size(), roleIds.size());
        
        // 获取用户列表
        List<User> users = userRepository.findAllById(userIds);
        if (users.isEmpty()) {
            log.warn("未找到要分配角色的用户");
            return 0;
        }
        
        // 获取角色列表
        List<Role> roles = roleRepository.findAllById(roleIds);
        if (roles.isEmpty()) {
            log.warn("未找到要分配的角色");
            return 0;
        }
        
        // 分配角色
        Set<Role> roleSet = new HashSet<>(roles);
        for (User user : users) {
            user.setRoles(roleSet);
            user.setUpdatedAt(LocalDateTime.now());
        }
        
        // 保存用户
        userRepository.saveAll(users);
        log.info("批量分配角色成功, 分配用户数量: {}", users.size());
        
        return users.size();
    }
    
    /**
     * 解锁用户账户
     *
     * @param userId 用户ID
     * @return 解锁后的用户
     */
    @Override
    @Transactional
    public UserDTO unlockUserAccount(Long userId) {
        log.info("开始解锁用户账户，用户ID: {}", userId);
        
        // 获取当前用户信息（执行解锁操作的管理员）
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new UsernameNotFoundException("当前用户不存在: " + currentUsername));
        
        // 获取要解锁的用户
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在，ID: " + userId));
        
        // 检查当前用户是否有权限解锁目标用户
        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(role -> "ROLE_ADMIN".equals(role.getName()) || "ROLE_SUPER_ADMIN".equals(role.getName()) || 
                               "ADMIN".equals(role.getName()) || "SUPER_ADMIN".equals(role.getName()));
        
        // 检查目标用户是否是超级管理员
        boolean isTargetSuperAdmin = user.getRoles().stream()
                .anyMatch(role -> "ROLE_SUPER_ADMIN".equals(role.getName()) || "SUPER_ADMIN".equals(role.getName()));
        
        // 检查当前用户是否是超级管理员
        boolean isCurrentSuperAdmin = currentUser.getRoles().stream()
                .anyMatch(role -> "ROLE_SUPER_ADMIN".equals(role.getName()) || "SUPER_ADMIN".equals(role.getName()));
        
        // 如果目标用户是超级管理员，且当前用户不是超级管理员，则无权解锁
        if (isTargetSuperAdmin && !isCurrentSuperAdmin) {
            log.warn("用户[{}]尝试解锁超级管理员[{}]账户，权限不足", currentUsername, user.getUsername());
            throw new SecurityException("您没有权限解锁超级管理员账户");
        }
        
        // 如果当前用户不是管理员，则无权解锁任何账户
        if (!isAdmin) {
            log.warn("非管理员用户[{}]尝试解锁用户[{}]账户", currentUsername, user.getUsername());
            throw new SecurityException("您没有权限解锁用户账户");
        }
        
        // 检查账户是否已锁定
        if (user.isAccountNonLocked()) {
            log.info("用户[{}]账户已经处于解锁状态", user.getUsername());
            return convertToDto(user);
        }
        
        // 解锁账户
        user.setAccountNonLocked(true);
        user.setLockTime(null);
        user.setLoginFailCount(0);
        User savedUser = userRepository.save(user);
        
        log.info("用户[{}]账户已被管理员[{}]手动解锁", user.getUsername(), currentUsername);
        
        return convertToDto(savedUser);
    }
} 