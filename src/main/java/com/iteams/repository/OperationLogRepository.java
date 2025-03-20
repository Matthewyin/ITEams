package com.iteams.repository;

import com.iteams.model.entity.OperationLog;
import com.iteams.model.enums.StatusType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 操作日志仓库接口
 */
@Repository
public interface OperationLogRepository extends JpaRepository<OperationLog, String>, JpaSpecificationExecutor<OperationLog> {
    
    /**
     * 统计某时间段内不同操作类型的数量
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 操作类型和数量的映射
     */
    @Query("SELECT o.operationType as type, COUNT(o) as count FROM OperationLog o " +
           "WHERE (:startTime IS NULL OR o.operationTime >= :startTime) " +
           "AND (:endTime IS NULL OR o.operationTime <= :endTime) " +
           "GROUP BY o.operationType")
    List<Map<String, Object>> countByOperationType(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 统计某时间段内不同模块的操作数量
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 模块和数量的映射
     */
    @Query("SELECT o.module as module, COUNT(o) as count FROM OperationLog o " +
           "WHERE (:startTime IS NULL OR o.operationTime >= :startTime) " +
           "AND (:endTime IS NULL OR o.operationTime <= :endTime) " +
           "GROUP BY o.module")
    List<Map<String, Object>> countByModule(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 统计某时间段内每日的操作数量
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 日期和数量的映射
     */
    @Query("SELECT FUNCTION('DATE_FORMAT', o.operationTime, '%Y-%m-%d') as date, " +
           "COUNT(o) as count, " +
           "SUM(CASE WHEN o.status = 'SUCCESS' THEN 1 ELSE 0 END) as successCount, " +
           "SUM(CASE WHEN o.status = 'FAILED' THEN 1 ELSE 0 END) as failedCount " +
           "FROM OperationLog o " +
           "WHERE (:startTime IS NULL OR o.operationTime >= :startTime) " +
           "AND (:endTime IS NULL OR o.operationTime <= :endTime) " +
           "GROUP BY FUNCTION('DATE_FORMAT', o.operationTime, '%Y-%m-%d') " +
           "ORDER BY date")
    List<Map<String, Object>> countByDay(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 统计某时间段内不同操作人的操作数量
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param limit 限制返回的数量
     * @return 操作人和数量的映射
     */
    @Query(value = "SELECT o.operator_id as operatorId, o.operator_name as operatorName, COUNT(o.id) as count " +
           "FROM sys_operation_log o " +
           "WHERE (:startTime IS NULL OR o.operation_time >= :startTime) " +
           "AND (:endTime IS NULL OR o.operation_time <= :endTime) " +
           "GROUP BY o.operator_id, o.operator_name " +
           "ORDER BY count DESC " +
           "LIMIT :limit", nativeQuery = true)
    List<Map<String, Object>> countByOperator(LocalDateTime startTime, LocalDateTime endTime, int limit);
    
    /**
     * 统计某时间段内的总操作数量
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 总数量
     */
    long countByOperationTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 统计某时间段内的成功操作数量
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param status 状态
     * @return 成功数量
     */
    long countByOperationTimeBetweenAndStatus(LocalDateTime startTime, LocalDateTime endTime, StatusType status);
} 