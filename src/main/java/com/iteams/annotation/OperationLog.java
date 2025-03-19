package com.iteams.annotation;

import com.iteams.model.enums.ModuleType;
import com.iteams.model.enums.OperationType;

import java.lang.annotation.*;

/**
 * 操作日志注解
 * <p>
 * 用于标记需要记录操作日志的方法，通过AOP切面实现日志记录
 * </p>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {
    
    /**
     * 操作模块
     */
    ModuleType module();
    
    /**
     * 操作类型
     */
    OperationType operationType();
    
    /**
     * 操作描述
     */
    String description() default "";
    
    /**
     * 操作对象类型
     */
    String objectType() default "";
    
    /**
     * 是否记录请求参数
     */
    boolean logParams() default true;
    
    /**
     * 是否记录返回结果
     */
    boolean logResult() default true;
    
    /**
     * 是否对敏感数据脱敏
     */
    boolean dataMasking() default true;
} 