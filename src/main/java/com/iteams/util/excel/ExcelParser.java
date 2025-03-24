package com.iteams.util.excel;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Excel文件解析器
 * <p>
 * 该组件负责解析上传的Excel文件，提取数据，并通过回调接口处理每一行数据。
 * 支持异步处理大型Excel文件，并提供实时的进度跟踪和错误处理功能。
 * 整个解析过程分为以下步骤：
 * <ol>
 *   <li>解析表头，建立列名与列索引的映射</li>
 *   <li>逐行读取数据，构建行数据Map</li>
 *   <li>通过RowProcessor回调处理每行数据</li>
 *   <li>跟踪处理进度和结果状态</li>
 * </ol>
 * </p>
 * <p>
 * 该解析器针对IT资产管理系统进行了优化，包含了预定义的表头映射，
 * 用于处理系统预期的资产导入模板。
 * </p>
 */
@Slf4j
@Component
public class ExcelParser {

    // 移除了未使用的DEFAULT_HEADER_MAP字段

    /**
     * 任务状态映射表
     * <p>
     * 用于存储和跟踪所有正在进行的Excel解析任务的状态。
     * 键为任务ID，值为对应的ImportTaskStatus对象。
     * 该映射支持并发访问，允许同时处理多个Excel导入任务。
     * </p>
     */
    private final Map<String, ImportTaskStatus> taskStatusMap = new ConcurrentHashMap<>();

    /**
     * 解析Excel文件
     * <p>
     * 主要方法，负责完整的Excel文件解析流程。接收上传的Excel文件和行处理器，
     * 异步解析文件内容，并通过行处理器处理每一行数据。
     * </p>
     * <p>
     * 解析过程中会实时更新任务状态，包括：
     * <ul>
     *   <li>总行数和已处理行数</li>
     *   <li>成功和失败的行数</li>
     *   <li>错误信息和失败的行号</li>
     *   <li>进度百分比</li>
     * </ul>
     * </p>
     * 
     * @param file 上传的Excel文件（MultipartFile格式）
     * @param processor 行处理器，实现RowProcessor接口的实例
     * @return 任务ID，用于后续查询任务状态
     * @throws IOException 文件读取或解析异常
     */
    public String parseFile(MultipartFile file, RowProcessor processor) throws IOException {
        String taskId = UUID.randomUUID().toString();
        
        ImportTaskStatus status = new ImportTaskStatus();
        status.setState(ImportTaskStatus.State.PROCESSING);
        taskStatusMap.put(taskId, status);
        
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);

            // 标题行验证
            Map<String, Integer> headerMap = validateAndExtractHeaders(sheet);
            if (headerMap == null) {
                status.setState(ImportTaskStatus.State.FAILED);
                status.setError("表头格式不匹配");
                return taskId;
            }

            status.setTotalRows(countRows(sheet));
            AtomicInteger processedRowCount = new AtomicInteger(0);
            AtomicInteger successRowCount = new AtomicInteger(0);

            // 从第二行开始处理数据（跳过标题行）
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                try {
                    Map<String, Object> rowData = extractRowData(row, headerMap);
                    boolean success = processor.processRow(rowData, i);
                    if (success) {
                        successRowCount.incrementAndGet();
                    } else {
                        status.getFailedRows().add(i + 1); // Excel行号从1开始
                    }
                } catch (Exception e) {
                    log.error("处理第{}行时发生错误: {}", i + 1, e.getMessage(), e);
                    status.getFailedRows().add(i + 1);
                    status.getErrors().add(String.format("行%d: %s", i + 1, e.getMessage()));
                }
                
                processedRowCount.incrementAndGet();
                status.setProcessedRows(processedRowCount.get());
                status.setSuccessRows(successRowCount.get());
                
