package com.iteams.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

/**
 * 文件存储服务接口
 */
public interface FileStorageService {

    /**
     * 初始化存储目录
     */
    void init();

    /**
     * 存储文件
     *
     * @param file 文件
     * @param subDirectory 子目录（可选）
     * @return 文件访问路径
     * @throws IOException IO异常
     */
    String store(MultipartFile file, String subDirectory) throws IOException;

    /**
     * 加载文件路径
     *
     * @param filename 文件名
     * @param subDirectory 子目录（可选）
     * @return 文件路径
     */
    Path load(String filename, String subDirectory);

    /**
     * 删除文件
     *
     * @param filename 文件名
     * @param subDirectory 子目录（可选）
     * @return 是否删除成功
     */
    boolean delete(String filename, String subDirectory);
}
