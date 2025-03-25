-- 检查系统管理部是否存在，如果不存在则创建
INSERT INTO sys_department (name, code, description, created_at)
SELECT '系统管理部', 'SYS_ADMIN', '系统管理部门', CURRENT_TIMESTAMP
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_department WHERE code = 'SYS_ADMIN');

-- 检查超级管理员角色是否存在，如果不存在则创建
INSERT INTO sys_role (name, code, description, created_at)
SELECT '超级管理员', 'SUPER_ADMIN', '系统超级管理员，拥有所有权限', CURRENT_TIMESTAMP
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE code = 'SUPER_ADMIN');

-- 检查普通用户角色是否存在，如果不存在则创建
INSERT INTO sys_role (name, code, description, created_at)
SELECT '普通用户', 'USER', '系统基本用户角色，拥有基本操作权限', CURRENT_TIMESTAMP
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE code = 'USER');

-- 检查管理员角色是否存在，如果不存在则创建
INSERT INTO sys_role (name, code, description, created_at)
SELECT '管理员', 'ADMIN', '系统管理员角色，拥有大部分管理权限', CURRENT_TIMESTAMP
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE code = 'ADMIN');

-- 检查超级管理员用户是否存在，如果不存在则创建
-- 密码为 Admin@123 的BCrypt加密值
INSERT INTO sys_user (username, password, real_name, email, department_id, account_non_expired, account_non_locked, credentials_non_expired, enabled, created_at)
SELECT 'supadmin', '$2a$10$0WPWBaXQvZ7SuUViXN5vSevqGLP8/i3rCsB6jyB8g78qH1emPukmK', '系统管理员', 'admin@iteams.com', 
       (SELECT id FROM sys_department WHERE code = 'SYS_ADMIN'),
       1, 1, 1, 1, CURRENT_TIMESTAMP
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_user WHERE username = 'supadmin');

-- 直接关联超级管理员用户和角色，不使用变量
INSERT INTO sys_user_role (user_id, role_id)
SELECT u.id, r.id
FROM sys_user u, sys_role r
WHERE u.username = 'supadmin' AND r.code = 'SUPER_ADMIN'
AND NOT EXISTS (
    SELECT 1 FROM sys_user_role ur 
    JOIN sys_user u2 ON ur.user_id = u2.id 
    JOIN sys_role r2 ON ur.role_id = r2.id
    WHERE u2.username = 'supadmin' AND r2.code = 'SUPER_ADMIN'
); 