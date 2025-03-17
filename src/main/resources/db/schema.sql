-- =====================================================
-- IT资产管理系统数据库设计
-- 设计原则：高内聚低耦合、历史可溯源、查询性能优化
-- =====================================================

-- 分类元数据表
-- 用途：存储资产分类树形结构，支持三级分类
-- 关系：自引用父子关系，通过parent_id构建分类树
CREATE TABLE IF NOT EXISTS category_metadata (
    category_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '分类ID，主键',
    level TINYINT NOT NULL COMMENT '分类层级（1-一级 2-二级 3-三级）',
    name VARCHAR(100) NOT NULL COMMENT '分类名称，如"服务器"、"网络设备"等',
    code CHAR(4) COMMENT '分类编码，用于资产编号前缀（例：IT01）',
    parent_id BIGINT UNSIGNED COMMENT '父级分类ID，一级分类为NULL',
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '创建时间，精确到微秒',
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '更新时间，自动更新',
    INDEX idx_parent (parent_id) COMMENT '父级分类索引，优化分类树查询'
) ENGINE=InnoDB COMMENT='分类元数据表 - 存储资产三级分类体系';

-- 资产主表
-- 用途：存储资产核心信息，是系统的中心实体
-- 关系：与分类表多对多，与空间表一对多，与维保表一对多
CREATE TABLE IF NOT EXISTS asset_master (
    asset_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '资产ID，内部主键',
    asset_uuid CHAR(36) NOT NULL COMMENT '资产UUID，业务唯一标识，格式如AST20240301-xxxxx',
    asset_no VARCHAR(32) NOT NULL COMMENT '资产编号，业务编号，格式如IT01HR2403xxxx',
    asset_name VARCHAR(255) NOT NULL COMMENT '资产名称，如"核心交换机"、"数据库服务器"等',
    current_status VARCHAR(20) NOT NULL COMMENT '当前状态（IN_USE:使用中/INVENTORY:库存中/MAINTENANCE:维修中/RETIRED:已报废）',
    category_hierarchy JSON NOT NULL COMMENT '分类层级JSON，包含三级分类ID {"l1":1,"l2":3,"l3":5}',
    space_id BIGINT UNSIGNED COMMENT '当前空间位置ID，关联space_timeline表',
    warranty_id BIGINT UNSIGNED COMMENT '当前有效维保ID，关联warranty_contract表',
    data_fingerprint CHAR(64) COMMENT '数据指纹，核心字段的SHA256哈希值，用于变更检测',
    import_batch VARCHAR(32) COMMENT '导入批次号，关联一次导入操作，格式如IMPORT-202403011205-xxxxx',
    excel_row_hash CHAR(64) COMMENT '原始Excel行哈希值，用于后续导入时去重判断',
    version INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '乐观锁版本控制，每次更新+1',
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '创建时间，精确到微秒',
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '更新时间，自动更新',
    UNIQUE KEY idx_uniq_no (asset_no) COMMENT '资产编号唯一索引，业务主键',
    UNIQUE KEY idx_uniq_uuid (asset_uuid) COMMENT '资产UUID唯一索引，业务标识',
    INDEX idx_batch (import_batch) COMMENT '批次索引，优化批次查询',
    INDEX idx_row_hash (excel_row_hash) COMMENT '行哈希索引，用于Excel导入去重检测',
    INDEX idx_category ((CAST(JSON_EXTRACT(category_hierarchy, '$.l1') AS UNSIGNED))) COMMENT '一级分类索引，优化分类查询'
) ENGINE=InnoDB COMMENT='资产主表 - 存储资产核心信息';

-- 空间轨迹表
-- 用途：存储资产位置变更历史，支持时间维度追溯
-- 关系：与资产表多对一，历史记录模式
CREATE TABLE IF NOT EXISTS space_timeline (
    space_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '空间ID，主键',
    asset_id BIGINT UNSIGNED NOT NULL COMMENT '关联的资产ID，外键',
    location_path VARCHAR(500) NOT NULL COMMENT '位置全路径，如"华东数据中心/A机房/3排机柜/U12"',
    latitude DECIMAL(10,7) COMMENT '纬度坐标，支持地图展示',
    longitude DECIMAL(10,7) COMMENT '经度坐标，支持地图展示',
    data_center VARCHAR(255) COMMENT '数据中心名称，如"华东数据中心"',
    room_name VARCHAR(255) COMMENT '机房名称，如"A机房"',
    cabinet_no VARCHAR(50) COMMENT '机柜编号，如"3排机柜"',
    u_position VARCHAR(10) COMMENT 'U位编号，如"U12"',
    environment VARCHAR(100) COMMENT '使用环境，如"生产环境"、"测试环境"',
    keeper VARCHAR(100) COMMENT '保管人，负责该位置资产的人员',
    valid_from DATETIME(6) NOT NULL COMMENT '位置记录生效时间，支持时间维度追溯',
    valid_to DATETIME(6) COMMENT '位置记录失效时间，NULL表示当前有效',
    is_current BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否当前有效记录，快速查询标识',
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '创建时间，记录插入时间',
    INDEX idx_asset (asset_id) COMMENT '资产ID索引，优化资产位置查询',
    INDEX idx_location (location_path(100)) COMMENT '位置路径索引，支持位置检索',
    INDEX idx_keeper (keeper) COMMENT '保管人索引，支持按保管人检索',
    INDEX idx_current (is_current) COMMENT '当前记录索引，优化当前位置查询'
) ENGINE=InnoDB COMMENT='空间轨迹表 - 记录资产位置变更历史';

