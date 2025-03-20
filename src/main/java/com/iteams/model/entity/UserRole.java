package com.iteams.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户角色关联实体类
 */
@Data
@Entity
@Table(name = "sys_user_role")
@IdClass(UserRole.UserRoleId.class)
public class UserRole {
    
    /**
     * 用户ID
     */
    @Id
    @Column(name = "user_id")
    private Long userId;
    
    /**
     * 角色ID
     */
    @Id
    @Column(name = "role_id")
    private Long roleId;
    
    /**
     * 复合主键类
     */
    @Data
    public static class UserRoleId implements Serializable {
        private Long userId;
        private Long roleId;
    }
} 