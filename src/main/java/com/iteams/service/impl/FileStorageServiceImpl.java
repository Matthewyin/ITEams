package com.iteams.service.impl;

import com.iteams.service.FileStorageService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

/**
 * 文件存储服务实现类
 */
@Slf4j
@Service
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    private Path rootLocation;

    @PostConstruct
    @Override
    public void init() {
        try {
            rootLocation = Paths.get(uploadDir);
            Files.createDirectories(rootLocation);
            log.info("初始化文件存储目录: {}", rootLocation.toAbsolutePath());
        } catch (IOException e) {
            log.error("无法初始化文件存储目录: {}", e.getMessage(), e);
            throw new RuntimeException("无法初始化文件存储目录", e);
        }
    }

    @Override
    public String store(MultipartFile file, String subDirectory) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("无法存储空文件");
        }

        // 获取文件名
        String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        
        // 检查文件名是否包含无效字符
        if (originalFilename.contains("..")) {
            throw new IllegalArgumentException("文件名包含无效字符: " + originalFilename);
        }

        // 生成唯一文件名
        String fileExtension = getFileExtension(originalFilename);
        String newFilename = UUID.randomUUID() + fileExtension;
        
        // 创建子目录（如果需要）
        Path targetLocation;
        if (StringUtils.hasText(subDirectory)) {
            Path subDir = rootLocation.resolve(subDirectory);
            Files.createDirectories(subDir);
            targetLocation = subDir.resolve(newFilename);
        } else {
            targetLocation = rootLocation.resolve(newFilename);
        }

        // 存储文件
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        
        // 返回文件访问路径
        String filePath = (StringUtils.hasText(subDirectory)) 
                ? subDirectory + "/" + newFilename 
                : newFilename;
                
        log.info("文件存储成功: {}", filePath);
        return filePath;
    }

    @Override
    public Path load(String filename, String subDirectory) {
        if (StringUtils.hasText(subDirectory)) {
            return rootLocation.resolve(subDirectory).resolve(filename);
        }
        return rootLocation.resolve(filename);
    }

    @Override
    public boolean delete(String filename, String subDirectory) {
        try {
            Path file = load(filename, subDirectory);
            return Files.deleteIfExists(file);
        } catch (IOException e) {
            log.error("删除文件失败: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 获取文件扩展名
     *
     * @param filename 文件名
     * @return 文件扩展名（包含点号）
     */
    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex == -1) ? "" : filename.substring(dotIndex);
    }
}