-- 维保合约表
-- 用途：存储资产维保信息，支持多份合同记录
-- 关系：与资产表多对一
CREATE TABLE IF NOT EXISTS warranty_contract (
    warranty_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '维保ID，主键',
    asset_id BIGINT UNSIGNED NOT NULL COMMENT '关联的资产ID，外键',
    contract_no VARCHAR(50) NOT NULL COMMENT '合同编号，如"WTY-2024-0001"',
    start_date DATE NOT NULL COMMENT '维保开始日期',
    end_date DATE NOT NULL COMMENT '维保结束日期',
    provider_level TINYINT NOT NULL DEFAULT 1 COMMENT '服务级别（1-基础 2-高级 3-专属）',
    service_scope JSON COMMENT '服务范围描述JSON，如{"hardware":true,"software":false}',
    is_active BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否当前有效，一个资产只有一份有效维保',
    provider VARCHAR(100) COMMENT '维保提供商名称，如"联想"、"戴尔"等',
    warranty_status VARCHAR(20) COMMENT '维保状态，如"有效"、"即将过期"、"已过期"',
    asset_life_years INT NOT NULL DEFAULT 5 COMMENT '资产使用年限(年)，常用于折旧计算',
    acceptance_date DATE COMMENT '到货验收日期，资产交付日期',
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '创建时间，记录插入时间',
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '更新时间，自动更新',
    UNIQUE KEY idx_uniq_contract (contract_no) COMMENT '合同编号唯一索引，保证合同唯一性',
    INDEX idx_asset (asset_id) COMMENT '资产ID索引，优化资产维保查询',
    INDEX idx_active (is_active) COMMENT '活动状态索引，优化有效维保查询',
    INDEX idx_timerange (start_date, end_date) COMMENT '时间范围索引，优化时间范围查询'
) ENGINE=InnoDB COMMENT='维保合约表 - 记录资产维保合同信息';

-- 变更溯源表
-- 用途：记录资产的所有变更操作，支持审计和回溯
-- 关系：与资产表多对一，事件记录模式
CREATE TABLE IF NOT EXISTS change_trace (
    trace_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '变更ID，主键',
    asset_id BIGINT UNSIGNED NOT NULL COMMENT '关联的资产ID，外键',
    change_type VARCHAR(20) NOT NULL COMMENT '变更类型（SPACE:位置变更/STATUS:状态变更/WARRANTY:维保变更/PROPERTY:属性变更/OWNER:归属变更/INITIAL:初始化）',
    delta_snapshot JSON NOT NULL COMMENT '变更差异快照JSON，包含变更前后的值对比',
    operation_tree JSON COMMENT '操作链路JSON，记录审批流程和操作轨迹',
    operated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '操作时间，变更发生时间',
    operated_by VARCHAR(50) COMMENT '操作人，执行变更的用户',
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '记录创建时间，可能晚于操作时间',
    INDEX idx_asset (asset_id) COMMENT '资产ID索引，优化资产变更历史查询',
    INDEX idx_type (change_type) COMMENT '变更类型索引，支持按变更类型检索',
    INDEX idx_operator (operated_by) COMMENT '操作人索引，支持按操作人检索'
) ENGINE=InnoDB COMMENT='变更溯源表 - 记录资产全生命周期变更历史';

-- 导入日志表
-- 用途：记录Excel导入操作的执行情况和结果
-- 关系：与资产表一对多（通过批次号）
CREATE TABLE IF NOT EXISTS import_log (
    log_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '日志ID，主键',
    batch_id VARCHAR(32) NOT NULL COMMENT '导入批次号，关联导入的所有资产',
    success_count INT NOT NULL DEFAULT 0 COMMENT '成功导入的记录数',
    failed_count INT NOT NULL DEFAULT 0 COMMENT '导入失败的记录数',
    error_details JSON COMMENT '错误详情JSON，包含每个失败行的行号和原因',
    cost_time DECIMAL(10,2) COMMENT '导入耗时（秒），性能监控指标',
    import_user VARCHAR(64) COMMENT '执行导入的用户账号',
    status VARCHAR(20) NOT NULL DEFAULT 'PROCESSING' COMMENT '导入状态（PROCESSING:处理中/COMPLETED:已完成/FAILED:失败）',
    started_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '导入开始时间',
    finished_at DATETIME(6) COMMENT '导入结束时间，NULL表示尚未完成',
    INDEX idx_batch (batch_id) COMMENT '批次索引，关联查询导入资产',
    INDEX idx_status (status) COMMENT '状态索引，筛选不同状态的导入任务',
    INDEX idx_user (import_user) COMMENT '用户索引，查询用户的导入历史'
) ENGINE=InnoDB COMMENT='导入日志表 - 记录Excel文件导入历史'; 