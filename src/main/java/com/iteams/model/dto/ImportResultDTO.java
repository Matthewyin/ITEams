package com.iteams.model.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 导入结果统计DTO
 */
@Data
public class ImportResultDTO {

    private String batchId;
    private LocalDateTime importTime;
    private long totalAssets;
    private long successCount;
    private long failedCount;
    private String importUser;
    private double costTime;
} 