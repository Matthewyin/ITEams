package com.iteams.model.dto;

import com.iteams.model.entity.Role;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色DTO
 */
@Data
public class RoleDTO {
    
    /**
     * 角色ID
     */
    private Long id;
    
    /**
     * 角色编码
     */
    private String roleCode;
    
    /**
     * 角色名称
     */
    private String roleName;
    
    /**
     * 角色描述
     */
    private String description;
    
    /**
     * 状态(1-启用, 0-禁用)
     */
    private Integer status;
    
    /**
     * 排序
     */
    private Integer sortOrder;
    
    /**
     * 创建人
     */
    private String createdBy;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
    
    /**
     * 更新人
     */
    private String updatedBy;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;
    
    /**
     * 权限ID列表
     */
    private List<Long> permissionIds;
    
    /**
     * 是否能删除（1-是，0-否）
     */
    private Integer canDelete = 1;
    
    /**
     * 用户数量
     */
    private Long userCount = 0L;
    
    /**
     * 从实体转换为DTO
     *
     * @param role 角色实体
     * @return 角色DTO
     */
    public static RoleDTO fromEntity(Role role) {
        if (role == null) {
            return null;
        }
        
        RoleDTO dto = new RoleDTO();
        dto.setId(role.getId());
        dto.setRoleCode(role.getRoleCode());
        dto.setRoleName(role.getRoleName());
        dto.setDescription(role.getDescription());
        dto.setStatus(role.getStatus());
        dto.setSortOrder(role.getSortOrder());
        dto.setCreatedBy(role.getCreatedBy());
        dto.setCreatedTime(role.getCreatedTime());
        dto.setUpdatedBy(role.getUpdatedBy());
        dto.setUpdatedTime(role.getUpdatedTime());
        
        // 超级管理员不能删除
        if ("superadmin".equals(role.getRoleCode())) {
            dto.setCanDelete(0);
        }
        
        return dto;
    }
    
    /**
     * 转换为实体
     *
     * @return 角色实体
     */
    public Role toEntity() {
        Role role = new Role();
        role.setId(this.id);
        role.setRoleCode(this.roleCode);
        role.setRoleName(this.roleName);
        role.setDescription(this.description);
        role.setStatus(this.status);
        role.setSortOrder(this.sortOrder);
        role.setCreatedBy(this.createdBy);
        role.setCreatedTime(this.createdTime);
        role.setUpdatedBy(this.updatedBy);
        role.setUpdatedTime(this.updatedTime);
        return role;
    }
} 