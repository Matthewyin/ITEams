package com.iteams.controller;

import com.iteams.model.dto.ImportProgressDTO;
import com.iteams.model.dto.ImportResultDTO;
import com.iteams.model.vo.ApiResponse;
import com.iteams.service.ImportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Excel资产导入控制器
 * <p>
 * 该控制器负责处理Excel资产数据导入的相关接口，主要功能包括：
 * <ul>
 *   <li>接收Excel文件上传并触发异步导入处理</li>
 *   <li>提供导入进度查询接口</li>
 *   <li>提供导入结果统计查询接口</li>
 * </ul>
 * 所有API采用RESTful设计风格，返回标准的ApiResponse格式响应。
 * 导入过程采用异步处理模式，避免长时间阻塞HTTP请求。
 * </p>
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/import")
public class ImportController {

    /**
     * 导入服务，处理Excel文件的实际导入逻辑
     */
    private final ImportService importService;

    /**
     * 上传Excel文件并触发异步导入处理
     * <p>
     * 接收前端上传的Excel文件，执行基础验证后，将文件交给ImportService
     * 进行异步处理。返回导入任务ID，前端可以通过该ID查询导入进度。
     * </p>
     * <p>
     * 验证内容包括：
     * <ul>
     *   <li>文件不能为空</li>
     *   <li>文件必须是Excel格式(.xlsx或.xls)</li>
     * </ul>
     * </p>
     * 
     * @param file 上传的Excel文件
     * @return 包含任务ID的API响应对象
     */
    @PostMapping(value = "/excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> uploadExcel(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ApiResponse.error("文件不能为空");
            }
            
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || (!originalFilename.endsWith(".xlsx") && !originalFilename.endsWith(".xls"))) {
                return ApiResponse.error("只支持Excel文件格式(.xlsx, .xls)");
            }
            
            // 获取CompletableFuture的结果
            String taskId = importService.importExcelAsync(file).get();
            
            // 确保返回的数据中包含taskId字段
            return ApiResponse.<String>success(taskId, "文件上传成功，开始处理");
            
        } catch (IOException e) {
            log.error("文件处理失败", e);
            return ApiResponse.error("文件处理失败：" + e.getMessage());
        } catch (InterruptedException | ExecutionException e) {
            log.error("异步任务执行失败", e);
            Thread.currentThread().interrupt(); // 重置中断状态
            return ApiResponse.error("异步处理失败：" + e.getMessage());
        } catch (Exception e) {
            // 捕获所有其他异常，确保返回一致的响应结构
            log.error("导入处理过程中发生未预期的错误", e);
            return ApiResponse.error("导入失败：" + e.getMessage());
        }
    }

    /**
     * 查询导入进度
     * <p>
     * 根据任务ID查询导入任务的实时进度信息，包括总行数、已处理行数、
     * 成功/失败数量、错误信息等。
     * </p>
     * <p>
     * 前端通常会定期轮询此接口，获取最新的进度信息并在UI上展示进度条和状态。
     * 当任务完成（成功或失败）时，前端可以根据响应中的状态停止轮询。
     * </p>
     * 
     * @param taskId 导入任务ID
     * @return 包含导入进度详情的API响应对象
     */
    @GetMapping("/progress/{taskId}")
    public ApiResponse<ImportProgressDTO> getImportProgress(@PathVariable String taskId) {
        ImportProgressDTO progress = importService.getImportProgress(taskId);
        if (progress == null) {
            return ApiResponse.error("任务不存在或已过期");
        }
        return ApiResponse.success(progress);
    }

    /**
     * 获取导入结果统计
     * <p>
     * 根据批次ID查询导入完成后的结果统计信息，包括总资产数、成功/失败数量、
     * 导入时间、耗时等。与进度查询不同，此接口返回的是导入完成后的最终统计数据。
     * </p>
     * <p>
     * 批次ID与任务ID不同：
     * <ul>
     *   <li>任务ID标识一次导入处理任务</li>
     *   <li>批次ID标识一批导入的资产记录</li>
     * </ul>
     * 通过批次ID可以查询到同一批次导入的所有资产记录。
     * </p>
     * 
     * @param batchId 导入批次ID
     * @return 包含导入结果统计的API响应对象
     */
    @GetMapping("/result/{batchId}")
    public ApiResponse<ImportResultDTO> getImportResult(@PathVariable String batchId) {
        ImportResultDTO result = importService.getImportResult(batchId);
        return ApiResponse.success(result);
    }
} 