package com.iteams.controller;

import com.iteams.model.dto.ApiResponse;
import com.iteams.model.dto.PageResult;
import com.iteams.model.dto.RoleDTO;
import com.iteams.model.enums.ModuleType;
import com.iteams.model.enums.OperationType;
import com.iteams.service.LogService;
import com.iteams.service.RoleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
 * 角色管理控制器
 */
@RestController
@RequestMapping("/api/roles")
@PreAuthorize("hasRole('ADMIN') or hasRole('SUPERADMIN')")
public class RoleController {
    
    @Autowired
    private RoleService roleService;
    
    @Autowired
    private LogService logService;
    
    /**
     * 获取角色列表
     *
     * @param query 查询条件
     * @param page 页码
     * @param limit 每页大小
     * @return 角色列表
     */
    @GetMapping
    @PreAuthorize("hasAuthority('system:role:list')")
    public ResponseEntity<ApiResponse<PageResult<RoleDTO>>> getRoles(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.ASC, "sortOrder"));
        Page<RoleDTO> rolePage = roleService.getRoles(query, pageable);
        
        PageResult<RoleDTO> pageResult = new PageResult<>();
        pageResult.setItems(rolePage.getContent());
        pageResult.setTotal(rolePage.getTotalElements());
        
        return ResponseEntity.ok(new ApiResponse<>(true, "获取角色列表成功", pageResult));
    }
    
    /**
     * 获取所有角色（不分页）
     *
     * @return 角色列表
     */
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('system:role:list')")
    public ResponseEntity<ApiResponse<List<RoleDTO>>> getAllRoles() {
        List<RoleDTO> roles = roleService.getAllRolesExceptSuperAdmin();
        return ResponseEntity.ok(new ApiResponse<>(true, "获取所有角色成功", roles));
    }
    
    /**
     * 获取角色详情
     *
     * @param id 角色ID
     * @return 角色详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:list')")
    public ResponseEntity<ApiResponse<RoleDTO>> getRole(@PathVariable Long id) {
        return roleService.getRoleById(id)
                .map(role -> ResponseEntity.ok(new ApiResponse<>(true, "获取角色详情成功", role)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "角色不存在", null)));
    }
    
    /**
     * 创建角色
     *
     * @param roleDTO 角色信息
     * @param principal 当前用户
     * @return 创建结果
     */
    @PostMapping
    @PreAuthorize("hasAuthority('system:role:add')")
    public ResponseEntity<ApiResponse<RoleDTO>> createRole(
            @Valid @RequestBody RoleDTO roleDTO,
            Principal principal) {
        
        RoleDTO createdRole = roleService.createRole(roleDTO, principal.getName());
        
        // 记录操作日志
        logService.logOperation(
                principal.getName(),
                ModuleType.AUTH,
                OperationType.CREATE,
                "创建角色: " + roleDTO.getRoleName()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "角色创建成功", createdRole));
    }
    
    /**
     * 更新角色
     *
     * @param id 角色ID
     * @param roleDTO 角色信息
     * @param principal 当前用户
     * @return 更新结果
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:edit')")
    public ResponseEntity<ApiResponse<RoleDTO>> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody RoleDTO roleDTO,
            Principal principal) {
        
        RoleDTO updatedRole = roleService.updateRole(id, roleDTO, principal.getName());
        
        // 记录操作日志
        logService.logOperation(
                principal.getName(),
                ModuleType.AUTH,
                OperationType.UPDATE,
                "更新角色: " + updatedRole.getRoleName()
        );
        
        return ResponseEntity.ok(new ApiResponse<>(true, "角色更新成功", updatedRole));
    }
    
    /**
     * 删除角色
     *
     * @param id 角色ID
     * @param principal 当前用户
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:delete')")
    public ResponseEntity<ApiResponse<Void>> deleteRole(
            @PathVariable Long id,
            Principal principal) {
        
        // 获取要删除的角色名称
        String roleName = roleService.getRoleById(id)
                .map(RoleDTO::getRoleName)
                .orElse("未知角色");
        
        boolean result = roleService.deleteRole(id);
        
        if (result) {
            // 记录操作日志
            logService.logOperation(
                    principal.getName(),
                    ModuleType.AUTH,
                    OperationType.DELETE,
                    "删除角色: " + roleName
            );
            
            return ResponseEntity.ok(new ApiResponse<>(true, "角色删除成功", null));
        } else {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "角色删除失败，可能是超级管理员角色或者该角色下有用户", null));
        }
    }
    
    /**
     * 获取角色的权限ID列表
     *
     * @param id 角色ID
     * @return 权限ID列表
     */
    @GetMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('system:role:permission')")
    public ResponseEntity<ApiResponse<List<Long>>> getRolePermissions(@PathVariable Long id) {
        List<Long> permissionIds = roleService.getRolePermissions(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "获取角色权限成功", permissionIds));
    }
    
    /**
     * 分配角色权限
     *
     * @param id 角色ID
     * @param permissionIds 权限ID列表
     * @param principal 当前用户
     * @return 分配结果
     */
    @PostMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('system:role:permission')")
    public ResponseEntity<ApiResponse<Void>> assignRolePermissions(
            @PathVariable Long id,
            @RequestBody List<Long> permissionIds,
            Principal principal) {
        
        // 获取角色名称
        String roleName = roleService.getRoleById(id)
                .map(RoleDTO::getRoleName)
                .orElse("未知角色");
        
        boolean result = roleService.assignRolePermissions(id, permissionIds);
        
        if (result) {
            // 记录操作日志
            logService.logOperation(
                    principal.getName(),
                    ModuleType.AUTH,
                    OperationType.UPDATE,
                    "分配角色权限: " + roleName
            );
            
            return ResponseEntity.ok(new ApiResponse<>(true, "角色权限分配成功", null));
        } else {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "角色权限分配失败", null));
        }
    }
    
    /**
     * 获取用户的角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('system:user:role')")
    public ResponseEntity<ApiResponse<List<RoleDTO>>> getUserRoles(@PathVariable Long userId) {
        List<RoleDTO> roles = roleService.getUserRoles(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "获取用户角色成功", roles));
    }
    
    /**
     * 分配用户角色
     *
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     * @param principal 当前用户
     * @return 分配结果
     */
    @PostMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('system:user:role')")
    public ResponseEntity<ApiResponse<Void>> assignUserRoles(
            @PathVariable Long userId,
            @RequestBody List<Long> roleIds,
            Principal principal) {
        
        boolean result = roleService.assignUserRoles(userId, roleIds);
        
        if (result) {
            // 记录操作日志
            logService.logOperation(
                    principal.getName(),
                    ModuleType.AUTH,
                    OperationType.UPDATE,
                    "分配用户角色, 用户ID: " + userId
            );
            
            return ResponseEntity.ok(new ApiResponse<>(true, "用户角色分配成功", null));
        } else {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "用户角色分配失败", null));
        }
    }
} 