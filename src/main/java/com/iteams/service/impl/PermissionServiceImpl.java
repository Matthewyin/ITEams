package com.iteams.service.impl;

import com.iteams.exception.ResourceNotFoundException;
import com.iteams.model.dto.PermissionDTO;
import com.iteams.model.entity.Permission;
import com.iteams.model.entity.Role;
import com.iteams.model.entity.User;
import com.iteams.repository.PermissionRepository;
import com.iteams.repository.RoleRepository;
import com.iteams.repository.UserRepository;
import com.iteams.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 权限服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    /**
     * 获取所有权限
     */
    @Override
    public List<PermissionDTO> getAllPermissions() {
        List<Permission> permissions = permissionRepository.findAllOrderByCode();
        return permissions.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    /**
     * 根据ID获取权限
     */
    @Override
    public PermissionDTO getPermissionById(Long id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("权限不存在: " + id));
        return convertToDto(permission);
    }

    /**
     * 根据编码获取权限
     */
    @Override
    public PermissionDTO getPermissionByCode(String code) {
        Permission permission = permissionRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("权限不存在: " + code));
        return convertToDto(permission);
    }

    /**
     * 创建权限
     */
    @Override
    @Transactional
    public PermissionDTO createPermission(PermissionDTO permissionDTO) {
        // 检查权限编码是否存在
        if (permissionRepository.existsByCode(permissionDTO.getCode())) {
            throw new IllegalArgumentException("权限编码已存在: " + permissionDTO.getCode());
        }

        // 创建权限
        Permission permission = new Permission();
        permission.setName(permissionDTO.getName());
        permission.setCode(permissionDTO.getCode());
        permission.setDescription(permissionDTO.getDescription());
        permission.setType(permissionDTO.getType());
        permission.setCreatedAt(LocalDateTime.now());

        // 保存权限
        Permission savedPermission = permissionRepository.save(permission);
        return convertToDto(savedPermission);
    }

    /**
     * 更新权限
     */
    @Override
    @Transactional
    public PermissionDTO updatePermission(Long id, PermissionDTO permissionDTO) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("权限不存在: " + id));

        // 检查权限编码是否重复
        if (!permission.getCode().equals(permissionDTO.getCode()) 
                && permissionRepository.existsByCode(permissionDTO.getCode())) {
            throw new IllegalArgumentException("权限编码已存在: " + permissionDTO.getCode());
        }

        // 更新权限
        permission.setName(permissionDTO.getName());
        permission.setCode(permissionDTO.getCode());
        permission.setDescription(permissionDTO.getDescription());
        permission.setType(permissionDTO.getType());
        permission.setUpdatedAt(LocalDateTime.now());

        // 保存权限
        Permission savedPermission = permissionRepository.save(permission);
        return convertToDto(savedPermission);
    }

    /**
     * 删除权限
     */
    @Override
    @Transactional
    public void deletePermission(Long id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("权限不存在: " + id));

        // 检查是否是系统内置权限
        if (isSystemPermission(permission.getCode())) {
            throw new IllegalArgumentException("不能删除系统内置权限: " + permission.getName());
        }

        // 删除权限
        permissionRepository.delete(permission);
    }

    /**
     * 获取角色的权限
     */
    @Override
    public List<PermissionDTO> getRolePermissions(Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("角色不存在: " + roleId));

        return role.getPermissions().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 获取用户的权限
     */
    @Override
    public List<PermissionDTO> getUserPermissions(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在: " + userId));

        // 获取用户所有角色的权限
        Set<Permission> permissions = new HashSet<>();
        user.getRoles().forEach(role -> permissions.addAll(role.getPermissions()));

        return permissions.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 获取用户的权限编码
     */
    @Override
    public Set<String> getUserPermissionCodes(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在: " + userId));

        // 获取用户所有角色的权限
        Set<String> permissionCodes = new HashSet<>();
        user.getRoles().forEach(role -> 
            role.getPermissions().forEach(permission -> 
                permissionCodes.add(permission.getCode())
            )
        );

        return permissionCodes;
    }

    /**
     * 转换权限实体为DTO
     */
    private PermissionDTO convertToDto(Permission permission) {
        return PermissionDTO.builder()
                .id(permission.getId())
                .name(permission.getName())
                .code(permission.getCode())
                .description(permission.getDescription())
                .type(permission.getType())
                .build();
    }

    /**
     * 检查是否是系统内置权限
     */
    private boolean isSystemPermission(String code) {
        // 定义系统基础权限（不允许删除）
        List<String> systemPermissions = List.of(
                "USER_VIEW", "USER_CREATE", "USER_EDIT", "USER_DELETE",
                "ROLE_VIEW", "ROLE_CREATE", "ROLE_EDIT", "ROLE_DELETE",
                "PERMISSION_VIEW", "PERMISSION_CREATE", "PERMISSION_EDIT", "PERMISSION_DELETE"
        );
        return systemPermissions.contains(code);
    }
} 