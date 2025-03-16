package io.nssa.iteams.util;

import io.nssa.iteams.entity.WarrantyContract;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Excel处理工具类
 */
public class ExcelHelper {

    private static final String EXCEL_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 判断文件是否为Excel文件
     * @param file 上传的文件
     * @return 是否为Excel文件
     */
    public static boolean isExcelFile(MultipartFile file) {
        return EXCEL_TYPE.equals(file.getContentType());
    }

    /**
     * 将保修合同列表导出为Excel
     * @param warranties 保修合同列表
     * @return 包含Excel内容的输入流
     */
    public static ByteArrayInputStream warrantiesToExcel(List<WarrantyContract> warranties) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("保修合同");

            // 表头样式
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // 创建表头
            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID", "资产ID", "合同编号", "服务提供商", "开始日期", "结束日期", "描述", "金额", "联系方式"};

            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
                sheet.autoSizeColumn(i);
            }

            int rowIdx = 1;
            for (WarrantyContract warranty : warranties) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(warranty.getWarrantyId());
                row.createCell(1).setCellValue(warranty.getAssetId());
                row.createCell(2).setCellValue(warranty.getContractNo());
                row.createCell(3).setCellValue(warranty.getProvider());
                row.createCell(4).setCellValue(warranty.getStartDate().format(DATE_FORMATTER));
                row.createCell(5).setCellValue(warranty.getEndDate().format(DATE_FORMATTER));
                row.createCell(6).setCellValue(warranty.getDescription() != null ? warranty.getDescription() : "");
                row.createCell(7).setCellValue(warranty.getContractCost() != null ? warranty.getContractCost().doubleValue() : 0.0);
                row.createCell(8).setCellValue(warranty.getContactInfo() != null ? warranty.getContactInfo() : "");
            }

            // 自动调整列宽
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("导出Excel失败: " + e.getMessage());
        }
    }

    /**
     * 从Excel导入保修合同数据
     * @param is Excel文件输入流
     * @return 保修合同列表
     */
    public static List<WarrantyContract> excelToWarranties(InputStream is) {
        try (Workbook workbook = WorkbookFactory.create(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            List<WarrantyContract> warranties = new ArrayList<>();

            // 跳过表头
            if (rows.hasNext()) {
                rows.next();
            }

            while (rows.hasNext()) {
                Row currentRow = rows.next();
                WarrantyContract warranty = new WarrantyContract();

                // 跳过空行
                if (currentRow.getCell(2) == null || currentRow.getCell(2).getStringCellValue().trim().isEmpty()) {
                    continue;
                }

                // 设置资产ID
                if (currentRow.getCell(1) != null) {
                    warranty.setAssetId((long) currentRow.getCell(1).getNumericCellValue());
                }

                // 设置合同编号
                warranty.setContractNo(currentRow.getCell(2).getStringCellValue());

                // 设置服务提供商
                warranty.setProvider(currentRow.getCell(3).getStringCellValue());

                // 设置开始日期
                String startDateStr = currentRow.getCell(4).getStringCellValue();
                warranty.setStartDate(LocalDate.parse(startDateStr, DATE_FORMATTER));

                // 设置结束日期
                String endDateStr = currentRow.getCell(5).getStringCellValue();
                warranty.setEndDate(LocalDate.parse(endDateStr, DATE_FORMATTER));

                // 设置描述（可选）
                if (currentRow.getCell(6) != null) {
                    warranty.setDescription(currentRow.getCell(6).getStringCellValue());
                }

                // 设置金额（可选）
                if (currentRow.getCell(7) != null) {
                    warranty.setContractCost(BigDecimal.valueOf(currentRow.getCell(7).getNumericCellValue()));
                }

                // 设置联系方式（可选）
                if (currentRow.getCell(8) != null) {
                    warranty.setContactInfo(currentRow.getCell(8).getStringCellValue());
                }

                warranties.add(warranty);
            }

            return warranties;
        } catch (IOException e) {
            throw new RuntimeException("解析Excel失败: " + e.getMessage());
        }
    }
}