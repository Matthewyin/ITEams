package com.iteams.controller;

import com.iteams.common.ApiResponse;
import com.iteams.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;

    /**
     * 上传文件
     *
     * @param file 文件
     * @param directory 目录（可选）
     * @return 文件URL
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<Map<String, String>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "directory", required = false, defaultValue = "") String directory) {
        
        try {
            String filePath = fileStorageService.store(file, directory);
            Map<String, String> result = new HashMap<>();
            result.put("filePath", filePath);
            result.put("url", "/api/files/view/" + (directory.isEmpty() ? "" : directory + "/") + filePath);
            
            log.info("文件上传成功: {}", filePath);
            return ApiResponse.success(result);
        } catch (IOException e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            return ApiResponse.error("文件上传失败: " + e.getMessage());
        }
    }
}
