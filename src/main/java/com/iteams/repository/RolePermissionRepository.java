package com.iteams.repository;

import com.iteams.model.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 角色权限关联仓库接口
 */
@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, RolePermission.RolePermissionId> {
    
    /**
     * 根据角色ID查询角色权限关联列表
     *
     * @param roleId 角色ID
     * @return 角色权限关联列表
     */
    List<RolePermission> findByRoleId(Long roleId);
    
    /**
     * 根据权限ID查询角色权限关联列表
     *
     * @param permissionId 权限ID
     * @return 角色权限关联列表
     */
    List<RolePermission> findByPermissionId(Long permissionId);
    
    /**
     * 根据角色ID删除角色权限关联
     *
     * @param roleId 角色ID
     * @return 影响行数
     */
    @Modifying
    @Query("DELETE FROM RolePermission rp WHERE rp.roleId = :roleId")
    int deleteByRoleId(Long roleId);
    
    /**
     * 根据权限ID删除角色权限关联
     *
     * @param permissionId 权限ID
     * @return 影响行数
     */
    @Modifying
    @Query("DELETE FROM RolePermission rp WHERE rp.permissionId = :permissionId")
    int deleteByPermissionId(Long permissionId);
    
    /**
     * 检查角色是否拥有指定权限
     *
     * @param roleId 角色ID
     * @param permissionId 权限ID
     * @return 是否存在
     */
    boolean existsByRoleIdAndPermissionId(Long roleId, Long permissionId);
} 