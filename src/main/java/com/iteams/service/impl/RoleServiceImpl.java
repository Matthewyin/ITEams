package com.iteams.service.impl;

import com.iteams.exception.ResourceNotFoundException;
import com.iteams.model.dto.RoleDTO;
import com.iteams.model.entity.Permission;
import com.iteams.model.entity.Role;
import com.iteams.model.entity.User;
import com.iteams.repository.PermissionRepository;
import com.iteams.repository.RoleRepository;
import com.iteams.repository.UserRepository;
import com.iteams.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 角色服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;

    /**
     * 获取角色分页列表
     */
    @Override
    public Page<RoleDTO> getRoles(String name, Pageable pageable) {
        Page<Role> rolePage = roleRepository.findByConditions(name, pageable);
        return rolePage.map(this::convertToDto);
    }

    /**
     * 获取所有角色（不分页）
     */
    @Override
    public List<RoleDTO> getAllRoles() {
        List<Role> roles = roleRepository.findAll();
        return roles.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    /**
     * 根据ID获取角色
     */
    @Override
    public RoleDTO getRoleById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("角色不存在: " + id));
        return convertToDto(role);
    }

    /**
     * 创建角色
     */
    @Override
    @Transactional
    public RoleDTO createRole(RoleDTO roleDTO) {
        // 检查角色编码是否存在
        if (roleRepository.existsByCode(roleDTO.getCode())) {
            throw new IllegalArgumentException("角色编码已存在: " + roleDTO.getCode());
        }

        // 创建角色
        Role role = new Role();
        role.setName(roleDTO.getName());
        role.setCode(roleDTO.getCode());
        role.setDescription(roleDTO.getDescription());
        role.setCreatedAt(LocalDateTime.now());

        // 保存角色
        Role savedRole = roleRepository.save(role);
        return convertToDto(savedRole);
    }

    /**
     * 更新角色
     */
    @Override
    @Transactional
    public RoleDTO updateRole(Long id, RoleDTO roleDTO) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("角色不存在: " + id));

        // 检查角色编码是否重复
        if (!role.getCode().equals(roleDTO.getCode()) && roleRepository.existsByCode(roleDTO.getCode())) {
            throw new IllegalArgumentException("角色编码已存在: " + roleDTO.getCode());
        }

        // 更新角色
        role.setName(roleDTO.getName());
        role.setCode(roleDTO.getCode());
        role.setDescription(roleDTO.getDescription());
        role.setUpdatedAt(LocalDateTime.now());

        // 保存角色
        Role savedRole = roleRepository.save(role);
        return convertToDto(savedRole);
    }

    /**
     * 删除角色
     */
    @Override
    @Transactional
    public void deleteRole(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("角色不存在: " + id));

        // 检查是否是系统内置角色
        if (isSystemRole(role.getCode())) {
            throw new IllegalArgumentException("不能删除系统内置角色: " + role.getName());
        }

        // 删除角色
        roleRepository.delete(role);
    }

    /**
     * 获取角色的权限
     */
    @Override
    public List<String> getRolePermissions(Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("角色不存在: " + roleId));

        return role.getPermissions().stream()
                .map(Permission::getCode)
                .collect(Collectors.toList());
    }

    /**
     * 分配角色权限
     */
    @Override
    @Transactional
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("角色不存在: " + roleId));

        // 获取权限列表
        List<Permission> permissions = permissionRepository.findAllById(permissionIds);
        if (permissions.isEmpty() && !permissionIds.isEmpty()) {
            throw new ResourceNotFoundException("未找到指定权限");
        }

        // 设置角色权限
        role.setPermissions(new HashSet<>(permissions));
        roleRepository.save(role);
    }

    /**
     * 获取用户的角色
     */
    @Override
    public List<String> getUserRoles(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在: " + userId));

        return user.getRoles().stream()
                .map(Role::getCode)
                .collect(Collectors.toList());
    }

    /**
     * 转换角色实体为DTO
     */
    private RoleDTO convertToDto(Role role) {
        RoleDTO dto = new RoleDTO();
        dto.setId(role.getId());
        dto.setName(role.getName());
        dto.setCode(role.getCode());
        dto.setDescription(role.getDescription());

        // 设置权限
        Set<String> permissions = role.getPermissions().stream()
                .map(Permission::getCode)
                .collect(Collectors.toSet());
        dto.setPermissions(permissions);

        return dto;
    }

    /**
     * 检查是否是系统内置角色
     */
    private boolean isSystemRole(String roleCode) {
        return List.of("SUPER_ADMIN", "ADMIN", "USER").contains(roleCode);
    }
} 