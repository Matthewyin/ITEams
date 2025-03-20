package com.iteams.service;

import com.iteams.model.dto.RoleDTO;
import com.iteams.model.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 角色服务接口
 */
public interface RoleService {
    
    /**
     * 获取所有角色
     *
     * @return 角色列表
     */
    List<RoleDTO> getAllRoles();
    
    /**
     * 获取除超级管理员外的所有角色
     *
     * @return 角色列表
     */
    List<RoleDTO> getAllRolesExceptSuperAdmin();
    
    /**
     * 分页查询角色
     *
     * @param query 查询条件
     * @param pageable 分页参数
     * @return 角色分页结果
     */
    Page<RoleDTO> getRoles(String query, Pageable pageable);
    
    /**
     * 根据ID获取角色
     *
     * @param id 角色ID
     * @return 角色信息
     */
    Optional<RoleDTO> getRoleById(Long id);
    
    /**
     * 根据角色编码获取角色
     *
     * @param roleCode 角色编码
     * @return 角色信息
     */
    Optional<RoleDTO> getRoleByCode(String roleCode);
    
    /**
     * 创建角色
     *
     * @param roleDTO 角色信息
     * @param currentUsername 当前用户名
     * @return 创建后的角色
     */
    RoleDTO createRole(RoleDTO roleDTO, String currentUsername);
    
    /**
     * 更新角色
     *
     * @param id 角色ID
     * @param roleDTO 角色信息
     * @param currentUsername 当前用户名
     * @return 更新后的角色
     */
    RoleDTO updateRole(Long id, RoleDTO roleDTO, String currentUsername);
    
    /**
     * 删除角色
     *
     * @param id 角色ID
     * @return 是否成功
     */
    boolean deleteRole(Long id);
    
    /**
     * 获取角色的权限ID列表
     *
     * @param roleId 角色ID
     * @return 权限ID列表
     */
    List<Long> getRolePermissions(Long roleId);
    
    /**
     * 分配角色权限
     *
     * @param roleId 角色ID
     * @param permissionIds 权限ID列表
     * @return 是否成功
     */
    boolean assignRolePermissions(Long roleId, List<Long> permissionIds);
    
    /**
     * 获取用户的角色
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    List<RoleDTO> getUserRoles(Long userId);
    
    /**
     * 分配用户角色
     *
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     * @return 是否成功
     */
    boolean assignUserRoles(Long userId, List<Long> roleIds);
} 