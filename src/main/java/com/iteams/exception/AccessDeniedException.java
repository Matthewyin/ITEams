package com.iteams.exception;

/**
 * 访问拒绝异常
 * 当用户尝试访问没有权限的资源时抛出
 */
public class AccessDeniedException extends RuntimeException {

    /**
     * 构造函数
     */
    public AccessDeniedException() {
        super("访问被拒绝");
    }

    /**
     * 带消息的构造函数
     *
     * @param message 错误消息
     */
    public AccessDeniedException(String message) {
        super(message);
    }

    /**
     * 带原因的构造函数
     *
     * @param message 错误消息
     * @param cause 原因
     */
    public AccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }
}
