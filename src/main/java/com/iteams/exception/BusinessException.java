package com.iteams.exception;

/**
 * 业务异常类
 * 用于表示业务逻辑错误，如参数验证失败、业务规则冲突等
 */
public class BusinessException extends RuntimeException {
    
    /**
     * 默认构造函数
     */
    public BusinessException() {
        super();
    }
    
    /**
     * 带消息的构造函数
     *
     * @param message 异常消息
     */
    public BusinessException(String message) {
        super(message);
    }
    
    /**
     * 带消息和原因的构造函数
     *
     * @param message 异常消息
     * @param cause 原因
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * 带原因的构造函数
     *
     * @param cause 原因
     */
    public BusinessException(Throwable cause) {
        super(cause);
    }
} 