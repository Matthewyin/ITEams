package com.iteams.service.impl;

import com.iteams.exception.ResourceNotFoundException;
import com.iteams.model.entity.User;
import com.iteams.repository.UserRepository;
import com.iteams.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {

    // 默认角色列表
    private static final List<RoleInfo> ROLES = Arrays.asList(
        new RoleInfo("admin", "管理员"),
        new RoleInfo("user", "普通用户")
    );
    
    // 默认密码
    private static final String DEFAULT_PASSWORD = "123456";
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    @Override
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    @Override
    public Page<User> getUsers(String query, String role, Integer status, Pageable pageable) {
        // 由于继承JpaRepository，可以直接使用JpaSpecificationExecutor的findAll方法
        return userRepository.findAll((Specification<User>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 关键字搜索
            if (StringUtils.hasText(query)) {
                Predicate usernamePredicate = criteriaBuilder.like(root.get("username"), "%" + query + "%");
                Predicate realNamePredicate = criteriaBuilder.like(root.get("realName"), "%" + query + "%");
                Predicate emailPredicate = criteriaBuilder.like(root.get("email"), "%" + query + "%");
                predicates.add(criteriaBuilder.or(usernamePredicate, realNamePredicate, emailPredicate));
            }
            
            // 角色筛选
            if (StringUtils.hasText(role)) {
                predicates.add(criteriaBuilder.isMember(role, root.get("roles")));
            }
            
            // 状态筛选
            if (status != null) {
                boolean enabled = status == 1;
                predicates.add(criteriaBuilder.equal(root.get("enabled"), enabled));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }
    
    @Override
    @Transactional
    public User createUser(User user) {
        // 设置密码
        if (!StringUtils.hasText(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        
        // 设置初始值
        user.setCreatedTime(LocalDateTime.now());
        
        return userRepository.save(user);
    }
    
    @Override
    @Transactional
    public User updateUser(Long id, User userUpdate) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        // 更新基本信息
        if (StringUtils.hasText(userUpdate.getRealName())) {
            user.setRealName(userUpdate.getRealName());
        }
        
        if (StringUtils.hasText(userUpdate.getEmail())) {
            user.setEmail(userUpdate.getEmail());
        }
        
        if (StringUtils.hasText(userUpdate.getPhone())) {
            user.setPhone(userUpdate.getPhone());
        }
        
        if (StringUtils.hasText(userUpdate.getAvatarUrl())) {
            user.setAvatarUrl(userUpdate.getAvatarUrl());
        }
        
        // 更新角色和权限
        if (userUpdate.getRoles() != null) {
            user.setRoles(userUpdate.getRoles());
        }
        
        if (userUpdate.getPermissions() != null) {
            user.setPermissions(userUpdate.getPermissions());
        }
        
        // 更新状态
        if (userUpdate.isEnabled() != user.isEnabled()) {
            user.setEnabled(userUpdate.isEnabled());
        }
        
        // 更新密码
        if (StringUtils.hasText(userUpdate.getPassword())) {
            user.setPassword(passwordEncoder.encode(userUpdate.getPassword()));
        }
        
        return userRepository.save(user);
    }
    
    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
    
    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    @Override
    @Transactional
    public String resetPassword(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        // 生成随机密码
        String randomPassword = UUID.randomUUID().toString().substring(0, 8);
        user.setPassword(passwordEncoder.encode(randomPassword));
        userRepository.save(user);
        
        return randomPassword;
    }
    
    @Override
    public List<RoleInfo> getAllRoles() {
        return ROLES;
    }
} 