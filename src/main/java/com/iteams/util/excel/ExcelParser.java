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

@Slf4j
@Component
public class ExcelParser {

    private static final Map<String, Integer> DEFAULT_HEADER_MAP = Map.ofEntries(
            Map.entry("资产编号", 0),
            Map.entry("资产名称", 1),
            Map.entry("资产分类", 2),
            Map.entry("使用人", 3),
            Map.entry("入账日期", 4),
            Map.entry("使用部门", 5),
            Map.entry("合同号", 6),
            Map.entry("一级分类", 7),
            Map.entry("二级分类", 8),
            Map.entry("三级分类", 9),
            Map.entry("品牌", 10),
            Map.entry("型号", 11),
            Map.entry("序列号", 12),
            Map.entry("变更记录", 13),
            Map.entry("数据中心", 14),
            Map.entry("机房名称", 15),
            Map.entry("机柜编号", 16),
            Map.entry("U位编号", 17),
            Map.entry("使用环境", 18),
            Map.entry("保管人", 19),
            Map.entry("资产状态", 20),
            Map.entry("应用系统/项目", 21),
            Map.entry("到货验收日期", 22),
            Map.entry("资产使用年限(年)", 23),
            Map.entry("维保开始日期", 24),
            Map.entry("维保结束日期", 25),
            Map.entry("维保提供商", 26),
            Map.entry("维保状态", 27),
            Map.entry("备注", 28),
            Map.entry("变更后数据中心", 29),
            Map.entry("变更后机房名称", 30),
            Map.entry("变更后机柜编号", 31),
            Map.entry("变更后U位编号", 32),
            Map.entry("变更后使用环境", 33),
            Map.entry("变更后保管人", 34),
            Map.entry("变更后应用系统/项目", 35),
            Map.entry("变更后型号", 36),
            Map.entry("变更后序列号", 37),
            Map.entry("变更后资产状态", 38),
            Map.entry("初始化", 39),
            Map.entry("拥有者", 40),
            Map.entry("创建人", 41),
            Map.entry("创建时间", 42),
            Map.entry("最近更新时间", 43)
    );

    // 动态保存当前正在处理的任务状态
    private final Map<String, ImportTaskStatus> taskStatusMap = new ConcurrentHashMap<>();

    public String parseFile(MultipartFile file, RowProcessor processor) throws IOException {
        String taskId = UUID.randomUUID().toString();
        
        ImportTaskStatus status = new ImportTaskStatus();
        status.setState(ImportTaskStatus.State.PROCESSING);
        taskStatusMap.put(taskId, status);
        
        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(inputStream);
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
    
    public ImportTaskStatus getTaskStatus(String taskId) {
        return taskStatusMap.getOrDefault(taskId, null);
    }
    
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
        
        // 验证必要的列是否存在
        for (String requiredHeader : Arrays.asList("资产编号", "资产名称", "一级分类", "二级分类", "三级分类")) {
            if (!result.containsKey(requiredHeader)) {
                log.error("缺少必要列: {}", requiredHeader);
                return null;
            }
        }
        
        return result;
    }
    
    private Map<String, Object> extractRowData(Row row, Map<String, Integer> headerMap) {
        Map<String, Object> rowData = new HashMap<>();
        
        for (Map.Entry<String, Integer> entry : headerMap.entrySet()) {
            String columnName = entry.getKey();
            int columnIndex = entry.getValue();
            
            if (columnIndex >= row.getLastCellNum()) continue;
            
            Cell cell = row.getCell(columnIndex);
            if (cell == null) continue;
            
            Object cellValue = getCellValue(cell);
            rowData.put(columnName, cellValue);
        }
        
        return rowData;
    }
    
    private Object getCellValue(Cell cell) {
        if (cell == null) return null;
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                } else {
                    return cell.getNumericCellValue();
                }
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case FORMULA:
                switch (cell.getCachedFormulaResultType()) {
                    case STRING:
                        return cell.getStringCellValue();
                    case NUMERIC:
                        return cell.getNumericCellValue();
                    default:
                        return null;
                }
            default:
                return null;
        }
    }
    
    private int countRows(Sheet sheet) {
        int lastRowNum = sheet.getLastRowNum();
        int count = 0;
        
        for (int i = 1; i <= lastRowNum; i++) {
            if (sheet.getRow(i) != null) {
                count++;
            }
        }
        
        return count;
    }

    // 导入任务状态类
    public static class ImportTaskStatus {
        public enum State {
            PROCESSING, COMPLETED, FAILED
        }
        
        private State state;
        private double progress;
        private int totalRows;
        private int processedRows;
        private int successRows;
        private List<Integer> failedRows = new ArrayList<>();
        private List<String> errors = new ArrayList<>();
        private String error;

        // Getters and Setters
        public State getState() {
            return state;
        }

        public void setState(State state) {
            this.state = state;
        }

        public double getProgress() {
            return progress;
        }

        public void setProgress(double progress) {
            this.progress = progress;
        }

        public int getTotalRows() {
            return totalRows;
        }

        public void setTotalRows(int totalRows) {
            this.totalRows = totalRows;
        }

        public int getProcessedRows() {
            return processedRows;
        }

        public void setProcessedRows(int processedRows) {
            this.processedRows = processedRows;
        }

        public int getSuccessRows() {
            return successRows;
        }

        public void setSuccessRows(int successRows) {
            this.successRows = successRows;
        }

        public List<Integer> getFailedRows() {
            return failedRows;
        }

        public List<String> getErrors() {
            return errors;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }
    }
} 