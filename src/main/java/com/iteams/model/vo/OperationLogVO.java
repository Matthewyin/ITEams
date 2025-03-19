package com.iteams.model.vo;

import com.iteams.model.enums.ModuleType;
import com.iteams.model.enums.OperationType;
import com.iteams.model.enums.StatusType;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志展示对象
 * <p>
 * 用于返回给前端展示的日志数据
 * </p>
 */
@Data
public class OperationLogVO {
    
    /**
     * 日志ID
     */
    private String id;
    
    /**
     * 操作类型
     */
    private OperationType operationType;
    
    /**
     * 操作类型描述
     */
    private String operationTypeDesc;
    
    /**
     * 操作模块
     */
    private ModuleType module;
    
    /**
     * 操作模块描述
     */
    private String moduleDesc;
    
    /**
     * 操作对象ID
     */
    private String objectId;
    
    /**
     * 操作对象类型
     */
    private String objectType;
    
    /**
     * 操作描述
     */
    private String description;
    
    /**
     * 操作人ID
     */
    private String operatorId;
    
    /**
     * 操作人姓名
     */
    private String operatorName;
    
    /**
     * 操作时间
     */
    private LocalDateTime operationTime;
    
    /**
     * 操作者IP地址
     */
    private String ipAddress;
    
    /**
     * 操作结果状态
     */
    private StatusType status;
    
    /**
     * 状态描述
     */
    private String statusDesc;
} 