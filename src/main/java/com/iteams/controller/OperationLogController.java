package com.iteams.controller;

import com.iteams.annotation.OperationLog;
import com.iteams.model.dto.OperationLogQuery;
import com.iteams.model.enums.ModuleType;
import com.iteams.model.enums.OperationType;
import com.iteams.model.vo.ApiResponse;
import com.iteams.model.vo.OperationLogDetailVO;
import com.iteams.model.vo.OperationLogStatsVO;
import com.iteams.model.vo.OperationLogVO;
import com.iteams.service.OperationLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

/**
 * 操作日志控制器
 * <p>
 * 提供操作日志的查询、导出和统计相关接口
 * </p>
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/logs")
@PreAuthorize("hasRole('ADMIN') or hasAuthority('LOG_VIEW')")
public class OperationLogController {
    
    private final OperationLogService operationLogService;
    
    /**
     * 分页查询操作日志
     * <p>
     * 根据多种条件筛选，支持分页
     * </p>
     * 
     * @param query 查询条件
     * @return 分页数据
     */
    @GetMapping
    @OperationLog(module = ModuleType.SYSTEM, operationType = OperationType.QUERY, description = "查询操作日志")
    public ApiResponse<Page<OperationLogVO>> getLogs(OperationLogQuery query) {
        Page<OperationLogVO> page = operationLogService.getLogs(query);
        return ApiResponse.success(page);
    }
    
    /**
     * 获取日志详情
     * <p>
     * 返回包含操作前后数据的详细信息
     * </p>
     * 
     * @param id 日志ID
     * @return 日志详情
     */
    @GetMapping("/{id}")
    @OperationLog(module = ModuleType.SYSTEM, operationType = OperationType.QUERY, description = "获取日志详情")
    public ApiResponse<OperationLogDetailVO> getLogDetail(@PathVariable String id) {
        OperationLogDetailVO detail = operationLogService.getLogDetail(id);
        return ApiResponse.success(detail);
    }
    
    /**
     * 导出操作日志
     * <p>
     * 将符合条件的日志导出为Excel文件
     * </p>
     * 
     * @param query 查询条件
     * @param response HTTP响应对象
     */
    @PostMapping("/export")
    @OperationLog(module = ModuleType.SYSTEM, operationType = OperationType.EXPORT, description = "导出操作日志")
    public void exportLogs(@RequestBody OperationLogQuery query, HttpServletResponse response) {
        operationLogService.exportLogs(query, response);
    }
    
    /**
     * 获取日志统计数据
     * <p>
     * 返回统计信息，包括各类型操作数量、模块分布、操作人统计等
     * </p>
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param module 模块
     * @return 统计数据
     */
    @GetMapping("/stats")
    @OperationLog(module = ModuleType.SYSTEM, operationType = OperationType.QUERY, description = "获取日志统计")
    public ApiResponse<OperationLogStatsVO> getLogStats(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime endTime,
            @RequestParam(required = false) String module) {
        
        OperationLogStatsVO stats = operationLogService.getLogStats(startTime, endTime, module);
        return ApiResponse.success(stats);
    }
    
    /**
     * 批量删除日志
     * <p>
     * 删除指定ID的日志记录
     * </p>
     * 
     * @param ids 日志ID数组
     * @return 删除数量
     */
    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    @OperationLog(module = ModuleType.SYSTEM, operationType = OperationType.DELETE, description = "批量删除日志")
    public ApiResponse<Integer> deleteLogs(@RequestBody @NotEmpty String[] ids) {
        int count = operationLogService.deleteLogs(ids);
        return ApiResponse.success(count, "成功删除" + count + "条日志");
    }
    
    /**
     * 清空日志
     * <p>
     * 删除所有日志记录，需要管理员权限
     * </p>
     * 
     * @return 删除数量
     */
    @DeleteMapping("/clear")
    @PreAuthorize("hasRole('ADMIN')")
    @OperationLog(module = ModuleType.SYSTEM, operationType = OperationType.DELETE, description = "清空日志")
    public ApiResponse<Integer> clearLogs() {
        int count = operationLogService.clearLogs();
        return ApiResponse.success(count, "成功清空" + count + "条日志");
    }
    
    /**
     * 归档日志
     * <p>
     * 归档指定日期之前的日志，需要管理员权限
     * </p>
     * 
     * @param beforeTime 日期
     * @return 归档数量
     */
    @PostMapping("/archive")
    @PreAuthorize("hasRole('ADMIN')")
    @OperationLog(module = ModuleType.SYSTEM, operationType = OperationType.EXPORT, description = "归档日志")
    public ApiResponse<Integer> archiveLogs(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime beforeTime) {
        int count = operationLogService.archiveLogs(beforeTime);
        return ApiResponse.success(count, "成功归档" + count + "条日志");
    }
}
