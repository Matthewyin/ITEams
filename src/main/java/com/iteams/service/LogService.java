package com.iteams.service;

import com.iteams.model.enums.ModuleType;
import com.iteams.model.enums.OperationType;

/**
 * 日志服务接口
 */
public interface LogService {
    
    /**
     * 记录操作日志
     *
     * @param username 用户名
     * @param moduleType 模块类型
     * @param operationType 操作类型
     * @param details 操作详情
     */
    void logOperation(String username, ModuleType moduleType, OperationType operationType, String details);
} 