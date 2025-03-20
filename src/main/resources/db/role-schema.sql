-- 角色表
CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_code VARCHAR(50) NOT NULL COMMENT '角色编码',
    role_name VARCHAR(100) NOT NULL COMMENT '角色名称',
    description VARCHAR(255) COMMENT '角色描述',
    status TINYINT DEFAULT 1 COMMENT '状态(1-启用, 0-禁用)',
    sort_order INT DEFAULT 0 COMMENT '排序',
    created_by VARCHAR(50) COMMENT '创建人',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by VARCHAR(50) COMMENT '更新人',
    updated_time DATETIME COMMENT '更新时间',
    UNIQUE KEY uk_role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 权限表
CREATE TABLE IF NOT EXISTS sys_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    parent_id BIGINT DEFAULT 0 COMMENT '父级ID',
    permission_code VARCHAR(100) NOT NULL COMMENT '权限编码',
    permission_name VARCHAR(100) NOT NULL COMMENT '权限名称',
    permission_type VARCHAR(20) NOT NULL COMMENT '权限类型(menu-菜单, button-按钮, api-接口)',
    path VARCHAR(255) COMMENT '路径',
    component VARCHAR(255) COMMENT '组件',
    icon VARCHAR(100) COMMENT '图标',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态(1-启用, 0-禁用)',
    visible TINYINT DEFAULT 1 COMMENT '是否可见(1-可见, 0-隐藏)',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME COMMENT '更新时间',
    UNIQUE KEY uk_permission_code (permission_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS sys_role_permission (
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    PRIMARY KEY (role_id, permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- 初始化基础角色
INSERT INTO sys_role (role_code, role_name, description, status, sort_order)
VALUES 
('superadmin', '超级管理员', '拥有所有权限，可以管理整个系统', 1, 0),
('admin', '管理员', '拥有大部分权限，负责系统的日常管理', 1, 1),
('user', '普通用户', '普通用户，只有基本的查看权限', 1, 2)
ON DUPLICATE KEY UPDATE role_name = VALUES(role_name), description = VALUES(description);

-- 初始化权限数据
INSERT INTO sys_permission (parent_id, permission_code, permission_name, permission_type, path, component, icon, sort_order, status, visible)
VALUES
-- 系统管理
(0, 'system', '系统管理', 'menu', '/system', 'Layout', 'setting', 10, 1, 1),

-- 用户管理
(1, 'system:user', '用户管理', 'menu', '/system/user', 'system/user/index', 'user', 11, 1, 1),
(2, 'system:user:list', '用户列表', 'button', NULL, NULL, NULL, 1, 1, 1),
(2, 'system:user:add', '用户新增', 'button', NULL, NULL, NULL, 2, 1, 1),
(2, 'system:user:edit', '用户编辑', 'button', NULL, NULL, NULL, 3, 1, 1),
(2, 'system:user:delete', '用户删除', 'button', NULL, NULL, NULL, 4, 1, 1),
(2, 'system:user:reset', '重置密码', 'button', NULL, NULL, NULL, 5, 1, 1),

-- 角色管理
(1, 'system:role', '角色管理', 'menu', '/system/role', 'system/role/index', 'role', 12, 1, 1),
(8, 'system:role:list', '角色列表', 'button', NULL, NULL, NULL, 1, 1, 1),
(8, 'system:role:add', '角色新增', 'button', NULL, NULL, NULL, 2, 1, 1),
(8, 'system:role:edit', '角色编辑', 'button', NULL, NULL, NULL, 3, 1, 1),
(8, 'system:role:delete', '角色删除', 'button', NULL, NULL, NULL, 4, 1, 1),
(8, 'system:role:permission', '分配权限', 'button', NULL, NULL, NULL, 5, 1, 1),

-- 资产管理
(0, 'asset', '资产管理', 'menu', '/asset', 'Layout', 'asset', 20, 1, 1),
(14, 'asset:list', '资产列表', 'button', NULL, NULL, NULL, 1, 1, 1),
(14, 'asset:add', '资产新增', 'button', NULL, NULL, NULL, 2, 1, 1),
(14, 'asset:edit', '资产编辑', 'button', NULL, NULL, NULL, 3, 1, 1),
(14, 'asset:delete', '资产删除', 'button', NULL, NULL, NULL, 4, 1, 1),
(14, 'asset:import', '资产导入', 'button', NULL, NULL, NULL, 5, 1, 1),
(14, 'asset:export', '资产导出', 'button', NULL, NULL, NULL, 6, 1, 1),

-- 数据中心
(0, 'datacenter', '数据中心', 'menu', '/datacenter', 'Layout', 'datacenter', 30, 1, 1),
(21, 'datacenter:view', '数据中心查看', 'button', NULL, NULL, NULL, 1, 1, 1),

-- 日志管理
(0, 'log', '日志管理', 'menu', '/log', 'log/index', 'log', 40, 1, 1),
(23, 'log:view', '日志查看', 'button', NULL, NULL, NULL, 1, 1, 1),
(23, 'log:export', '日志导出', 'button', NULL, NULL, NULL, 2, 1, 1)

ON DUPLICATE KEY UPDATE 
permission_name = VALUES(permission_name),
permission_type = VALUES(permission_type),
path = VALUES(path),
component = VALUES(component),
icon = VALUES(icon),
sort_order = VALUES(sort_order);

-- 为超级管理员分配所有权限
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 
    (SELECT id FROM sys_role WHERE role_code = 'superadmin'),
    id
FROM 
    sys_permission
ON DUPLICATE KEY UPDATE role_id = VALUES(role_id);

-- 为管理员分配除角色管理外的大部分权限
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 
    (SELECT id FROM sys_role WHERE role_code = 'admin'),
    id
FROM 
    sys_permission
WHERE 
    permission_code NOT LIKE 'system:role%'
ON DUPLICATE KEY UPDATE role_id = VALUES(role_id);

-- 为普通用户分配只读权限
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 
    (SELECT id FROM sys_role WHERE role_code = 'user'),
    id
FROM 
    sys_permission
WHERE 
    permission_code IN ('system:user:list', 'asset:list', 'datacenter:view', 'log:view')
ON DUPLICATE KEY UPDATE role_id = VALUES(role_id); 