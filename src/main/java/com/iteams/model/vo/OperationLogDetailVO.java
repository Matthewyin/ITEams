package com.iteams.model.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 操作日志详情展示对象
 * <p>
 * 用于返回给前端展示的日志详细数据
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OperationLogDetailVO extends OperationLogVO {
    
    /**
     * 操作前数据(JSON格式)
     */
    private String beforeData;
    
    /**
     * 操作后数据(JSON格式)
     */
    private String afterData;
    
    /**
     * 用户代理字符串
     */
    private String userAgent;
    
    /**
     * 错误信息
     */
    private String errorMessage;
} 