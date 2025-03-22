-- 创建用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    real_name VARCHAR(50),
    avatar_url VARCHAR(255),
    last_login_time DATETIME,
    created_time DATETIME NOT NULL,
    updated_time DATETIME,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    account_non_expired BOOLEAN NOT NULL DEFAULT TRUE,
    account_non_locked BOOLEAN NOT NULL DEFAULT TRUE,
    credentials_non_expired BOOLEAN NOT NULL DEFAULT TRUE
);

-- 创建用户角色表
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, role),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 创建用户权限表
CREATE TABLE IF NOT EXISTS user_permissions (
    user_id BIGINT NOT NULL,
    permission VARCHAR(100) NOT NULL,
    PRIMARY KEY (user_id, permission),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 插入管理员用户（密码: admin123，使用BCrypt加密）
INSERT INTO users (username, password, real_name, enabled, account_non_expired, account_non_locked, credentials_non_expired, created_time)
VALUES ('admin', '$2a$10$0WPWBaXQvZ7SuUViXN5vSevqGLP8/i3rCsB6jyB8g78qH1emPukmK', '系统管理员', TRUE, TRUE, TRUE, TRUE, NOW())
ON DUPLICATE KEY UPDATE updated_time = NOW(), password = '$2a$10$0WPWBaXQvZ7SuUViXN5vSevqGLP8/i3rCsB6jyB8g78qH1emPukmK';

-- 插入普通用户（密码: user123，使用BCrypt加密）
INSERT INTO users (username, password, real_name, enabled, account_non_expired, account_non_locked, credentials_non_expired, created_time)
VALUES ('user', '$2a$10$9ZYKmPzWAHHk0GlQBkL2xOUMZL9WhZ7pUGSE/2NhsIEQQCFTRQYQi', '普通用户', TRUE, TRUE, TRUE, TRUE, NOW())
ON DUPLICATE KEY UPDATE updated_time = NOW();

-- 清空角色表中的数据
DELETE FROM user_roles;

-- 插入管理员角色
INSERT INTO user_roles (user_id, role)
SELECT id, 'admin' FROM users WHERE username = 'admin'
ON DUPLICATE KEY UPDATE role = 'admin';

-- 插入普通用户角色
INSERT INTO user_roles (user_id, role)
SELECT id, 'user' FROM users WHERE username = 'user'
ON DUPLICATE KEY UPDATE role = 'user';

-- 清空权限表中的数据
DELETE FROM user_permissions;

-- 插入管理员权限
INSERT INTO user_permissions (user_id, permission)
SELECT id, '*' FROM users WHERE username = 'admin'
ON DUPLICATE KEY UPDATE permission = '*';

-- 插入普通用户权限
INSERT INTO user_permissions (user_id, permission)
SELECT id, 'asset:view' FROM users WHERE username = 'user'
ON DUPLICATE KEY UPDATE permission = 'asset:view';

INSERT INTO user_permissions (user_id, permission)
SELECT id, 'dashboard:view' FROM users WHERE username = 'user'
ON DUPLICATE KEY UPDATE permission = 'dashboard:view';
