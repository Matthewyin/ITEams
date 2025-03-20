package com.iteams.controller;

import com.iteams.model.dto.ApiResponse;
import com.iteams.model.dto.PermissionDTO;
import com.iteams.model.enums.ModuleType;
import com.iteams.model.enums.OperationType;
import com.iteams.service.LogService;
import com.iteams.service.PermissionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
 * 权限管理控制器
 */
@RestController
@RequestMapping("/api/permissions")
@PreAuthorize("hasRole('SUPERADMIN')")
public class PermissionController {
    
    @Autowired
    private PermissionService permissionService;
    
    @Autowired
    private LogService logService;
    
    /**
     * 获取权限列表
     *
     * @return 权限列表
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<PermissionDTO>>> getAllPermissions() {
        List<PermissionDTO> permissions = permissionService.getAllPermissions();
        return ResponseEntity.ok(new ApiResponse<>(true, "获取权限列表成功", permissions));
    }
    
    /**
     * 获取权限树
     *
     * @return 权限树
     */
    @GetMapping("/tree")
    public ResponseEntity<ApiResponse<List<PermissionDTO>>> getPermissionTree() {
        List<PermissionDTO> permissionTree = permissionService.getPermissionTree();
        return ResponseEntity.ok(new ApiResponse<>(true, "获取权限树成功", permissionTree));
    }
    
    /**
     * 获取权限详情
     *
     * @param id 权限ID
     * @return 权限详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PermissionDTO>> getPermission(@PathVariable Long id) {
        return permissionService.getPermissionById(id)
                .map(permission -> ResponseEntity.ok(new ApiResponse<>(true, "获取权限详情成功", permission)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "权限不存在", null)));
    }
    
    /**
     * 创建权限
     *
     * @param permissionDTO 权限信息
     * @param principal 当前用户
     * @return 创建结果
     */
    @PostMapping
    public ResponseEntity<ApiResponse<PermissionDTO>> createPermission(
            @Valid @RequestBody PermissionDTO permissionDTO,
            Principal principal) {
        
        PermissionDTO createdPermission = permissionService.createPermission(permissionDTO);
        
        // 记录操作日志
        logService.logOperation(
                principal.getName(),
                ModuleType.AUTH,
                OperationType.CREATE,
                "创建权限: " + permissionDTO.getPermissionName()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "权限创建成功", createdPermission));
    }
    
    /**
     * 更新权限
     *
     * @param id 权限ID
     * @param permissionDTO 权限信息
     * @param principal 当前用户
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PermissionDTO>> updatePermission(
            @PathVariable Long id,
            @Valid @RequestBody PermissionDTO permissionDTO,
            Principal principal) {
        
        PermissionDTO updatedPermission = permissionService.updatePermission(id, permissionDTO);
        
        // 记录操作日志
        logService.logOperation(
                principal.getName(),
                ModuleType.AUTH,
                OperationType.UPDATE,
                "更新权限: " + updatedPermission.getPermissionName()
        );
        
        return ResponseEntity.ok(new ApiResponse<>(true, "权限更新成功", updatedPermission));
    }
    
    /**
     * 删除权限
     *
     * @param id 权限ID
     * @param principal 当前用户
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePermission(
            @PathVariable Long id,
            Principal principal) {
        
        // 获取要删除的权限名称
        String permissionName = permissionService.getPermissionById(id)
                .map(PermissionDTO::getPermissionName)
                .orElse("未知权限");
        
        boolean result = permissionService.deletePermission(id);
        
        if (result) {
            // 记录操作日志
            logService.logOperation(
                    principal.getName(),
                    ModuleType.AUTH,
                    OperationType.DELETE,
                    "删除权限: " + permissionName
            );
            
            return ResponseEntity.ok(new ApiResponse<>(true, "权限删除成功", null));
        } else {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "权限删除失败，该权限可能有子权限或者被角色使用", null));
        }
    }
    
    /**
     * 获取角色的权限列表
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    @GetMapping("/role/{roleId}")
    public ResponseEntity<ApiResponse<List<PermissionDTO>>> getRolePermissions(@PathVariable Long roleId) {
        List<PermissionDTO> permissions = permissionService.getRolePermissions(roleId);
        return ResponseEntity.ok(new ApiResponse<>(true, "获取角色权限成功", permissions));
    }
    
    /**
     * 获取用户的权限列表
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<PermissionDTO>>> getUserPermissions(@PathVariable Long userId) {
        List<PermissionDTO> permissions = permissionService.getUserPermissions(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "获取用户权限成功", permissions));
    }
    
    /**
     * 获取用户的菜单列表
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    @GetMapping("/user/{userId}/menus")
    public ResponseEntity<ApiResponse<List<PermissionDTO>>> getUserMenus(@PathVariable Long userId) {
        List<PermissionDTO> menus = permissionService.getUserMenus(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "获取用户菜单成功", menus));
    }
} 