package com.iteams.model.enums;

/**
 * 操作状态枚举
 * <p>
 * 定义操作的结果状态，如成功或失败
 * </p>
 */
public enum StatusType {
    
    SUCCESS("成功"),
    FAILED("失败");
    
    private final String description;
    
    StatusType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
} 