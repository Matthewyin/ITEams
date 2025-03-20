package com.iteams.repository;

import com.iteams.model.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 权限仓库接口
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long>, JpaSpecificationExecutor<Permission> {
    
    /**
     * 根据权限编码查询权限
     *
     * @param permissionCode 权限编码
     * @return 权限
     */
    Optional<Permission> findByPermissionCode(String permissionCode);
    
    /**
     * 检查权限编码是否存在
     *
     * @param permissionCode 权限编码
     * @return 是否存在
     */
    boolean existsByPermissionCode(String permissionCode);
    
    /**
     * 根据父级ID查询权限列表
     *
     * @param parentId 父级ID
     * @return 权限列表
     */
    List<Permission> findByParentIdOrderBySortOrder(Long parentId);
    
    /**
     * 根据权限类型查询权限列表
     *
     * @param permissionType 权限类型
     * @return 权限列表
     */
    List<Permission> findByPermissionTypeOrderBySortOrder(String permissionType);
    
    /**
     * 根据角色ID查询权限列表
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    @Query("SELECT p FROM Permission p INNER JOIN RolePermission rp ON p.id = rp.permissionId WHERE rp.roleId = :roleId ORDER BY p.sortOrder")
    List<Permission> findByRoleId(Long roleId);
    
    /**
     * 根据用户ID查询权限列表
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    @Query("SELECT DISTINCT p FROM Permission p " +
           "INNER JOIN RolePermission rp ON p.id = rp.permissionId " +
           "INNER JOIN UserRole ur ON rp.roleId = ur.roleId " +
           "WHERE ur.userId = :userId AND p.status = 1 " +
           "ORDER BY p.sortOrder")
    List<Permission> findByUserId(Long userId);
    
    /**
     * 查询菜单类型的权限列表
     *
     * @return 菜单列表
     */
    @Query("SELECT p FROM Permission p WHERE p.permissionType = 'menu' AND p.status = 1 ORDER BY p.sortOrder")
    List<Permission> findMenus();
    
    /**
     * 根据用户ID查询菜单类型的权限列表
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    @Query("SELECT DISTINCT p FROM Permission p " +
           "INNER JOIN RolePermission rp ON p.id = rp.permissionId " +
           "INNER JOIN UserRole ur ON rp.roleId = ur.roleId " +
           "WHERE ur.userId = :userId AND p.permissionType = 'menu' AND p.status = 1 " +
           "ORDER BY p.sortOrder")
    List<Permission> findMenusByUserId(Long userId);
} 