package com.iteams.controller;

import com.iteams.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件视图控制器
 */
@Slf4j
@Controller
@RequestMapping("/api/files/view")
@RequiredArgsConstructor
public class FileViewController {

    private final FileStorageService fileStorageService;
    
    // 文件类型映射
    private static final Map<String, String> CONTENT_TYPES = new HashMap<>();
    
    static {
        // 图片类型
        CONTENT_TYPES.put("jpg", "image/jpeg");
        CONTENT_TYPES.put("jpeg", "image/jpeg");
        CONTENT_TYPES.put("png", "image/png");
        CONTENT_TYPES.put("gif", "image/gif");
        CONTENT_TYPES.put("webp", "image/webp");
        CONTENT_TYPES.put("svg", "image/svg+xml");
        
        // 文档类型
        CONTENT_TYPES.put("pdf", "application/pdf");
        CONTENT_TYPES.put("doc", "application/msword");
        CONTENT_TYPES.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        CONTENT_TYPES.put("xls", "application/vnd.ms-excel");
        CONTENT_TYPES.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        CONTENT_TYPES.put("ppt", "application/vnd.ms-powerpoint");
        CONTENT_TYPES.put("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
        
        // 文本类型
        CONTENT_TYPES.put("txt", "text/plain");
        CONTENT_TYPES.put("html", "text/html");
        CONTENT_TYPES.put("css", "text/css");
        CONTENT_TYPES.put("js", "application/javascript");
        CONTENT_TYPES.put("json", "application/json");
        CONTENT_TYPES.put("xml", "application/xml");
    }

    /**
     * 查看文件
     *
     * @param filename 文件名
     * @return 文件资源
     */
    @GetMapping("/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> viewFile(@PathVariable String filename) {
        return serveFile(filename, "");
    }

    /**
     * 查看子目录中的文件
     *
     * @param directory 子目录
     * @param filename  文件名
     * @return 文件资源
     */
    @GetMapping("/{directory}/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> viewFileInDirectory(
            @PathVariable String directory,
            @PathVariable String filename) {
        return serveFile(filename, directory);
    }

    /**
     * 提供文件资源
     *
     * @param filename     文件名
     * @param subDirectory 子目录
     * @return 文件资源响应
     */
    private ResponseEntity<Resource> serveFile(String filename, String subDirectory) {
        try {
            Path filePath = fileStorageService.load(filename, subDirectory);
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                // 获取文件扩展名
                String extension = getFileExtension(filename);
                // 获取Content-Type
                String contentType = CONTENT_TYPES.getOrDefault(extension, "application/octet-stream");
                
                log.info("提供文件: {}, 类型: {}", filename, contentType);
                
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                log.warn("文件不存在或不可读: {}", filename);
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            log.error("文件URL格式错误: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 获取文件扩展名
     *
     * @param filename 文件名
     * @return 文件扩展名（不包含点号）
     */
    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex == -1) ? "" : filename.substring(dotIndex + 1).toLowerCase();
    }
}
