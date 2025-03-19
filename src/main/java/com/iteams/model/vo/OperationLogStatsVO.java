package com.iteams.model.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 操作日志统计展示对象
 * <p>
 * 用于返回给前端展示的日志统计数据
 * </p>
 */
@Data
public class OperationLogStatsVO {
    
    /**
     * 总记录数
     */
    private Long totalCount;
    
    /**
     * 成功操作数
     */
    private Long successCount;
    
    /**
     * 失败操作数
     */
    private Long failedCount;
    
    /**
     * 各类型操作统计
     */
    private Map<String, Long> typeStats;
    
    /**
     * 各模块操作统计
     */
    private Map<String, Long> moduleStats;
    
    /**
     * 每日统计数据
     */
    private List<DailyStatVO> dailyStats;
    
    /**
     * 操作人统计
     */
    private List<OperatorStatVO> operatorStats;
    
    /**
     * 每日统计数据
     */
    @Data
    public static class DailyStatVO {
        /**
         * 日期，格式：yyyy-MM-dd
         */
        private String date;
        
        /**
         * 操作总数
         */
        private Long count;
        
        /**
         * 成功操作数
         */
        private Long successCount;
        
        /**
         * 失败操作数
         */
        private Long failedCount;
    }
    
    /**
     * 操作人统计数据
     */
    @Data
    public static class OperatorStatVO {
        /**
         * 操作人ID
         */
        private String operatorId;
        
        /**
         * 操作人姓名
         */
        private String operatorName;
        
        /**
         * 操作总数
         */
        private Long count;
    }
} 