package com.iteams.model.dto;

import com.iteams.model.entity.Permission;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 权限DTO
 */
@Data
public class PermissionDTO {
    
    /**
     * 权限ID
     */
    private Long id;
    
    /**
     * 父级ID
     */
    private Long parentId;
    
    /**
     * 权限编码
     */
    private String permissionCode;
    
    /**
     * 权限名称
     */
    private String permissionName;
    
    /**
     * 权限类型(menu-菜单, button-按钮, api-接口)
     */
    private String permissionType;
    
    /**
     * 路径
     */
    private String path;
    
    /**
     * 组件
     */
    private String component;
    
    /**
     * 图标
     */
    private String icon;
    
    /**
     * 排序
     */
    private Integer sortOrder;
    
    /**
     * 状态(1-启用, 0-禁用)
     */
    private Integer status;
    
    /**
     * 是否可见(1-可见, 0-隐藏)
     */
    private Integer visible;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;
    
    /**
     * 子权限列表
     */
    private List<PermissionDTO> children = new ArrayList<>();
    
    /**
     * 是否为叶子节点(1-是, 0-否)
     */
    private Integer isLeaf = 1;
    
    /**
     * 从实体转换为DTO
     *
     * @param permission 权限实体
     * @return 权限DTO
     */
    public static PermissionDTO fromEntity(Permission permission) {
        if (permission == null) {
            return null;
        }
        
        PermissionDTO dto = new PermissionDTO();
        dto.setId(permission.getId());
        dto.setParentId(permission.getParentId());
        dto.setPermissionCode(permission.getPermissionCode());
        dto.setPermissionName(permission.getPermissionName());
        dto.setPermissionType(permission.getPermissionType());
        dto.setPath(permission.getPath());
        dto.setComponent(permission.getComponent());
        dto.setIcon(permission.getIcon());
        dto.setSortOrder(permission.getSortOrder());
        dto.setStatus(permission.getStatus());
        dto.setVisible(permission.getVisible());
        dto.setCreatedTime(permission.getCreatedTime());
        dto.setUpdatedTime(permission.getUpdatedTime());
        
        return dto;
    }
    
    /**
     * 转换为实体
     *
     * @return 权限实体
     */
    public Permission toEntity() {
        Permission permission = new Permission();
        permission.setId(this.id);
        permission.setParentId(this.parentId);
        permission.setPermissionCode(this.permissionCode);
        permission.setPermissionName(this.permissionName);
        permission.setPermissionType(this.permissionType);
        permission.setPath(this.path);
        permission.setComponent(this.component);
        permission.setIcon(this.icon);
        permission.setSortOrder(this.sortOrder);
        permission.setStatus(this.status);
        permission.setVisible(this.visible);
        permission.setCreatedTime(this.createdTime);
        permission.setUpdatedTime(this.updatedTime);
        
        return permission;
    }
    
    /**
     * 构建树形结构
     *
     * @param permissionList 权限列表
     * @return 树形结构
     */
    public static List<PermissionDTO> buildTree(List<PermissionDTO> permissionList) {
        List<PermissionDTO> tree = new ArrayList<>();
        
        // 先找出所有的根节点
        for (PermissionDTO permission : permissionList) {
            if (permission.getParentId() == 0L) {
                tree.add(permission);
            }
        }
        
        // 为根节点设置子节点
        for (PermissionDTO parent : tree) {
            recursiveBuildChildren(parent, permissionList);
        }
        
        return tree;
    }
    
    /**
     * 递归构建子节点
     *
     * @param parent 父节点
     * @param permissionList 权限列表
     */
    private static void recursiveBuildChildren(PermissionDTO parent, List<PermissionDTO> permissionList) {
        for (PermissionDTO permission : permissionList) {
            if (permission.getParentId().equals(parent.getId())) {
                parent.getChildren().add(permission);
                parent.setIsLeaf(0);  // 非叶子节点
                recursiveBuildChildren(permission, permissionList);
            }
        }
    }
} 