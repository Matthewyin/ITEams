package com.iteams.service;

import com.iteams.model.dto.ImportProgressDTO;
import com.iteams.model.dto.ImportResultDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * Excel导入服务接口
 */
public interface ImportService {

    /**
     * 异步导入Excel文件，处理进度跟踪
     * @param file Excel文件
     * @return 包含导入任务ID的CompletableFuture
     * @throws IOException 文件处理异常
     */
    CompletableFuture<String> importExcelAsync(MultipartFile file) throws IOException;

    /**
     * 获取导入进度
     * @param taskId 任务ID
     * @return 导入进度DTO
     */
    ImportProgressDTO getImportProgress(String taskId);

    /**
     * 获取导入结果统计
     * @param batchId 批次ID
     * @return 导入结果DTO
     */
    ImportResultDTO getImportResult(String batchId);
} 