package com.iteams.model.enums;

import lombok.Getter;

/**
 * 操作类型枚举
 * <p>
 * 定义系统支持的各种操作类型，如新增、修改、删除等
 * </p>
 */
@Getter
public enum OperationType {
    
    CREATE("新增"),
    UPDATE("修改"),
    DELETE("删除"),
    QUERY("查询"),
    IMPORT("导入"),
    EXPORT("导出"),
    LOGIN("登录"),
    LOGOUT("登出"),
    UPLOAD("上传"),
    DOWNLOAD("下载");
    
    private final String description;
    
    OperationType(String description) {
        this.description = description;
    }

} 