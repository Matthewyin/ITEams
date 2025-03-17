package com.iteams.util.excel;

import java.util.Map;

/**
 * Excel行处理器接口
 * <p>
 * 该接口定义了处理Excel文件中每一行数据的标准方法。通过实现此接口，
 * 可以自定义针对Excel中不同行的处理逻辑，使得Excel解析过程更加灵活。
 * </p>
 * <p>
 * 在资产导入系统中，此接口通常由具体的业务处理类实现，以将Excel行数据
 * 转换为资产对象并保存到数据库。接口设计采用回调模式，ExcelParser负责
 * 提取数据，而实现此接口的类负责处理提取的数据。
 * </p>
 * <p>
 * 常见的实现场景包括：
 * <ul>
 *   <li>将Excel行数据转换为实体对象</li>
 *   <li>对行数据进行业务验证</li>
 *   <li>将验证通过的数据写入数据库</li>
 *   <li>记录处理失败的行和原因</li>
 * </ul>
 * </p>
 */
public interface RowProcessor {

    /**
     * 处理Excel中的一行数据
     * <p>
     * 该方法在Excel解析器读取每一行数据后被调用，传入该行的所有单元格数据。
     * 数据以Map形式提供，其中键为列名（通常是表头名称），值为单元格内容。
     * 不同类型的单元格会被解析为相应的Java类型（如数字、日期、字符串等）。
     * </p>
     * <p>
     * 实现者负责：
     * <ul>
     *   <li>从Map中提取所需字段</li>
     *   <li>转换数据类型（如需要）</li>
     *   <li>验证数据的有效性</li>
     *   <li>执行业务处理逻辑</li>
     *   <li>返回处理结果状态</li>
     * </ul>
     * </p>
     * 
     * @param rowData 行数据，键为列名，值为单元格值
     *                可能的值类型包括：String、Double、Boolean、Date等
     * @param rowIndex 行索引，从0开始（不包括表头行）
     *                 可用于错误报告或日志记录
     * @return 处理成功返回true，失败返回false
     *         返回false将导致该行被记录为处理失败
     */
    boolean processRow(Map<String, Object> rowData, int rowIndex);
} 