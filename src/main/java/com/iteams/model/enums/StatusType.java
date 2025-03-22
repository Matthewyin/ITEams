package com.iteams.model.enums;

import lombok.Getter;

/**
 * 操作状态枚举
 * <p>
 * 定义操作的结果状态，如成功或失败
 * </p>
 */
@Getter
public enum StatusType {
    
    SUCCESS("成功"),
    FAILED("失败");
    
    private final String description;
    
    StatusType(String description) {
        this.description = description;
    }

} 