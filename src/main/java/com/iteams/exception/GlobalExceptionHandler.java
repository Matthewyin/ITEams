package com.iteams.exception;

import com.iteams.model.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * 全局异常处理
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理认证异常
     */
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<Void> handleAuthenticationException(AuthenticationException e) {
        logger.error("用户认证失败", e);
        
        if (e instanceof BadCredentialsException) {
            return ApiResponse.error("用户名或密码错误");
        } else if (e instanceof DisabledException) {
            return ApiResponse.error("账号已被禁用");
        } else if (e instanceof LockedException) {
            return ApiResponse.error("账号已被锁定");
        } else if (e instanceof UsernameNotFoundException) {
            return ApiResponse.error("用户名或密码错误");
        }
        
        return ApiResponse.error("认证失败");
    }

    /**
     * 处理请求资源不存在异常
     */
    @ExceptionHandler({NoResourceFoundException.class, NoHandlerFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<Void> handleNoResourceFoundException(Exception e) {
        if (e.getMessage() != null && (
            e.getMessage().contains(".js") || 
            e.getMessage().contains(".css") || 
            e.getMessage().contains(".html") || 
            e.getMessage().contains(".ico") || 
            e.getMessage().contains(".png") || 
            e.getMessage().contains(".jpg"))) {
            logger.warn("静态资源未找到: {}", e.getMessage());
            return ApiResponse.error("资源不存在");
        }
        
        logger.warn("请求的API未找到: {}", e.getMessage());
        return ApiResponse.error("请求的API不存在");
    }

    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleRuntimeException(RuntimeException e) {
        String message = e.getMessage();
        
        // 处理登录认证相关异常
        if (message != null) {
            // 用户认证失败相关异常
            if (message.contains("用户不存在") || 
                message.contains("登录失败") || 
                message.contains("密码错误") || 
                message.contains("用户名或密码错误")) {
                
                logger.error("登录认证失败: {}", message, e);
                return ApiResponse.error("用户名或密码错误");
            }
            
            // 处理前端路由请求或静态资源请求
            if (message.contains(".js") || 
                message.contains(".css") || 
                message.contains(".html") || 
                message.contains(".ico") || 
                message.contains(".png") || 
                message.contains(".jpg") ||
                message.contains("/api/")) {
                
                logger.debug("忽略前端路由或资源请求: {}", message);
                return ApiResponse.error("资源不存在");
            }
        }
        
        logger.error("运行时异常", e);
        return ApiResponse.error("服务器内部错误");
    }

    /**
     * 处理所有未捕获的异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleException(Exception e) {
        logger.error("未知异常", e);
        return ApiResponse.error("服务器内部错误");
    }
} 