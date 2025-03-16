package com.iteams.util.excel;

import java.util.Map;

/**
 * Excel行处理器接口，用于处理Excel文件中的每一行数据
 */
public interface RowProcessor {

    /**
     * 处理Excel中的一行数据
     * @param rowData 行数据，键为列名，值为单元格值
     * @param rowIndex 行索引，从0开始
     * @return 处理成功返回true，失败返回false
     */
    boolean processRow(Map<String, Object> rowData, int rowIndex);
} 