                // 每处理10行更新一次进度
                if (i % 10 == 0) {
                    status.setProgress((double) i / sheet.getLastRowNum());
                }
            }
            
            status.setProgress(1.0);
            status.setState(ImportTaskStatus.State.COMPLETED);
            return taskId;
            
        } catch (Exception e) {
            log.error("解析Excel文件失败", e);
            status.setState(ImportTaskStatus.State.FAILED);
            status.setError("文件解析异常: " + e.getMessage());
            return taskId;
        }
    }
    
    /**
     * 获取任务状态
     * <p>
     * 根据任务ID查询Excel导入任务的当前状态。
     * 可用于前端轮询获取导入进度和结果。
     * </p>
     * 
     * @param taskId 任务ID，由parseFile方法返回
     * @return 任务状态对象，若任务不存在则返回null
     */
    public ImportTaskStatus getTaskStatus(String taskId) {
        return taskStatusMap.getOrDefault(taskId, null);
    }
    
    /**
     * 验证并提取表头
     * <p>
     * 从Excel的首行提取表头信息，构建列名与列索引的映射。
     * 这个映射用于后续定位和提取特定列的数据。
     * </p>
     * 
     * @param sheet Excel工作表
     * @return 列名到列索引的映射，如果表头无效则返回null
     */
    private Map<String, Integer> validateAndExtractHeaders(Sheet sheet) {
        Row headerRow = sheet.getRow(0);
        if (headerRow == null) return null;
        
        Map<String, Integer> result = new HashMap<>();
        
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            Cell cell = headerRow.getCell(i);
            if (cell != null) {
                String headerText = cell.getStringCellValue().trim();
                result.put(headerText, i);
            }
        }
        
        // 至少包含资产编号和资产名称两个必要列
        return (result.containsKey("资产编号") && result.containsKey("资产名称")) ? result : null;
    }
    
    /**
     * 提取行数据
     * <p>
     * 根据表头映射提取一行中的所有单元格数据，构建列名到单元格值的映射。
     * 处理不同类型的单元格（字符串、数字、日期等），转换为合适的Java类型。
     * </p>
     * 
     * @param row Excel行对象
     * @param headerMap 表头映射（列名到列索引）
     * @return 列名到单元格值的映射
     */
    private Map<String, Object> extractRowData(Row row, Map<String, Integer> headerMap) {
        Map<String, Object> rowData = new HashMap<>();
        
        for (Map.Entry<String, Integer> entry : headerMap.entrySet()) {
            String columnName = entry.getKey();
            int columnIndex = entry.getValue();
            
            Cell cell = row.getCell(columnIndex);
            if (cell != null) {
                Object cellValue = getCellValue(cell);
                rowData.put(columnName, cellValue);
            }
        }
        
        return rowData;
    }
    
    /**
     * 获取单元格值
     * <p>
     * 根据单元格类型提取其值，并转换为合适的Java对象类型。
     * 支持字符串、数字、布尔值、日期等多种单元格类型。
     * </p>
     * 
     * @param cell Excel单元格对象
     * @return 单元格值，类型可能是String、Double、Boolean或Date
     */
    private Object getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
                
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                } else {
                    // 获取数值而不是科学计数法表示
                    double value = cell.getNumericCellValue();
                    // 如果是整数，去掉小数部分
                    if (value == Math.floor(value) && !Double.isInfinite(value)) {
                        if (value <= Long.MAX_VALUE && value >= Long.MIN_VALUE) {
                            return (long) value;
                        }
                    }
                    return value;
                }
                
            case BOOLEAN:
                return cell.getBooleanCellValue();
                
            case FORMULA:
                return cell.getCellFormula();
                
            case BLANK:
            case ERROR:
            default:
                return null;
        }
    }
    
    /**
     * 计算有效行数
     * <p>
     * 计算Excel中的有效数据行数（不包括表头）。
     * 跳过空行和标题行。
     * </p>
     * 
     * @param sheet Excel工作表
     * @return 有效数据行数
     */
    private int countRows(Sheet sheet) {
        int count = 0;
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                count++;
            }
        }
        return count;
    }

    /**
     * 导入任务状态类
     * <p>
     * 内部静态类，用于存储和跟踪Excel导入任务的状态信息。
     * 包含任务的处理状态、进度、成功/失败统计、错误信息等。
     * 此类的实例通过taskStatusMap映射与特定任务ID关联。
     * </p>
     */
    public static class ImportTaskStatus {
        /**
         * 任务状态枚举
         * <p>
         * PROCESSING: 处理中 - 任务正在进行
         * COMPLETED: 已完成 - 任务已成功完成
         * FAILED: 失败 - 任务因错误而中止
         * </p>
         */
        public enum State {
            PROCESSING, COMPLETED, FAILED
        }
        
        /**
         * 当前任务状态
         */
        private State state;
        
        /**
         * 处理进度，范围0.0-1.0
         */
        private double progress;
        
        /**
         * 总行数（不含表头）
         */
        private int totalRows;
        
        /**
         * 已处理行数
         */
        private int processedRows;
        
        /**
         * 成功处理的行数
         */
        private int successRows;
        
        /**
         * 处理失败的行号列表
         */
        private List<Integer> failedRows = new ArrayList<>();
        
        /**
         * 详细错误信息列表
         */
        private List<String> errors = new ArrayList<>();
        
        /**
         * 任务级别错误信息
         */
        private String error;
        
        /**
         * 附加数据，用于存储特定于任务的额外信息
         * 例如导入批次ID等，可以在任务完成后传递给前端
         */
        private Map<String, Object> extraData = new HashMap<>();

        // Getters and Setters
        /**
         * 获取任务状态
         * @return 当前状态枚举值
         */
        public State getState() {
            return state;
        }

        /**
         * 设置任务状态
         * @param state 新的状态枚举值
         */
        public void setState(State state) {
            this.state = state;
        }

        /**
         * 获取处理进度
         * @return 进度值，范围0.0-1.0
         */
        public double getProgress() {
            return progress;
        }

        /**
         * 设置处理进度
         * @param progress 新的进度值，范围0.0-1.0
         */
        public void setProgress(double progress) {
            this.progress = progress;
        }

        /**
         * 获取总行数
         * @return 需要处理的总行数
         */
        public int getTotalRows() {
            return totalRows;
        }

        /**
         * 设置总行数
         * @param totalRows 需要处理的总行数
         */
        public void setTotalRows(int totalRows) {
            this.totalRows = totalRows;
        }

        /**
         * 获取已处理行数
         * @return 当前已处理的行数
         */
        public int getProcessedRows() {
            return processedRows;
        }

        /**
         * 设置已处理行数
         * @param processedRows 当前已处理的行数
         */
        public void setProcessedRows(int processedRows) {
            this.processedRows = processedRows;
        }

        /**
         * 获取成功行数
         * @return 成功处理的行数
         */
        public int getSuccessRows() {
            return successRows;
        }

        /**
         * 设置成功行数
         * @param successRows 成功处理的行数
         */
        public void setSuccessRows(int successRows) {
            this.successRows = successRows;
        }

        /**
         * 获取失败行号列表
         * @return 处理失败的行号集合
         */
        public List<Integer> getFailedRows() {
            return failedRows;
        }

        /**
         * 获取错误信息列表
         * @return 详细错误信息集合
         */
        public List<String> getErrors() {
            return errors;
        }

        /**
         * 获取任务级别错误信息
         * @return 错误描述
         */
        public String getError() {
            return error;
        }

        /**
         * 设置任务级别错误信息
         * @param error 错误描述
         */
        public void setError(String error) {
            this.error = error;
        }
        
        /**
         * 获取附加数据
         * @return 附加数据Map
         */
        public Map<String, Object> getExtraData() {
            return extraData;
        }
        
        /**
         * 添加附加数据
         * @param key 键名
         * @param value 值
         */
        public void addExtraData(String key, Object value) {
            this.extraData.put(key, value);
        }
    }
} 