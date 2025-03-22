package com.iteams.controller;

import com.iteams.model.dto.ApiResponse;
import com.iteams.model.dto.RoleDTO;
import com.iteams.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class RoleController {

    private final RoleService roleService;

    /**
     * 获取角色列表
     *
     * @param name     角色名称（模糊查询）
     * @param pageable 分页参数
     * @return 角色列表
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<RoleDTO>>> getRoles(
            @RequestParam(required = false) String name,
            @PageableDefault(size = 10) Pageable pageable) {
        log.info("获取角色列表，查询条件：name={}", name);
        Page<RoleDTO> roles = roleService.getRoles(name, pageable);
        return ResponseEntity.ok(ApiResponse.success("获取角色列表成功", roles));
    }

    /**
     * 获取所有角色（不分页）
     *
     * @return 角色列表
     */
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<RoleDTO>>> getAllRoles() {
        log.info("获取所有角色（不分页）");
        List<RoleDTO> roles = roleService.getAllRoles();
        return ResponseEntity.ok(ApiResponse.success("获取角色列表成功", roles));
    }

    /**
     * 获取角色详情
     *
     * @param id 角色ID
     * @return 角色详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleDTO>> getRoleById(@PathVariable Long id) {
        log.info("获取角色详情，ID：{}", id);
        RoleDTO role = roleService.getRoleById(id);
        return ResponseEntity.ok(ApiResponse.success("获取角色详情成功", role));
    }

    /**
     * 创建角色
     *
     * @param roleDTO 角色数据
     * @return 创建后的角色
     */
    @PostMapping
    public ResponseEntity<ApiResponse<RoleDTO>> createRole(@Valid @RequestBody RoleDTO roleDTO) {
        log.info("创建角色：{}", roleDTO.getName());
        RoleDTO createdRole = roleService.createRole(roleDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("创建角色成功", createdRole));
    }

    /**
     * 更新角色
     *
     * @param id      角色ID
     * @param roleDTO 角色数据
     * @return 更新后的角色
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleDTO>> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody RoleDTO roleDTO) {
        log.info("更新角色，ID：{}", id);
        RoleDTO updatedRole = roleService.updateRole(id, roleDTO);
        return ResponseEntity.ok(ApiResponse.success("更新角色成功", updatedRole));
    }

    /**
     * 删除角色
     *
     * @param id 角色ID
     * @return 响应
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRole(@PathVariable Long id) {
        log.info("删除角色，ID：{}", id);
        roleService.deleteRole(id);
        return ResponseEntity.ok(ApiResponse.success("删除角色成功", null));
    }

    /**
     * 获取角色权限
     *
     * @param id 角色ID
     * @return 权限编码列表
     */
    @GetMapping("/{id}/permissions")
    public ResponseEntity<ApiResponse<List<String>>> getRolePermissions(@PathVariable Long id) {
        log.info("获取角色权限，角色ID：{}", id);
        List<String> permissions = roleService.getRolePermissions(id);
        return ResponseEntity.ok(ApiResponse.success("获取角色权限成功", permissions));
    }

    /**
     * 分配角色权限
     *
     * @param id            角色ID
     * @param permissionIds 权限ID列表
     * @return 响应
     */
    @PostMapping("/{id}/permissions")
    public ResponseEntity<ApiResponse<Void>> assignPermissions(
            @PathVariable Long id,
            @RequestBody List<Long> permissionIds) {
        log.info("分配角色权限，角色ID：{}，权限IDs：{}", id, permissionIds);
        roleService.assignPermissions(id, permissionIds);
        return ResponseEntity.ok(ApiResponse.success("分配权限成功", null));
    }

    /**
     * 获取用户的角色
     *
     * @param userId 用户ID
     * @return 角色编码列表
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<String>>> getUserRoles(@PathVariable Long userId) {
        log.info("获取用户角色，用户ID：{}", userId);
        List<String> roles = roleService.getUserRoles(userId);
        return ResponseEntity.ok(ApiResponse.success("获取用户角色成功", roles));
    }
} 