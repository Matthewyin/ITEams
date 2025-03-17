package com.iteams.model.dto;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * 导入任务进度数据传输对象
 * <p>
 * 该DTO用于向前端传递Excel导入任务的实时进度信息，包括任务状态、进度百分比、
 * 成功/失败行数统计以及错误信息等。通过该对象，前端可以实时展示导入进度，
 * 并在导入过程中向用户提供反馈。
 * </p>
 */
@Data
public class ImportProgressDTO {

    /**
     * 任务ID，唯一标识一个导入任务
     * 用于前端轮询查询特定导入任务的进度
     */
    private String taskId;
    
    /**
     * 当前任务状态
     * 可能的值包括：PENDING（等待中）、PROCESSING（处理中）、COMPLETED（已完成）、FAILED（失败）
     */
    private String state;
    
    /**
     * 导入进度百分比，范围 0.0 - 1.0
     * 用于前端显示进度条
     */
    private double progress;
    
    /**
     * Excel文件总行数（不含表头）
     */
    private int totalRows;
    
    /**
     * 已处理的行数
     */
    private int processedRows;
    
    /**
     * 成功导入的行数
     */
    private int successRows;
    
    /**
     * 导入失败的行号列表
     * 记录哪些行导入失败，以便用户修正
     */
    private List<Integer> failedRows = new ArrayList<>();
    
    /**
     * 详细错误信息列表
     * 包含每个导入错误的具体描述
     */
    private List<String> errors = new ArrayList<>();
    
    /**
     * 任务级别的错误信息
     * 当整个导入任务失败时的错误描述
     */
    private String error;
} 