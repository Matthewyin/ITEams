-- 检查是否存在部门表，如果不存在则创建
CREATE TABLE IF NOT EXISTS sys_department (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    code VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(200),
    parent_id BIGINT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (parent_id) REFERENCES sys_department(id)
);

-- 检查是否存在用户表，如果不存在则创建
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    real_name VARCHAR(50) NOT NULL,
    email VARCHAR(100),
    department_id BIGINT,
    account_non_expired BOOLEAN NOT NULL DEFAULT 1,
    account_non_locked BOOLEAN NOT NULL DEFAULT 1,
    credentials_non_expired BOOLEAN NOT NULL DEFAULT 1,
    enabled BOOLEAN NOT NULL DEFAULT 1,
    last_login_time TIMESTAMP,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (department_id) REFERENCES sys_department(id)
);

-- 检查是否存在角色表，如果不存在则创建
CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    code VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(200),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- 检查是否存在用户角色关联表，如果不存在则创建
CREATE TABLE IF NOT EXISTS sys_user_role (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES sys_user(id),
    FOREIGN KEY (role_id) REFERENCES sys_role(id)
); 