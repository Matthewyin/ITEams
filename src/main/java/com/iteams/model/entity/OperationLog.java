package com.iteams.model.entity;

import com.iteams.model.enums.ModuleType;
import com.iteams.model.enums.OperationType;
import com.iteams.model.enums.StatusType;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 操作日志实体类
 * <p>
 * 对应数据库表sys_operation_log，记录用户操作的详细信息
 * </p>
 */
@Data
@Entity
@Table(name = "sys_operation_log", 
    indexes = {
        @Index(name = "idx_operation_time", columnList = "operation_time"),
        @Index(name = "idx_operator_id", columnList = "operator_id"),
        @Index(name = "idx_module", columnList = "module"),
        @Index(name = "idx_operation_type", columnList = "operation_type"),
        @Index(name = "idx_object_id", columnList = "object_id"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_created_at", columnList = "created_at")
    }
)
public class OperationLog {
    
    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    
    /**
     * 操作类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "operation_type", nullable = false, length = 20)
    private OperationType operationType;
    
    /**
     * 操作模块
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "module", nullable = false, length = 50)
    private ModuleType module;
    
    /**
     * 操作对象ID
     */
    @Column(name = "object_id", length = 36)
    private String objectId;
    
    /**
     * 操作对象类型
     */
    @Column(name = "object_type", length = 50)
    private String objectType;
    
    /**
     * 操作前数据(JSON格式)
     */
    @Column(name = "before_data", columnDefinition = "LONGTEXT")
    private String beforeData;
    
    /**
     * 操作后数据(JSON格式)
     */
    @Column(name = "after_data", columnDefinition = "LONGTEXT")
    private String afterData;
    
    /**
     * 操作人ID
     */
    @Column(name = "operator_id", nullable = false, length = 36)
    private String operatorId;
    
    /**
     * 操作人姓名
     */
    @Column(name = "operator_name", nullable = false, length = 50)
    private String operatorName;
    
    /**
     * 操作时间
     */
    @Column(name = "operation_time", nullable = false)
    private LocalDateTime operationTime;
    
    /**
     * 操作者IP地址
     */
    @Column(name = "ip_address", length = 50)
    private String ipAddress;
    
    /**
     * 用户代理字符串
     */
    @Column(name = "user_agent", length = 500)
    private String userAgent;
    
    /**
     * 操作结果状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StatusType status;
    
    /**
     * 错误信息
     */
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
    
    /**
     * 操作描述
     */
    @Column(name = "description", length = 500)
    private String description;
    
    /**
     * 记录创建时间
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * 创建前初始化
     */
    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.operationTime == null) {
            this.operationTime = LocalDateTime.now();
        }
    }
} 