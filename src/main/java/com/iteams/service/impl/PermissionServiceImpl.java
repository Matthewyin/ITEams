package com.iteams.service.impl;

import com.iteams.exception.BusinessException;
import com.iteams.model.dto.PermissionDTO;
import com.iteams.model.entity.Permission;
import com.iteams.repository.PermissionRepository;
import com.iteams.repository.RolePermissionRepository;
import com.iteams.service.PermissionService;
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
 * 权限服务实现类
 */
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;

    @Override
    public List<PermissionDTO> getAllPermissions() {
        return permissionRepository.findAll().stream()
                .map(PermissionDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<PermissionDTO> getPermissionTree() {
        List<PermissionDTO> allPermissions = getAllPermissions();
        return PermissionDTO.buildTree(allPermissions);
    }

    @Override
    public Page<PermissionDTO> getPermissions(String query, Pageable pageable) {
        Specification<Permission> spec = (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (StringUtils.hasText(query)) {
                predicates.add(criteriaBuilder.or(
                    criteriaBuilder.like(root.get("permissionName"), "%" + query + "%"),
                    criteriaBuilder.like(root.get("permissionCode"), "%" + query + "%"),
                    criteriaBuilder.like(root.get("path"), "%" + query + "%")
                ));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        
        return permissionRepository.findAll(spec, pageable)
                .map(PermissionDTO::fromEntity);
    }

    @Override
    public Optional<PermissionDTO> getPermissionById(Long id) {
        return permissionRepository.findById(id)
                .map(PermissionDTO::fromEntity);
    }

    @Override
    public Optional<PermissionDTO> getPermissionByCode(String permissionCode) {
        return permissionRepository.findByPermissionCode(permissionCode)
                .map(PermissionDTO::fromEntity);
    }

    @Override
    @Transactional
    public PermissionDTO createPermission(PermissionDTO permissionDTO) {
        // 检查权限代码是否已存在
        if (permissionRepository.existsByPermissionCode(permissionDTO.getPermissionCode())) {
            throw new BusinessException("权限代码已存在");
        }
        
        // 检查父级权限是否存在
        if (permissionDTO.getParentId() != null && permissionDTO.getParentId() > 0) {
            permissionRepository.findById(permissionDTO.getParentId())
                    .orElseThrow(() -> new BusinessException("父级权限不存在"));
        }
        
        Permission permission = permissionDTO.toEntity();
        permission = permissionRepository.save(permission);
        
        return PermissionDTO.fromEntity(permission);
    }

    @Override
    @Transactional
    public PermissionDTO updatePermission(Long id, PermissionDTO permissionDTO) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("权限不存在"));
        
        // 检查权限代码是否已存在(如果更改了权限代码)
        if (!permissionDTO.getPermissionCode().equals(permission.getPermissionCode()) && 
                permissionRepository.existsByPermissionCode(permissionDTO.getPermissionCode())) {
            throw new BusinessException("权限代码已存在");
        }
        
        // 检查父级权限是否存在
        if (permissionDTO.getParentId() != null && permissionDTO.getParentId() > 0) {
            // 防止循环引用 - 不能将自己或子权限设置为父权限
            if (permissionDTO.getParentId().equals(id)) {
                throw new BusinessException("不能将自己设置为父级权限");
            }
            
            // 检查父级权限是否存在
            permissionRepository.findById(permissionDTO.getParentId())
                    .orElseThrow(() -> new BusinessException("父级权限不存在"));
            
            // TODO: 进一步检查是否形成循环引用(子权限的子权限...)
        }
        
        // 更新权限信息
        permission.setParentId(permissionDTO.getParentId());
        permission.setPermissionCode(permissionDTO.getPermissionCode());
        permission.setPermissionName(permissionDTO.getPermissionName());
        permission.setPermissionType(permissionDTO.getPermissionType());
        permission.setPath(permissionDTO.getPath());
        permission.setComponent(permissionDTO.getComponent());
        permission.setIcon(permissionDTO.getIcon());
        permission.setSortOrder(permissionDTO.getSortOrder());
        permission.setStatus(permissionDTO.getStatus());
        permission.setVisible(permissionDTO.getVisible());
        
        permission = permissionRepository.save(permission);
        
        return PermissionDTO.fromEntity(permission);
    }

    @Override
    @Transactional
    public boolean deletePermission(Long id) {
        try {
            Permission permission = permissionRepository.findById(id)
                    .orElseThrow(() -> new BusinessException("权限不存在"));
            
            // 检查是否有子权限
            List<Permission> children = permissionRepository.findByParentIdOrderBySortOrder(id);
            if (!children.isEmpty()) {
                throw new BusinessException("该权限下有子权限，不允许删除");
            }
            
            // 检查是否有角色使用该权限
            long count = rolePermissionRepository.findByPermissionId(id).size();
            if (count > 0) {
                throw new BusinessException("该权限已被角色使用，不允许删除");
            }
            
            // 删除权限
            permissionRepository.delete(permission);
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<PermissionDTO> getRolePermissions(Long roleId) {
        List<Permission> permissions = permissionRepository.findByRoleId(roleId);
        return permissions.stream()
                .map(PermissionDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<PermissionDTO> getUserPermissions(Long userId) {
        return permissionRepository.findByUserId(userId).stream()
                .map(PermissionDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<PermissionDTO> getUserMenus(Long userId) {
        return permissionRepository.findMenusByUserId(userId).stream()
                .map(PermissionDTO::fromEntity)
                .collect(Collectors.toList());
    }
} 