package com.iteams.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

/**
 * 角色实体类
 */
@Data
@Entity
@Table(name = "sys_role")
@DynamicInsert
@DynamicUpdate
public class Role {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 角色编码
     */
    @Column(name = "role_code", nullable = false, length = 50, unique = true)
    private String roleCode;
    
    /**
     * 角色名称
     */
    @Column(name = "role_name", nullable = false, length = 100)
    private String roleName;
    
    /**
     * 角色描述
     */
    @Column(name = "description", length = 255)
    private String description;
    
    /**
     * 状态(1-启用, 0-禁用)
     */
    @Column(name = "status")
    private Integer status = 1;
    
    /**
     * 排序
     */
    @Column(name = "sort_order")
    private Integer sortOrder = 0;
    
    /**
     * 创建人
     */
    @Column(name = "created_by", length = 50)
    private String createdBy;
    
    /**
     * 创建时间
     */
    @Column(name = "created_time", nullable = false)
    private LocalDateTime createdTime;
    
    /**
     * 更新人
     */
    @Column(name = "updated_by", length = 50)
    private String updatedBy;
    
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