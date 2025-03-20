package com.iteams.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

/**
 * 角色权限关联实体类
 */
@Data
@Entity
@Table(name = "sys_role_permission")
@IdClass(RolePermission.RolePermissionId.class)
public class RolePermission {
    
    /**
     * 角色ID
     */
    @Id
    @Column(name = "role_id")
    private Long roleId;
    
    /**
     * 权限ID
     */
    @Id
    @Column(name = "permission_id")
    private Long permissionId;
    
    /**
     * 复合主键类
     */
    @Data
    public static class RolePermissionId implements Serializable {
        private Long roleId;
        private Long permissionId;
    }
} 