package com.iteams.model.dto;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * 导入任务进度DTO
 */
@Data
public class ImportProgressDTO {

    private String taskId;
    private String state;
    private double progress;
    private int totalRows;
    private int processedRows;
    private int successRows;
    private List<Integer> failedRows = new ArrayList<>();
    private List<String> errors = new ArrayList<>();
    private String error;
} 