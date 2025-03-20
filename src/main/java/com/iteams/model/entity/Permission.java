package com.iteams.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

/**
 * 权限实体类
 */
@Data
@Entity
@Table(name = "sys_permission")
@DynamicInsert
@DynamicUpdate
public class Permission {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 父级ID
     */
    @Column(name = "parent_id")
    private Long parentId = 0L;
    
    /**
     * 权限编码
     */
    @Column(name = "permission_code", nullable = false, length = 100, unique = true)
    private String permissionCode;
    
    /**
     * 权限名称
     */
    @Column(name = "permission_name", nullable = false, length = 100)
    private String permissionName;
    
    /**
     * 权限类型(menu-菜单, button-按钮, api-接口)
     */
    @Column(name = "permission_type", nullable = false, length = 20)
    private String permissionType;
    
    /**
     * 路径
     */
    @Column(name = "path", length = 255)
    private String path;
    
    /**
     * 组件
     */
    @Column(name = "component", length = 255)
    private String component;
    
    /**
     * 图标
     */
    @Column(name = "icon", length = 100)
    private String icon;
    
    /**
     * 排序
     */
    @Column(name = "sort_order")
    private Integer sortOrder = 0;
    
    /**
     * 状态(1-启用, 0-禁用)
     */
    @Column(name = "status")
    private Integer status = 1;
    
    /**
     * 是否可见(1-可见, 0-隐藏)
     */
    @Column(name = "visible")
    private Integer visible = 1;
    
    /**
     * 创建时间
     */
    @Column(name = "created_time", nullable = false)
    private LocalDateTime createdTime;
    
    /**
     * 更新时间
     */
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;
    
    @PrePersist
    public void prePersist() {
        if (createdTime == null) {
            createdTime = LocalDateTime.now();
        }
    }
    
    @PreUpdate
    public void preUpdate() {
        updatedTime = LocalDateTime.now();
    }
} 