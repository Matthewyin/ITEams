package com.iteams.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * UUID和业务编号生成工具类
 * <p>
 * 该工具类用于生成各种类型的业务标识符，包括：资产UUID、导入批次ID和资产编号。
 * 所有生成的标识符都包含时间元素，便于跟踪和分类，并采用规范的编码格式，
 * 确保在系统内的唯一性和可读性。
 * </p>
 * <p>
 * 该类中的所有方法都是静态的，可以直接通过类名调用，无需实例化。
 * 生成的标识符遵循预定义的格式规则，保证了系统中标识符的一致性。
 * </p>
 */
public class UuidGenerator {

    /**
     * 日期格式化器，格式为"yyyyMMdd"
     * 用于资产UUID生成时的日期部分
     */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    
    /**
     * 批次时间格式化器，格式为"yyyyMMddHHmmss"
     * 用于导入批次ID生成时的时间戳部分，精确到秒
     */
    private static final DateTimeFormatter BATCH_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    /**
     * 生成资产UUID
     * <p>
     * 格式：AST{日期}-{8位随机字符}
     * 例如：AST20240301-a1b2c3d4
     * </p>
     * <p>
     * 该UUID用于唯一标识系统中的每个资产，由以下部分组成：
     * <ul>
     *   <li>前缀"AST"表示资产(Asset)</li>
     *   <li>8位日期（年月日）</li>
     *   <li>连字符"-"</li>
     *   <li>8位随机字符（从UUID中截取）</li>
     * </ul>
     * </p>
     * 
     * @return 生成的资产UUID字符串
     */
    public static String generateAssetUuid() {
        String date = LocalDateTime.now().format(DATE_FORMATTER);
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        return String.format("AST%s-%s", date, uuid);
    }

    /**
     * 生成导入批次ID
     * <p>
     * 格式：IMPORT-{时间戳}-{6位随机字符}
     * 例如：IMPORT-20240301120530-a1b2c3
     * </p>
     * <p>
     * 该批次ID用于标识和跟踪一次Excel导入操作，由以下部分组成：
     * <ul>
     *   <li>前缀"IMPORT"表示导入批次</li>
     *   <li>14位时间戳（年月日时分秒）</li>
     *   <li>连字符"-"</li>
     *   <li>6位随机字符（从UUID中截取）</li>
     * </ul>
     * 批次ID可用于关联同一批次导入的所有资产记录，便于后续查询和操作。
     * </p>
     * 
     * @return 生成的导入批次ID字符串
     */
    public static String generateImportBatchId() {
        String timestamp = LocalDateTime.now().format(BATCH_FORMATTER);
        String uuid = UUID.randomUUID().toString().substring(0, 6);
        return String.format("IMPORT-%s-%s", timestamp, uuid);
    }
    
    /**
     * 生成资产编号
     * <p>
     * 格式：{分类码}{部门码}{年月}{4位序列号}
     * 例如：IT01HR24030001
     * </p>
     * <p>
     * 该编号是资产的业务编号，由以下部分组成：
     * <ul>
     *   <li>分类码：表示资产类别，通常2-4个字符</li>
     *   <li>部门码：表示资产所属部门，通常2个字符</li>
     *   <li>年月：当前年份的后两位和月份，共4位</li>
     *   <li>序列号：4位数字，从0001开始递增</li>
     * </ul>
     * 资产编号是日常业务中标识和检索资产的主要方式，具有一定的可读性和规律性。
     * </p>
     * 
     * @param categoryCode 资产分类代码（如IT01表示服务器）
     * @param departmentCode 部门代码（如HR表示人力资源部）
     * @param sequence 序列号，从1开始递增
     * @return 生成的资产编号字符串
     */
    public static String generateAssetNo(String categoryCode, String departmentCode, long sequence) {
        String yearMonth = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMM"));
        return String.format("%s%s%s%04d", categoryCode, departmentCode, yearMonth, sequence);
    }
} 