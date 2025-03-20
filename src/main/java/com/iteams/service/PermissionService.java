package com.iteams.service;

import com.iteams.model.dto.PermissionDTO;
import com.iteams.model.entity.Permission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 权限服务接口
 */
public interface PermissionService {
    
    /**
     * 获取所有权限
     *
     * @return 权限列表
     */
    List<PermissionDTO> getAllPermissions();
    
    /**
     * 获取权限树
     *
     * @return 权限树
     */
    List<PermissionDTO> getPermissionTree();
    
    /**
     * 分页查询权限
     *
     * @param query 查询条件
     * @param pageable 分页参数
     * @return 权限分页结果
     */
    Page<PermissionDTO> getPermissions(String query, Pageable pageable);
    
    /**
     * 根据ID获取权限
     *
     * @param id 权限ID
     * @return 权限信息
     */
    Optional<PermissionDTO> getPermissionById(Long id);
    
    /**
     * 根据权限编码获取权限
     *
     * @param permissionCode 权限编码
     * @return 权限信息
     */
    Optional<PermissionDTO> getPermissionByCode(String permissionCode);
    
    /**
     * 创建权限
     *
     * @param permissionDTO 权限信息
     * @return 创建后的权限
     */
    PermissionDTO createPermission(PermissionDTO permissionDTO);
    
    /**
     * 更新权限
     *
     * @param id 权限ID
     * @param permissionDTO 权限信息
     * @return 更新后的权限
     */
    PermissionDTO updatePermission(Long id, PermissionDTO permissionDTO);
    
    /**
     * 删除权限
     *
     * @param id 权限ID
     * @return 是否成功
     */
    boolean deletePermission(Long id);
    
    /**
     * 获取用户的所有权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    List<PermissionDTO> getUserPermissions(Long userId);
    
    /**
     * 获取用户的菜单权限
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    List<PermissionDTO> getUserMenus(Long userId);
    
    /**
     * 获取角色的所有权限
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    List<PermissionDTO> getRolePermissions(Long roleId);
} 