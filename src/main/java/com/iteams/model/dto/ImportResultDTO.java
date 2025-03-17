package com.iteams.model.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 导入结果统计数据传输对象
 * <p>
 * 该DTO用于向前端传递Excel导入完成后的统计结果信息，
 * 包含批次标识、导入时间、资产总数、成功/失败数量等统计指标。
 * 与ImportProgressDTO不同，该对象用于展示导入完成后的最终结果概要，
 * 而非实时进度。
 * </p>
 */
@Data
public class ImportResultDTO {

    /**
     * 导入批次ID，用于关联同批次导入的所有资产
     * 可用于后续查询或操作特定批次的数据
     */
    private String batchId;
    
    /**
     * 导入完成时间
     * 记录导入任务结束的时间点
     */
    private LocalDateTime importTime;
    
    /**
     * 总资产数量
     * 实际成功导入的资产记录总数
     */
    private long totalAssets;
    
    /**
     * 成功导入数量
     * 成功处理并保存的记录数
     */
    private long successCount;
    
    /**
     * 导入失败数量
     * 由于各种原因（如数据格式错误、重复等）导致失败的记录数
     */
    private long failedCount;
    
    /**
     * 导入操作执行人
     * 记录执行导入操作的用户标识
     */
    private String importUser;
    
    /**
     * 导入耗时（秒）
     * 记录从开始到完成导入所花费的时间
     */
    private double costTime;
} 