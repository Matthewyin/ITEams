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
        
        String roleCode = adminConfig.getRoleCode();
        if (!StringUtils.hasText(roleCode)) {
            log.warn("管理员角色编码为空，默认设置为SUPER_ADMIN");
            roleCode = "SUPER_ADMIN";
        }
        
        // 确保SUPER_ADMIN角色存在
        int roleCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM sys_role WHERE code = ?", 
            Integer.class, 
            roleCode
        );
        
        if (roleCount == 0) {
            log.warn("超级管理员角色[{}]不存在，尝试创建", roleCode);
            jdbcTemplate.update(
                "INSERT INTO sys_role (name, code, description, created_at) VALUES (?, ?, ?, ?)",
                "超级管理员", 
                roleCode, 
                "系统超级管理员，拥有所有权限", 
                Timestamp.valueOf(LocalDateTime.now())
            );
            log.info("创建角色[{}]成功", roleCode);
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
            
            // 关联用户与超级管理员角色
            createUserRoleAssociation(username, roleCode);
        } else {
            log.info("用户[{}]已存在，确保其拥有超级管理员权限", username);
            
            // 检查是否已经关联了超级管理员角色
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
            
            // 检查用户和角色是否已关联
            int userRoleCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM sys_user_role WHERE user_id = ? AND role_id = ?", 
                Integer.class, 
                userId, roleId
            );
            
            if (userRoleCount == 0) {
                log.info("用户[{}]未关联超级管理员角色，尝试关联", username);
                // 关联用户和超级管理员角色
                createUserRoleAssociation(username, roleCode);
            } else {
                log.info("用户[{}]已关联超级管理员角色", username);
            }
        }
    }
    
    /**
     * 创建用户与角色的关联
     * 
     * @param username 用户名
     * @param roleCode 角色编码
     */
    private void createUserRoleAssociation(String username, String roleCode) {
        try {
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
                // 先检查是否已存在关联
                int existingCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM sys_user_role WHERE user_id = ? AND role_id = ?", 
                    Integer.class, 
                    userId, roleId
                );
                
                if (existingCount == 0) {
                    // 关联用户和角色
                    jdbcTemplate.update(
                        "INSERT INTO sys_user_role (user_id, role_id) VALUES (?, ?)",
                        userId, roleId
                    );
                    log.info("关联用户[{}]和角色[{}]成功", username, roleCode);
                } else {
                    log.info("用户[{}]和角色[{}]已经关联", username, roleCode);
                }
                
                // 确保admin和administrator角色标识也被识别
                addAdditionalAdminRoles(userId);
            } else {
                log.warn("无法找到用户ID或角色ID，关联失败");
            }
        } catch (Exception e) {
            log.error("创建用户角色关联失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 为用户添加额外的管理员角色标识，确保前端能够识别
     * 
     * @param userId 用户ID
     */
    private void addAdditionalAdminRoles(Long userId) {
        // 添加ADMIN角色（如果不存在）
        try {
            // 检查ADMIN角色是否存在
            int adminRoleCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM sys_role WHERE code = ?", 
                Integer.class, 
                "ADMIN"
            );
            
            if (adminRoleCount == 0) {
                jdbcTemplate.update(
                    "INSERT INTO sys_role (name, code, description, created_at) VALUES (?, ?, ?, ?)",
                    "管理员", 
                    "ADMIN", 
                    "系统管理员角色", 
                    Timestamp.valueOf(LocalDateTime.now())
                );
                log.info("创建角色[ADMIN]成功");
            }
            
            // 获取ADMIN角色ID
            Long adminRoleId = jdbcTemplate.queryForObject(
                "SELECT id FROM sys_role WHERE code = ?", 
                Long.class, 
                "ADMIN"
            );
            
            // 关联用户和ADMIN角色
            int userRoleCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM sys_user_role WHERE user_id = ? AND role_id = ?", 
                Integer.class, 
                userId, adminRoleId
            );
            
            if (userRoleCount == 0) {
                jdbcTemplate.update(
                    "INSERT INTO sys_user_role (user_id, role_id) VALUES (?, ?)",
                    userId, adminRoleId
                );
                log.info("关联用户ID[{}]和角色[ADMIN]成功", userId);
            }
            
            // 检查administrator角色是否存在
            int administratorRoleCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM sys_role WHERE code = ?", 
                Integer.class, 
                "administrator"
            );
            
            if (administratorRoleCount == 0) {
                jdbcTemplate.update(
                    "INSERT INTO sys_role (name, code, description, created_at) VALUES (?, ?, ?, ?)",
                    "管理员", 
                    "administrator", 
                    "系统管理员角色", 
                    Timestamp.valueOf(LocalDateTime.now())
                );
                log.info("创建角色[administrator]成功");
            }
            
            // 获取administrator角色ID
            Long administratorRoleId = jdbcTemplate.queryForObject(
                "SELECT id FROM sys_role WHERE code = ?", 
                Long.class, 
                "administrator"
            );
            
            // 关联用户和administrator角色
            int adminUserRoleCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM sys_user_role WHERE user_id = ? AND role_id = ?", 
                Integer.class, 
                userId, administratorRoleId
            );
            
            if (adminUserRoleCount == 0) {
                jdbcTemplate.update(
                    "INSERT INTO sys_user_role (user_id, role_id) VALUES (?, ?)",
                    userId, administratorRoleId
                );
                log.info("关联用户ID[{}]和角色[administrator]成功", userId);
            }
        } catch (Exception e) {
            log.error("为用户添加额外管理员角色失败: {}", e.getMessage(), e);
        }
    }
} 