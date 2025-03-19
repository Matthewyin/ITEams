package com.iteams.service.impl;

import com.iteams.model.dto.OperationLogQuery;
import com.iteams.model.entity.OperationLog;
import com.iteams.model.enums.ModuleType;
import com.iteams.model.enums.OperationType;
import com.iteams.model.enums.StatusType;
import com.iteams.model.vo.OperationLogDetailVO;
import com.iteams.model.vo.OperationLogStatsVO;
import com.iteams.model.vo.OperationLogVO;
import com.iteams.repository.OperationLogRepository;
import com.iteams.service.OperationLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 操作日志服务实现类
 * <p>
 * 提供操作日志的查询、导出和统计功能
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OperationLogServiceImpl implements OperationLogService {
    
    private final OperationLogRepository operationLogRepository;
    
    /**
     * 分页查询操作日志
     *
     * @param query 查询条件
     * @return 分页数据
     */
    @Override
    public Page<OperationLogVO> getLogs(OperationLogQuery query) {
        // 构建查询条件
        Specification<OperationLog> spec = buildSpecification(query);
        
        // 构建分页和排序
        PageRequest pageRequest = PageRequest.of(
            query.getPage() - 1, 
            query.getPageSize(),
            Sort.by(Sort.Direction.DESC, "operationTime")
        );
        
        // 执行查询
        Page<OperationLog> page = operationLogRepository.findAll(spec, pageRequest);
        
        // 转换结果
        List<OperationLogVO> voList = page.getContent().stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
        
        return new PageImpl<>(voList, pageRequest, page.getTotalElements());
    }
    
    /**
     * 根据ID获取操作日志详情
     *
     * @param id 日志ID
     * @return 日志详情
     */
    @Override
    public OperationLogDetailVO getLogDetail(String id) {
        OperationLog log = operationLogRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("日志不存在: " + id));
        
        OperationLogDetailVO vo = new OperationLogDetailVO();
        BeanUtils.copyProperties(convertToVO(log), vo);
        
        // 设置详情特有的字段
        vo.setBeforeData(log.getBeforeData());
        vo.setAfterData(log.getAfterData());
        vo.setUserAgent(log.getUserAgent());
        vo.setErrorMessage(log.getErrorMessage());
        
        return vo;
    }
    
    /**
     * 导出操作日志
     *
     * @param query 查询条件
     * @param response HTTP响应对象
     */
    @Override
    public void exportLogs(OperationLogQuery query, HttpServletResponse response) {
        try {
            // 不分页，获取所有符合条件的记录
            query.setPage(1);
            query.setPageSize(Integer.MAX_VALUE);
            
            // 执行查询
            List<OperationLogVO> logs = getLogs(query).getContent();
            
            // 创建工作簿和工作表
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("操作日志");
            
            // 设置列宽
            sheet.setColumnWidth(0, 20 * 256);
            sheet.setColumnWidth(1, 15 * 256);
            sheet.setColumnWidth(2, 15 * 256);
            sheet.setColumnWidth(3, 30 * 256);
            sheet.setColumnWidth(4, 20 * 256);
            sheet.setColumnWidth(5, 15 * 256);
            sheet.setColumnWidth(6, 20 * 256);
            sheet.setColumnWidth(7, 15 * 256);
            
            // 创建表头
            Row headerRow = sheet.createRow(0);
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            
            String[] headers = {"操作时间", "操作类型", "操作模块", "操作描述", "操作人", "IP地址", "操作对象", "操作结果"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // 填充数据
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            int rowNum = 1;
            for (OperationLogVO log : logs) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(log.getOperationTime().format(formatter));
                row.createCell(1).setCellValue(log.getOperationTypeDesc());
                row.createCell(2).setCellValue(log.getModuleDesc());
                row.createCell(3).setCellValue(log.getDescription());
                row.createCell(4).setCellValue(log.getOperatorName());
                row.createCell(5).setCellValue(log.getIpAddress());
                row.createCell(6).setCellValue(log.getObjectId() != null ? log.getObjectId() : "");
                row.createCell(7).setCellValue(log.getStatusDesc());
            }
            
            // 设置响应头
            String fileName = URLEncoder.encode("操作日志_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")), StandardCharsets.UTF_8);
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xlsx");
            
            // 写入响应
            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (IOException e) {
            log.error("导出操作日志失败", e);
            throw new RuntimeException("导出失败: " + e.getMessage());
        }
    }
    
    /**
     * 统计操作日志
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param module 模块
     * @return 统计数据
     */
    @Override
    public OperationLogStatsVO getLogStats(LocalDateTime startTime, LocalDateTime endTime, String module) {
        OperationLogStatsVO stats = new OperationLogStatsVO();
        
        // 统计总记录数
        long totalCount = operationLogRepository.countByOperationTimeBetween(startTime, endTime);
        stats.setTotalCount(totalCount);
        
        // 统计成功和失败记录数
        long successCount = operationLogRepository.countByOperationTimeBetweenAndStatus(startTime, endTime, StatusType.SUCCESS);
        stats.setSuccessCount(successCount);
        stats.setFailedCount(totalCount - successCount);
        
        // 统计各类型操作
        List<Map<String, Object>> typeStats = operationLogRepository.countByOperationType(startTime, endTime);
        Map<String, Long> typeMap = typeStats.stream()
            .collect(HashMap::new, 
                (map, item) -> {
                    OperationType type = (OperationType) item.get("type");
                    Long count = ((Number) item.get("count")).longValue();
                    map.put(type.getDescription(), count);
                }, 
                HashMap::putAll);
        stats.setTypeStats(typeMap);
        
        // 统计各模块操作
        List<Map<String, Object>> moduleStats = operationLogRepository.countByModule(startTime, endTime);
        Map<String, Long> moduleMap = moduleStats.stream()
            .collect(HashMap::new, 
                (map, item) -> {
                    ModuleType moduleType = (ModuleType) item.get("module");
                    Long count = ((Number) item.get("count")).longValue();
                    map.put(moduleType.getDescription(), count);
                }, 
                HashMap::putAll);
        stats.setModuleStats(moduleMap);
        
        // 统计每日数据
        List<Map<String, Object>> dailyStats = operationLogRepository.countByDay(startTime, endTime);
        List<OperationLogStatsVO.DailyStatVO> dailyList = dailyStats.stream()
            .map(item -> {
                OperationLogStatsVO.DailyStatVO daily = new OperationLogStatsVO.DailyStatVO();
                daily.setDate((String) item.get("date"));
                daily.setCount(((Number) item.get("count")).longValue());
                daily.setSuccessCount(((Number) item.get("successCount")).longValue());
                daily.setFailedCount(((Number) item.get("failedCount")).longValue());
                return daily;
            })
            .collect(Collectors.toList());
        stats.setDailyStats(dailyList);
        
        // 统计操作人数据（仅返回前10个）
        List<Map<String, Object>> operatorStats = operationLogRepository.countByOperator(startTime, endTime, 10);
        List<OperationLogStatsVO.OperatorStatVO> operatorList = operatorStats.stream()
            .map(item -> {
                OperationLogStatsVO.OperatorStatVO operator = new OperationLogStatsVO.OperatorStatVO();
                operator.setOperatorId((String) item.get("operatorId"));
                operator.setOperatorName((String) item.get("operatorName"));
                operator.setCount(((Number) item.get("count")).longValue());
                return operator;
            })
            .collect(Collectors.toList());
        stats.setOperatorStats(operatorList);
        
        return stats;
    }
    
    /**
     * 批量删除操作日志
     *
     * @param ids 日志ID列表
     * @return 删除数量
     */
    @Override
    @Transactional
    public int deleteLogs(String[] ids) {
        int count = 0;
        for (String id : ids) {
            operationLogRepository.deleteById(id);
            count++;
        }
        return count;
    }
    
    /**
     * 清空操作日志
     *
     * @return 删除数量
     */
    @Override
    @Transactional
    public int clearLogs() {
        long count = operationLogRepository.count();
        operationLogRepository.deleteAll();
        return (int) count;
    }
    
    /**
     * 归档指定日期之前的日志
     * 注：此为示例实现，实际归档应考虑数据量和性能问题
     *
     * @param beforeTime 日期
     * @return 归档数量
     */
    @Override
    @Transactional
    public int archiveLogs(LocalDateTime beforeTime) {
        // 此处应实现实际的归档逻辑，如复制到归档表
        // 此处简化为删除操作
        Specification<OperationLog> spec = (root, query, cb) -> 
            cb.lessThan(root.get("operationTime"), beforeTime);
        
        List<OperationLog> logs = operationLogRepository.findAll(spec);
        operationLogRepository.deleteAll(logs);
        
        return logs.size();
    }
    
    /**
     * 构建查询条件
     *
     * @param query 查询参数
     * @return 查询条件
     */
    private Specification<OperationLog> buildSpecification(OperationLogQuery query) {
        return (root, q, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 时间范围
            if (query.getStartTime() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("operationTime"), query.getStartTime()));
            }
            if (query.getEndTime() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("operationTime"), query.getEndTime()));
            }
            
            // 操作类型
            if (query.getOperationTypes() != null && !query.getOperationTypes().isEmpty()) {
                predicates.add(root.get("operationType").in(query.getOperationTypes()));
            }
            
            // 操作模块
            if (query.getModules() != null && !query.getModules().isEmpty()) {
                predicates.add(root.get("module").in(query.getModules()));
            }
            
            // 操作人
            if (StringUtils.hasText(query.getOperatorId())) {
                predicates.add(cb.equal(root.get("operatorId"), query.getOperatorId()));
            }
            
            // 状态
            if (query.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), query.getStatus()));
            }
            
            // 关键词
            if (StringUtils.hasText(query.getKeyword())) {
                String keyword = "%" + query.getKeyword() + "%";
                Predicate keywordPredicate = cb.or(
                    cb.like(root.get("description"), keyword),
                    cb.like(root.get("operatorName"), keyword),
                    cb.like(root.get("objectId"), keyword)
                );
                predicates.add(keywordPredicate);
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
    
    /**
     * 将实体转换为VO
     *
     * @param entity 实体
     * @return VO
     */
    private OperationLogVO convertToVO(OperationLog entity) {
        OperationLogVO vo = new OperationLogVO();
        BeanUtils.copyProperties(entity, vo);
        
        // 设置枚举的描述字段
        if (entity.getOperationType() != null) {
            vo.setOperationTypeDesc(entity.getOperationType().getDescription());
        }
        if (entity.getModule() != null) {
            vo.setModuleDesc(entity.getModule().getDescription());
        }
        if (entity.getStatus() != null) {
            vo.setStatusDesc(entity.getStatus().getDescription());
        }
        
        return vo;
    }
}
