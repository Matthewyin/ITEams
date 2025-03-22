package com.iteams.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * SQL初始化器
 * <p>
 * 通过JDBC直接执行SQL语句，确保超级管理员用户和角色被创建
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SQLInitializer implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;
    private final InitialDataProperties initialDataProperties;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (!initialDataProperties.isEnabled()) {
            log.info("初始化数据功能已禁用，跳过执行");
            return;
        }
        
        log.info("开始执行SQL初始化...");
        
        try {
            initRoles();
            initSuperAdmin();
            log.info("SQL初始化完成");
        } catch (Exception e) {
            log.error("SQL初始化失败", e);
        }
    }

    /**
     * 初始化角色
     */
    private void initRoles() {
        for (InitialDataProperties.Role roleConfig : initialDataProperties.getRoles()) {
            String roleCode = roleConfig.getCode();
            if (!StringUtils.hasText(roleCode)) {
                log.warn("角色编码为空，跳过创建");
                continue;
            }
            
            // 检查角色是否存在
            int roleCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM sys_role WHERE code = ?", 
                Integer.class, 
                roleCode
            );
            
            if (roleCount == 0) {
                jdbcTemplate.update(
                    "INSERT INTO sys_role (name, code, description, created_at) VALUES (?, ?, ?, ?)",
                    roleConfig.getName(), 
                    roleCode, 
                    roleConfig.getDescription(), 
                    Timestamp.valueOf(LocalDateTime.now())
                );
                log.info("创建角色[{}]成功", roleConfig.getName());
            } else {
                log.info("角色[{}]已存在", roleConfig.getName());
            }
        }
    }

    /**
     * 初始化超级管理员用户
     */
    private void initSuperAdmin() {
        InitialDataProperties.Admin adminConfig = initialDataProperties.getAdminUser();
        String username = adminConfig.getUsername();
        
        if (!StringUtils.hasText(username)) {
            log.warn("管理员用户名为空，跳过创建");
            return;
        }
        
        // 检查超级管理员用户是否存在
        int superAdminCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM sys_user WHERE username = ?", 
            Integer.class, 
            username
        );
        
        if (superAdminCount == 0) {
            // 获取或生成密码哈希
            String passwordHash;
            if (adminConfig.isPasswordEncrypted()) {
                // 如果配置的密码已经是哈希值，则直接使用
                passwordHash = adminConfig.getPassword();
            } else {
                // 否则使用PasswordEncoder加密密码
                passwordHash = passwordEncoder.encode(adminConfig.getPassword());
            }
            
            // 创建超级管理员用户
            jdbcTemplate.update(
                "INSERT INTO sys_user (username, password, real_name, email, department, account_non_expired, account_non_locked, credentials_non_expired, enabled, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                username, 
                passwordHash, 
                adminConfig.getRealName(), 
                adminConfig.getEmail(),
                adminConfig.getDepartment(),
                true,
                true,
                true,
                true,
                Timestamp.valueOf(LocalDateTime.now())
            );
            log.info("创建用户[{}]成功", username);
            
            // 关联用户与角色
            String roleCode = adminConfig.getRoleCode();
            if (StringUtils.hasText(roleCode)) {
                // 获取用户和角色ID
                Long userId = jdbcTemplate.queryForObject(
                    "SELECT id FROM sys_user WHERE username = ?", 
                    Long.class, 
                    username
                );
                
                Long roleId = jdbcTemplate.queryForObject(
                    "SELECT id FROM sys_role WHERE code = ?", 
                    Long.class, 
                    roleCode
                );
                
                if (userId != null && roleId != null) {
                    // 关联用户和角色
                    jdbcTemplate.update(
                        "INSERT INTO sys_user_role (user_id, role_id) VALUES (?, ?)",
                        userId, roleId
                    );
                    log.info("关联用户[{}]和角色[{}]成功", username, roleCode);
                } else {
                    log.warn("无法找到用户ID或角色ID，关联失败");
                }
            } else {
                log.warn("未指定用户[{}]的关联角色，跳过关联", username);
            }
        } else {
            log.info("用户[{}]已存在", username);
        }
    }
} 