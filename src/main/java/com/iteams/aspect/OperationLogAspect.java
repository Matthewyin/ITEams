package com.iteams.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iteams.annotation.OperationLog;
import com.iteams.model.enums.StatusType;
import com.iteams.model.vo.ApiResponse;
import com.iteams.repository.OperationLogRepository;
import com.iteams.util.DataMaskingUtil;
import com.iteams.util.IPUtil;
import com.iteams.util.UserInfoUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

/**
 * 操作日志AOP切面
 * <p>
 * 拦截带有@OperationLog注解的方法，记录操作日志
 * </p>
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {
    
    private final OperationLogRepository operationLogRepository;
    private final ObjectMapper objectMapper;
    
    /**
     * 定义切点
     */
    @Pointcut("@annotation(com.iteams.annotation.OperationLog)")
    public void operationLogPointCut() {
    }
    
    /**
     * 前置通知，用于记录操作前的数据
     */
    @Before("operationLogPointCut()")
    public void doBefore(JoinPoint joinPoint) {
        // 前置处理，如果需要可以在这里获取操作前的数据
    }
    
    /**
     * 后置通知，用于记录正常操作的日志
     */
    @AfterReturning(pointcut = "operationLogPointCut()", returning = "result")
    public void doAfterReturning(JoinPoint joinPoint, Object result) {
        try {
            saveLog(joinPoint, result, null);
        } catch (Exception e) {
            log.error("记录操作日志失败", e);
        }
    }
    
    /**
     * 异常通知，用于记录操作异常的日志
     */
    @AfterThrowing(pointcut = "operationLogPointCut()", throwing = "exception")
    public void doAfterThrowing(JoinPoint joinPoint, Exception exception) {
        try {
            saveLog(joinPoint, null, exception);
        } catch (Exception e) {
            log.error("记录操作异常日志失败", e);
        }
    }
    
    /**
     * 保存操作日志
     *
     * @param joinPoint 连接点
     * @param result 返回结果
     * @param exception 异常信息
     */
    @SuppressWarnings("unchecked")
    private void saveLog(JoinPoint joinPoint, Object result, Exception exception) throws Exception {
        // 获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = Objects.requireNonNull(attributes).getRequest();
        
        // 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        
        // 获取注解信息
        OperationLog operationLogAnnotation = method.getAnnotation(OperationLog.class);
        
        // 创建日志实体
        com.iteams.model.entity.OperationLog operationLog = new com.iteams.model.entity.OperationLog();
        
        // 设置操作人信息
        operationLog.setOperatorId(UserInfoUtil.getCurrentUserId());
        operationLog.setOperatorName(UserInfoUtil.getCurrentUserName());
        
        // 设置操作模块和类型
        operationLog.setModule(operationLogAnnotation.module());
        operationLog.setOperationType(operationLogAnnotation.operationType());
        
        // 设置操作描述
        operationLog.setDescription(operationLogAnnotation.description());
        
        // 设置操作对象类型
        operationLog.setObjectType(operationLogAnnotation.objectType());
        
        // 获取请求参数
        if (operationLogAnnotation.logParams()) {
            Object[] args = joinPoint.getArgs();
            String paramsJson = objectMapper.writeValueAsString(args);
            
            // 敏感数据脱敏
            if (operationLogAnnotation.dataMasking()) {
                paramsJson = DataMaskingUtil.maskSensitiveData(paramsJson);
            }
            
            operationLog.setBeforeData(paramsJson);
        }
        
        // 获取返回结果
        if (operationLogAnnotation.logResult() && result != null) {
            String resultJson = objectMapper.writeValueAsString(result);
            
            // 敏感数据脱敏
            if (operationLogAnnotation.dataMasking()) {
                resultJson = DataMaskingUtil.maskSensitiveData(resultJson);
            }
            
            operationLog.setAfterData(resultJson);
            
            // 从API响应中提取对象ID
            if (result instanceof ApiResponse) {
                Object data = ((ApiResponse<?>) result).getData();
                if (data != null) {
                    try {
                        // 尝试从返回数据中提取ID字段
                        Map<String, Object> map = objectMapper.convertValue(data, Map.class);
                        if (map.containsKey("id")) {
                            operationLog.setObjectId(map.get("id").toString());
                        }
                    } catch (Exception e) {
                        // 忽略错误，ID字段不是必须的
                        log.debug("无法从返回数据中提取ID", e);
                    }
                }
            }
        }
        
        // 设置IP和User-Agent
        operationLog.setIpAddress(IPUtil.getClientIp(request));
        operationLog.setUserAgent(request.getHeader("User-Agent"));
        
        // 设置操作时间
        operationLog.setOperationTime(LocalDateTime.now());
        
        // 设置操作结果
        if (exception != null) {
            operationLog.setStatus(StatusType.FAILED);
            operationLog.setErrorMessage(exception.getMessage());
        } else {
            operationLog.setStatus(StatusType.SUCCESS);
        }
        
        // 保存日志
        operationLogRepository.save(operationLog);
    }
} 