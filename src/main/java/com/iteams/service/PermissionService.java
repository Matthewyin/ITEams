package com.iteams.service;

import com.iteams.model.dto.PermissionDTO;

import java.util.List;
import java.util.Set;

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
     * 根据ID获取权限
     *
     * @param id 权限ID
     * @return 权限
     */
    PermissionDTO getPermissionById(Long id);

    /**
     * 根据编码获取权限
     *
     * @param code 权限编码
     * @return 权限
     */
    PermissionDTO getPermissionByCode(String code);

    /**
     * 创建权限
     *
     * @param permissionDTO 权限数据
     * @return 创建后的权限
     */
    PermissionDTO createPermission(PermissionDTO permissionDTO);

    /**
     * 更新权限
     *
     * @param id            权限ID
     * @param permissionDTO 权限数据
     * @return 更新后的权限
     */
    PermissionDTO updatePermission(Long id, PermissionDTO permissionDTO);

    /**
     * 删除权限
     *
     * @param id 权限ID
     */
    void deletePermission(Long id);

    /**
     * 获取角色的权限
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    List<PermissionDTO> getRolePermissions(Long roleId);

    /**
     * 获取用户的权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    List<PermissionDTO> getUserPermissions(Long userId);

    /**
     * 获取用户的权限编码
     *
     * @param userId 用户ID
     * @return 权限编码列表
     */
    Set<String> getUserPermissionCodes(Long userId);
} 