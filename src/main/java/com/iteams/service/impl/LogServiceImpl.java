package com.iteams.service.impl;

import com.iteams.model.entity.OperationLog;
import com.iteams.model.enums.ModuleType;
import com.iteams.model.enums.OperationType;
import com.iteams.model.enums.StatusType;
import com.iteams.repository.OperationLogRepository;
import com.iteams.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 日志服务实现类
 */
@Service
public class LogServiceImpl implements LogService {
    
    @Autowired
    private OperationLogRepository operationLogRepository;
    
    @Override
    public void logOperation(String username, ModuleType moduleType, OperationType operationType, String details) {
        OperationLog log = new OperationLog();
        log.setOperatorId(username);
        log.setOperatorName(username);
        log.setModule(moduleType);
        log.setOperationType(operationType);
        log.setDescription(details);
        log.setOperationTime(LocalDateTime.now());
        log.setStatus(StatusType.SUCCESS);
        
        // 记录客户端IP和浏览器信息
        // 在实际项目中，可以通过RequestContextHolder获取当前请求的HttpServletRequest
        // 并从中提取IP和User-Agent信息
        
        operationLogRepository.save(log);
    }
} 