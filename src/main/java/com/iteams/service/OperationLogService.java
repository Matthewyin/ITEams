package com.iteams.service;

import com.iteams.model.dto.OperationLogQuery;
import com.iteams.model.vo.OperationLogDetailVO;
import com.iteams.model.vo.OperationLogStatsVO;
import com.iteams.model.vo.OperationLogVO;
import org.springframework.data.domain.Page;

import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

/**
 * 操作日志服务接口
 * <p>
 * 提供操作日志的查询、导出和统计功能
 * </p>
 */
public interface OperationLogService {
    
    /**
     * 分页查询操作日志
     *
     * @param query 查询条件
     * @return 分页数据
     */
    Page<OperationLogVO> getLogs(OperationLogQuery query);
    
    /**
     * 根据ID获取操作日志详情
     *
     * @param id 日志ID
     * @return 日志详情
     */
    OperationLogDetailVO getLogDetail(String id);
    
    /**
     * 导出操作日志
     *
     * @param query 查询条件
     * @param response HTTP响应对象
     */
    void exportLogs(OperationLogQuery query, HttpServletResponse response);
    
    /**
     * 统计操作日志
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param module 模块
     * @return 统计数据
     */
    OperationLogStatsVO getLogStats(LocalDateTime startTime, LocalDateTime endTime, String module);
    
    /**
     * 批量删除操作日志
     *
     * @param ids 日志ID列表
     * @return 删除数量
     */
    int deleteLogs(String[] ids);
    
    /**
     * 清空操作日志
     *
     * @return 删除数量
     */
    int clearLogs();
    
    /**
     * 归档指定日期之前的日志
     *
     * @param beforeTime 日期
     * @return 归档数量
     */
    int archiveLogs(LocalDateTime beforeTime);
}
