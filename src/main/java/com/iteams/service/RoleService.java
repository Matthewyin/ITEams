package com.iteams.service;

import com.iteams.model.dto.RoleDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 角色服务接口
 */
public interface RoleService {

    /**
     * 获取角色分页列表
     *
     * @param name     角色名称（模糊查询）
     * @param pageable 分页参数
     * @return 角色分页列表
     */
    Page<RoleDTO> getRoles(String name, Pageable pageable);

    /**
     * 获取所有角色（不分页）
     *
     * @return 角色列表
     */
    List<RoleDTO> getAllRoles();

    /**
     * 根据ID获取角色
     *
     * @param id 角色ID
     * @return 角色
     */
    RoleDTO getRoleById(Long id);

    /**
     * 创建角色
     *
     * @param roleDTO 角色数据
     * @return 创建后的角色
     */
    RoleDTO createRole(RoleDTO roleDTO);

    /**
     * 更新角色
     *
     * @param id      角色ID
     * @param roleDTO 角色数据
     * @return 更新后的角色
     */
    RoleDTO updateRole(Long id, RoleDTO roleDTO);

    /**
     * 删除角色
     *
     * @param id 角色ID
     */
    void deleteRole(Long id);

    /**
     * 获取角色的权限
     *
     * @param roleId 角色ID
     * @return 权限编码列表
     */
    List<String> getRolePermissions(Long roleId);

    /**
     * 分配角色权限
     *
     * @param roleId        角色ID
     * @param permissionIds 权限ID列表
     */
    void assignPermissions(Long roleId, List<Long> permissionIds);

    /**
     * 获取用户的角色
     *
     * @param userId 用户ID
     * @return 角色编码列表
     */
    List<String> getUserRoles(Long userId);
} 