-- 分类元数据表
CREATE TABLE IF NOT EXISTS category_metadata (
    category_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    level TINYINT NOT NULL COMMENT '分类层级（1-一级 2-二级 3-三级）',
    name VARCHAR(100) NOT NULL COMMENT '分类名称',
    code CHAR(4) COMMENT '分类编码（例：IT01）',
    parent_id BIGINT UNSIGNED COMMENT '父级分类ID',
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '创建时间',
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '更新时间',
    INDEX idx_parent (parent_id)
) ENGINE=InnoDB COMMENT='分类元数据表';

-- 资产主表
CREATE TABLE IF NOT EXISTS asset_master (
    asset_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    asset_uuid CHAR(36) NOT NULL COMMENT '业务唯一标识',
    asset_no VARCHAR(32) NOT NULL COMMENT '资产编号',
    asset_name VARCHAR(255) NOT NULL COMMENT '资产名称',
    current_status VARCHAR(20) NOT NULL COMMENT '当前状态（IN_USE/INVENTORY/MAINTENANCE/RETIRED）',
    category_hierarchy JSON NOT NULL COMMENT '分类层级JSON {"l1":1,"l2":3,"l3":5}',
    space_id BIGINT UNSIGNED COMMENT '当前空间位置ID',
    warranty_id BIGINT UNSIGNED COMMENT '当前有效维保ID',
    data_fingerprint CHAR(64) COMMENT '数据指纹（SHA256哈希值）',
    import_batch VARCHAR(32) COMMENT '导入批次号',
    excel_row_hash CHAR(64) COMMENT '原始Excel行哈希值',
    version INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '乐观锁版本控制',
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '创建时间',
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '更新时间',
    UNIQUE KEY idx_uniq_no (asset_no),
    UNIQUE KEY idx_uniq_uuid (asset_uuid),
    INDEX idx_batch (import_batch),
    INDEX idx_row_hash (excel_row_hash),
    INDEX idx_category ((CAST(JSON_EXTRACT(category_hierarchy, '$.l1') AS UNSIGNED)))
) ENGINE=InnoDB COMMENT='资产主表';

-- 空间轨迹表
CREATE TABLE IF NOT EXISTS space_timeline (
    space_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    asset_id BIGINT UNSIGNED NOT NULL COMMENT '资产ID',
    location_path VARCHAR(500) NOT NULL COMMENT '位置全路径（华东数据中心/A机房/3排机柜/U12）',
    latitude DECIMAL(10,7) COMMENT '纬度',
    longitude DECIMAL(10,7) COMMENT '经度',
    data_center VARCHAR(255) COMMENT '数据中心',
    room_name VARCHAR(255) COMMENT '机房名称',
    cabinet_no VARCHAR(50) COMMENT '机柜编号',
    u_position VARCHAR(10) COMMENT 'U位编号',
    environment VARCHAR(100) COMMENT '使用环境',
    keeper VARCHAR(100) COMMENT '保管人',
    valid_from DATETIME(6) NOT NULL COMMENT '生效时间',
    valid_to DATETIME(6) COMMENT '失效时间（NULL表示当前有效）',
    is_current BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否当前有效记录',
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '创建时间',
    INDEX idx_asset (asset_id),
    INDEX idx_location (location_path(100)),
    INDEX idx_keeper (keeper),
    INDEX idx_current (is_current)
) ENGINE=InnoDB COMMENT='空间轨迹表';

-- 维保合约表
CREATE TABLE IF NOT EXISTS warranty_contract (
    warranty_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    asset_id BIGINT UNSIGNED NOT NULL COMMENT '资产ID',
    contract_no VARCHAR(50) NOT NULL COMMENT '合同编号',
    start_date DATE NOT NULL COMMENT '维保开始日期',
    end_date DATE NOT NULL COMMENT '维保结束日期',
    provider_level TINYINT NOT NULL DEFAULT 1 COMMENT '服务级别（1-基础 2-高级 3-专属）',
    service_scope JSON COMMENT '服务范围描述JSON',
    is_active BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否当前有效',
    provider VARCHAR(100) COMMENT '维保提供商',
    warranty_status VARCHAR(20) COMMENT '维保状态',
    asset_life_years INT NOT NULL DEFAULT 5 COMMENT '资产使用年限(年)',
    acceptance_date DATE COMMENT '到货验收日期',
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '创建时间',
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '更新时间',
    UNIQUE KEY idx_uniq_contract (contract_no),
    INDEX idx_asset (asset_id),
    INDEX idx_active (is_active),
    INDEX idx_timerange (start_date, end_date)
) ENGINE=InnoDB COMMENT='维保合约表';

-- 变更溯源表
CREATE TABLE IF NOT EXISTS change_trace (
    trace_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    asset_id BIGINT UNSIGNED NOT NULL COMMENT '资产ID',
    change_type VARCHAR(20) NOT NULL COMMENT '变更类型（SPACE/STATUS/WARRANTY/PROPERTY/OWNER/INITIAL）',
    delta_snapshot JSON NOT NULL COMMENT '变更差异快照JSON',
    operation_tree JSON COMMENT '操作链路JSON（审批流程记录）',
    operated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '操作时间',
    operated_by VARCHAR(50) COMMENT '操作人',
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '创建时间',
    INDEX idx_asset (asset_id),
    INDEX idx_type (change_type),
    INDEX idx_operator (operated_by)
) ENGINE=InnoDB COMMENT='变更溯源表';

-- 导入日志表
CREATE TABLE IF NOT EXISTS import_log (
    log_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    batch_id VARCHAR(32) NOT NULL COMMENT '导入批次号',
    success_count INT NOT NULL DEFAULT 0 COMMENT '成功记录数',
    failed_count INT NOT NULL DEFAULT 0 COMMENT '失败记录数',
    error_details JSON COMMENT '错误详情JSON（含行号+原因）',
    cost_time DECIMAL(10,2) COMMENT '导入耗时（秒）',
    import_user VARCHAR(64) COMMENT '操作人',
    status VARCHAR(20) NOT NULL DEFAULT 'PROCESSING' COMMENT '状态（PROCESSING/COMPLETED/FAILED）',
    started_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '开始时间',
    finished_at DATETIME(6) COMMENT '结束时间',
    INDEX idx_batch (batch_id),
    INDEX idx_status (status),
    INDEX idx_user (import_user)
) ENGINE=InnoDB COMMENT='导入日志表'; 