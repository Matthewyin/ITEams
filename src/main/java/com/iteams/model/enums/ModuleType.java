package com.iteams.model.enums;

import lombok.Getter;

/**
 * 模块类型枚举
 * <p>
 * 定义系统的各个功能模块，如资产管理、用户管理等
 * </p>
 */
@Getter
public enum ModuleType {
    
    ASSET("资产管理"),
    USER("用户管理"),
    DEPARTMENT("部门管理"),
    SYSTEM("系统设置"),
    AUTH("权限管理"),
    DASHBOARD("仪表盘");
    
    private final String description;
    
    ModuleType(String description) {
        this.description = description;
    }

} 