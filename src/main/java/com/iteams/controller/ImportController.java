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

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/import")
public class ImportController {

    private final ImportService importService;

    /**
     * 上传Excel文件并异步处理
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
            return ApiResponse.success(taskId, "文件上传成功，开始处理");
            
        } catch (IOException e) {
            log.error("文件处理失败", e);
            return ApiResponse.error("文件处理失败：" + e.getMessage());
        } catch (InterruptedException | ExecutionException e) {
            log.error("异步任务执行失败", e);
            Thread.currentThread().interrupt(); // 重置中断状态
            return ApiResponse.error("异步处理失败：" + e.getMessage());
        }
    }

    /**
     * 查询导入进度
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
     * 获取导入结果
     */
    @GetMapping("/result/{batchId}")
    public ApiResponse<ImportResultDTO> getImportResult(@PathVariable String batchId) {
        ImportResultDTO result = importService.getImportResult(batchId);
        return ApiResponse.success(result);
    }
} 