package com.iteams.annotation;

import java.lang.annotation.*;

/**
 * 自定义权限注解，用于方法级别的权限控制
 * 可以指定需要的权限编码，支持多个权限的逻辑组合
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {
    
    /**
     * 所需权限编码数组
     * 默认为空数组，表示不需要特定权限
     */
    String[] value() default {};
    
    /**
     * 权限逻辑类型
     * ANY: 满足任意一个权限即可访问（OR关系）
     * ALL: 必须满足所有指定的权限才能访问（AND关系）
     */
    LogicalType logical() default LogicalType.ANY;
    
    /**
     * 权限逻辑类型枚举
     */
    enum LogicalType {
        /**
         * 满足任意一个权限即可（OR关系）
         */
        ANY,
        
        /**
         * 必须满足所有权限（AND关系）
         */
        ALL
    }
}
