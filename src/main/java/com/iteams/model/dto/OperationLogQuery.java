package com.iteams.model.dto;

import com.iteams.model.enums.ModuleType;
import com.iteams.model.enums.OperationType;
import com.iteams.model.enums.StatusType;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 操作日志查询条件对象
 * <p>
 * 用于接收前端传递的查询参数
 * </p>
 */
@Data
public class OperationLogQuery {
    
    /**
     * 当前页码
     */
    private Integer page = 1;
    
    /**
     * 每页记录数
     */
    private Integer pageSize = 10;
    
    /**
     * 开始时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startTime;
    
    /**
     * 结束时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endTime;
    
    /**
     * 操作类型列表，多个类型用逗号分隔转换成列表
     */
    private List<OperationType> operationTypes;
    
    /**
     * 操作模块列表，多个模块用逗号分隔转换成列表
     */
    private List<ModuleType> modules;
    
    /**
     * 操作人ID
     */
    private String operatorId;
    
    /**
     * 操作结果状态
     */
    private StatusType status;
    
    /**
     * 关键词
     */
    private String keyword;
} 