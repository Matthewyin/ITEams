package com.iteams.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * UUID和业务编号生成工具
 */
public class UuidGenerator {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter BATCH_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    /**
     * 生成资产UUID：格式 AST20240101-{uuid}
     */
    public static String generateAssetUuid() {
        String date = LocalDateTime.now().format(DATE_FORMATTER);
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        return String.format("AST%s-%s", date, uuid);
    }

    /**
     * 生成导入批次ID：格式 IMPORT-20240101123030-{uuid}
     */
    public static String generateImportBatchId() {
        String timestamp = LocalDateTime.now().format(BATCH_FORMATTER);
        String uuid = UUID.randomUUID().toString().substring(0, 6);
        return String.format("IMPORT-%s-%s", timestamp, uuid);
    }
    
    /**
     * 生成资产编号：格式 分类码+部门码+年月+序列号
     */
    public static String generateAssetNo(String categoryCode, String departmentCode, long sequence) {
        String yearMonth = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMM"));
        return String.format("%s%s%s%04d", categoryCode, departmentCode, yearMonth, sequence);
    }
} 