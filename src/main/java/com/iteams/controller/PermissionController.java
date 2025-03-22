package com.iteams.controller;

import com.iteams.model.dto.ApiResponse;
import com.iteams.model.dto.PermissionDTO;
import com.iteams.service.PermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * 权限管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class PermissionController {

    private final PermissionService permissionService;

    /**
     * 获取所有权限
     *
     * @return 权限列表
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<PermissionDTO>>> getAllPermissions() {
        log.info("获取所有权限");
        List<PermissionDTO> permissions = permissionService.getAllPermissions();
        return ResponseEntity.ok(ApiResponse.success("获取权限列表成功", permissions));
    }

    /**
     * 获取权限详情
     *
     * @param id 权限ID
     * @return 权限详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PermissionDTO>> getPermissionById(@PathVariable Long id) {
        log.info("获取权限详情，ID：{}", id);
        PermissionDTO permission = permissionService.getPermissionById(id);
        return ResponseEntity.ok(ApiResponse.success("获取权限详情成功", permission));
    }

    /**
     * 创建权限
     *
     * @param permissionDTO 权限数据
     * @return 创建后的权限
     */
    @PostMapping
    public ResponseEntity<ApiResponse<PermissionDTO>> createPermission(@Valid @RequestBody PermissionDTO permissionDTO) {
        log.info("创建权限：{}", permissionDTO.getName());
        PermissionDTO createdPermission = permissionService.createPermission(permissionDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("创建权限成功", createdPermission));
    }

    /**
     * 更新权限
     *
     * @param id            权限ID
     * @param permissionDTO 权限数据
     * @return 更新后的权限
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PermissionDTO>> updatePermission(
            @PathVariable Long id,
            @Valid @RequestBody PermissionDTO permissionDTO) {
        log.info("更新权限，ID：{}", id);
        PermissionDTO updatedPermission = permissionService.updatePermission(id, permissionDTO);
        return ResponseEntity.ok(ApiResponse.success("更新权限成功", updatedPermission));
    }

    /**
     * 删除权限
     *
     * @param id 权限ID
     * @return 响应
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePermission(@PathVariable Long id) {
        log.info("删除权限，ID：{}", id);
        permissionService.deletePermission(id);
        return ResponseEntity.ok(ApiResponse.success("删除权限成功", null));
    }

    /**
     * 获取角色权限
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    @GetMapping("/role/{roleId}")
    public ResponseEntity<ApiResponse<List<PermissionDTO>>> getRolePermissions(@PathVariable Long roleId) {
        log.info("获取角色权限，角色ID：{}", roleId);
        List<PermissionDTO> permissions = permissionService.getRolePermissions(roleId);
        return ResponseEntity.ok(ApiResponse.success("获取角色权限成功", permissions));
    }

    /**
     * 获取用户权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<PermissionDTO>>> getUserPermissions(@PathVariable Long userId) {
        log.info("获取用户权限，用户ID：{}", userId);
        List<PermissionDTO> permissions = permissionService.getUserPermissions(userId);
        return ResponseEntity.ok(ApiResponse.success("获取用户权限成功", permissions));
    }

    /**
     * 获取用户权限编码
     *
     * @param userId 用户ID
     * @return 权限编码列表
     */
    @GetMapping("/user/{userId}/codes")
    public ResponseEntity<ApiResponse<Set<String>>> getUserPermissionCodes(@PathVariable Long userId) {
        log.info("获取用户权限编码，用户ID：{}", userId);
        Set<String> permissionCodes = permissionService.getUserPermissionCodes(userId);
        return ResponseEntity.ok(ApiResponse.success("获取用户权限编码成功", permissionCodes));
    }
} 