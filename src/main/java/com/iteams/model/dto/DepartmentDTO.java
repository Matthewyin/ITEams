package com.iteams.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 部门数据传输对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentDTO {

    /**
     * 部门ID
     */
    private Long id;

    /**
     * 部门名称
     */
    private String name;

    /**
     * 部门代码
     */
    private String code;

    /**
     * 部门描述
     */
    private String description;

    /**
     * 父部门ID
     */
    private Long parentId;

    /**
     * 父部门名称（仅用于显示）
     */
    private String parentName;

    /**
     * 部门级别（1为顶级部门）
     */
    private Integer level;

    /**
     * 排序号
     */
    private Integer sortOrder;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 部门下的用户数量
     */
    private Integer userCount;
}
