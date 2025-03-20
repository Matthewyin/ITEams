package com.iteams.service.impl;

import com.iteams.exception.BusinessException;
import com.iteams.model.dto.RoleDTO;
import com.iteams.model.entity.Role;
import com.iteams.model.entity.RolePermission;
import com.iteams.model.entity.UserRole;
import com.iteams.repository.RolePermissionRepository;
import com.iteams.repository.RoleRepository;
import com.iteams.repository.UserRoleRepository;
import com.iteams.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 角色服务实现类
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final UserRoleRepository userRoleRepository;

    @Override
    public List<RoleDTO> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(role -> {
                    RoleDTO dto = RoleDTO.fromEntity(role);
                    dto.setUserCount(userRoleRepository.countByRoleId(role.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<RoleDTO> getAllRolesExceptSuperAdmin() {
        return roleRepository.findAllExceptSuperAdmin().stream()
                .map(role -> {
                    RoleDTO dto = RoleDTO.fromEntity(role);
                    dto.setUserCount(userRoleRepository.countByRoleId(role.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Page<RoleDTO> getRoles(String query, Pageable pageable) {
        Specification<Role> spec = (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (StringUtils.hasText(query)) {
                predicates.add(criteriaBuilder.or(
                    criteriaBuilder.like(root.get("roleName"), "%" + query + "%"),
                    criteriaBuilder.like(root.get("roleCode"), "%" + query + "%"),
                    criteriaBuilder.like(root.get("description"), "%" + query + "%")
                ));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        
        return roleRepository.findAll(spec, pageable)
                .map(role -> {
                    RoleDTO dto = RoleDTO.fromEntity(role);
                    dto.setUserCount(userRoleRepository.countByRoleId(role.getId()));
                    return dto;
                });
    }

    @Override
    public Optional<RoleDTO> getRoleById(Long id) {
        return roleRepository.findById(id)
                .map(role -> {
                    RoleDTO dto = RoleDTO.fromEntity(role);
                    dto.setUserCount(userRoleRepository.countByRoleId(role.getId()));
                    return dto;
                });
    }

    @Override
    public Optional<RoleDTO> getRoleByCode(String roleCode) {
        return roleRepository.findByRoleCode(roleCode)
                .map(role -> {
                    RoleDTO dto = RoleDTO.fromEntity(role);
                    dto.setUserCount(userRoleRepository.countByRoleId(role.getId()));
                    return dto;
                });
    }

    @Override
    @Transactional
    public RoleDTO createRole(RoleDTO roleDTO, String currentUsername) {
        // 检查角色代码是否已存在
        if (roleRepository.existsByRoleCode(roleDTO.getRoleCode())) {
            throw new BusinessException("角色代码已存在");
        }
        
        Role role = roleDTO.toEntity();
        role.setCreatedBy(currentUsername);
        role = roleRepository.save(role);
        
        // 如果有权限ID列表，则分配权限
        if (roleDTO.getPermissionIds() != null && !roleDTO.getPermissionIds().isEmpty()) {
            assignRolePermissions(role.getId(), roleDTO.getPermissionIds());
        }
        
        return RoleDTO.fromEntity(role);
    }

    @Override
    @Transactional
    public RoleDTO updateRole(Long id, RoleDTO roleDTO, String currentUsername) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("角色不存在"));
        
        // 检查是否是超级管理员角色，超级管理员角色不允许修改代码
        if ("superadmin".equals(role.getRoleCode()) && !roleDTO.getRoleCode().equals(role.getRoleCode())) {
            throw new BusinessException("超级管理员角色代码不允许修改");
        }
        
        // 检查角色代码是否已存在(如果更改了角色代码)
        if (!roleDTO.getRoleCode().equals(role.getRoleCode()) && roleRepository.existsByRoleCode(roleDTO.getRoleCode())) {
            throw new BusinessException("角色代码已存在");
        }
        
        // 更新角色信息
        role.setRoleCode(roleDTO.getRoleCode());
        role.setRoleName(roleDTO.getRoleName());
        role.setDescription(roleDTO.getDescription());
        role.setStatus(roleDTO.getStatus());
        role.setSortOrder(roleDTO.getSortOrder());
        role.setUpdatedBy(currentUsername);
        
        role = roleRepository.save(role);
        
        // 如果有权限ID列表，则更新权限
        if (roleDTO.getPermissionIds() != null) {
            // 先删除所有的角色权限
            rolePermissionRepository.deleteByRoleId(id);
            // 重新分配权限
            if (!roleDTO.getPermissionIds().isEmpty()) {
                assignRolePermissions(id, roleDTO.getPermissionIds());
            }
        }
        
        return RoleDTO.fromEntity(role);
    }

    @Override
    @Transactional
    public boolean deleteRole(Long id) {
        try {
            Role role = roleRepository.findById(id)
                    .orElseThrow(() -> new BusinessException("角色不存在"));
            
            // 检查是否是超级管理员角色，超级管理员角色不允许删除
            if ("superadmin".equals(role.getRoleCode())) {
                throw new BusinessException("超级管理员角色不允许删除");
            }
            
            // 检查角色是否有用户使用
            if (userRoleRepository.countByRoleId(id) > 0) {
                throw new BusinessException("该角色下有用户，不允许删除");
            }
            
            // 删除角色权限
            rolePermissionRepository.deleteByRoleId(id);
            
            // 删除角色
            roleRepository.delete(role);
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<Long> getRolePermissions(Long roleId) {
        return rolePermissionRepository.findByRoleId(roleId).stream()
                .map(RolePermission::getPermissionId)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean assignRolePermissions(Long roleId, List<Long> permissionIds) {
        try {
            // 检查角色是否存在
            if (!roleRepository.existsById(roleId)) {
                throw new BusinessException("角色不存在");
            }
            
            // 删除现有的角色权限
            rolePermissionRepository.deleteByRoleId(roleId);
            
            // 重新分配权限
            List<RolePermission> rolePermissions = permissionIds.stream()
                    .map(permissionId -> {
                        RolePermission rolePermission = new RolePermission();
                        rolePermission.setRoleId(roleId);
                        rolePermission.setPermissionId(permissionId);
                        return rolePermission;
                    })
                    .collect(Collectors.toList());
            
            if (!rolePermissions.isEmpty()) {
                rolePermissionRepository.saveAll(rolePermissions);
            }
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<RoleDTO> getUserRoles(Long userId) {
        return userRoleRepository.findByUserId(userId).stream()
                .map(UserRole::getRoleId)
                .map(roleId -> roleRepository.findById(roleId).orElse(null))
                .filter(role -> role != null)
                .map(RoleDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean assignUserRoles(Long userId, List<Long> roleIds) {
        try {
            // 删除用户现有的角色
            userRoleRepository.deleteByUserId(userId);
            
            // 重新分配角色
            List<UserRole> userRoles = roleIds.stream()
                    .map(roleId -> {
                        UserRole userRole = new UserRole();
                        userRole.setUserId(userId);
                        userRole.setRoleId(roleId);
                        return userRole;
                    })
                    .collect(Collectors.toList());
            
            if (!userRoles.isEmpty()) {
                userRoleRepository.saveAll(userRoles);
            }
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }
} 