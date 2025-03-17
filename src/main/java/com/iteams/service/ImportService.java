package com.iteams.service;

import com.iteams.model.dto.ImportProgressDTO;
import com.iteams.model.dto.ImportResultDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * Excel资产导入服务接口
 * <p>
 * 该服务负责处理IT资产Excel数据批量导入功能，支持异步处理大规模数据导入，
 * 提供实时进度跟踪和结果统计能力。导入过程包括文件解析、数据验证、去重检查、
 * 数据转换和入库等多个环节。
 * </p>
 * <p>
 * 核心功能包括：
 * <ul>
 *   <li>Excel文件异步解析与处理</li>
 *   <li>数据验证与错误收集</li>
 *   <li>导入进度实时跟踪</li>
 *   <li>导入结果统计与查询</li>
 *   <li>重复数据检测与处理</li>
 * </ul>
 * </p>
 * <p>
 * 性能特性：
 * <ul>
 *   <li>支持后台异步处理，不阻塞用户操作</li>
 *   <li>采用批量处理策略，提高大数据量导入效率</li>
 *   <li>实现多级缓存，减少数据库查询压力</li>
 *   <li>可通过任务ID查询进度，支持前端轮询</li>
 * </ul>
 * </p>
 */
public interface ImportService {

    /**
     * 异步导入Excel资产数据文件
     * <p>
     * 该方法接收上传的Excel文件，创建异步任务进行处理，立即返回任务ID，
     * 不会阻塞调用线程。整个导入过程在后台线程池中执行，包括以下步骤：
     * <ol>
     *   <li>验证Excel文件格式和表头</li>
     *   <li>逐行解析Excel数据</li>
     *   <li>验证数据有效性和完整性</li>
     *   <li>检查重复数据（基于资产编号和Excel行哈希）</li>
     *   <li>转换数据为实体对象</li>
     *   <li>批量保存到数据库</li>
     *   <li>更新导入进度和状态</li>
     * </ol>
     * </p>
     * <p>
     * 该方法采用线程池管理，支持并发导入多个文件，每个导入任务会分配唯一的
     * 任务ID和批次ID，便于后续跟踪和查询。
     * </p>
     * 
     * @param file Excel文件，支持.xlsx格式，最大10MB
     * @return 包含导入任务ID的CompletableFuture，可用于跟踪任务完成状态
     * @throws IOException 文件读取或解析异常
     * @throws IllegalArgumentException 如果文件为null或格式不正确
     */
    CompletableFuture<String> importExcelAsync(MultipartFile file) throws IOException;

    /**
     * 获取资产导入任务的实时进度
     * <p>
     * 根据任务ID查询当前导入任务的执行状态和进度详情，返回结构化的进度数据，
     * 包括处理状态（处理中/已完成/失败）、百分比进度、已处理行数、成功行数、
     * 失败行数及错误明细等。
     * </p>
     * <p>
     * 该方法通常由前端轮询调用，以展示实时进度条和状态信息。
     * 返回的DTO包含以下核心信息：
     * <ul>
     *   <li>taskId: 任务唯一标识</li>
     *   <li>state: 当前状态（PENDING/PROCESSING/COMPLETED/FAILED）</li>
     *   <li>progress: 完成百分比（0.0-1.0）</li>
     *   <li>totalRows: 总行数</li>
     *   <li>processedRows: 已处理行数</li>
     *   <li>successRows: 成功行数</li>
     *   <li>failedRows: 失败行号列表</li>
     *   <li>errors: 错误明细列表</li>
     * </ul>
     * </p>
     * 
     * @param taskId 导入任务ID，由importExcelAsync方法返回
     * @return 导入进度数据传输对象
     * @throws IllegalArgumentException 如果taskId为null或不存在
     */
    ImportProgressDTO getImportProgress(String taskId);

    /**
     * 获取已完成导入任务的结果统计
     * <p>
     * 根据批次ID查询已完成导入任务的最终结果和统计数据，包括成功导入的资产数量、
     * 失败数量、导入耗时、操作用户等信息。此方法通常在导入任务完成后调用，
     * 用于展示导入结果摘要。
     * </p>
     * <p>
     * 注意：批次ID与任务ID不同，一个批次ID对应导入到数据库的一批资产记录，
     * 可用于后续查询同批次导入的所有资产。批次ID格式如：IMPORT-20240301120530-a1b2c3
     * </p>
     * <p>
     * 返回的DTO包含以下核心信息：
     * <ul>
     *   <li>batchId: 导入批次ID</li>
     *   <li>importTime: 导入完成时间</li>
     *   <li>totalAssets: 总资产数</li>
     *   <li>successCount: 成功导入数</li>
     *   <li>failedCount: 失败数</li>
     *   <li>importUser: 导入用户</li>
     *   <li>costTime: 耗时（秒）</li>
     * </ul>
     * </p>
     * 
     * @param batchId 导入批次ID，格式如IMPORT-20240301120530-a1b2c3
     * @return 导入结果统计数据传输对象
     * @throws IllegalArgumentException 如果batchId为null或不存在
     */
    ImportResultDTO getImportResult(String batchId);
} 