package com.iteams.controller;

import com.iteams.common.ApiResponse;
import com.iteams.model.dto.DepartmentDTO;
import com.iteams.model.dto.UserDTO;
import com.iteams.model.dto.pagination.PagedResponse;
import com.iteams.service.DepartmentService;
import com.iteams.service.UserService;
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
 * 部门管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;
    private final UserService userService;

    /**
     * 获取部门列表
     *
     * @param name     部门名称（模糊查询）
     * @param pageable 分页参数
     * @return 部门列表
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<PagedResponse<DepartmentDTO>>> getDepartments(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) Boolean enabled,
            @PageableDefault(size = 10) Pageable pageable) {
        log.info("获取部门列表，查询条件：name={}, code={}, enabled={}", name, code, enabled);
        Page<DepartmentDTO> departments = departmentService.getDepartments(name, code, enabled, pageable);
        PagedResponse<DepartmentDTO> response = PagedResponse.of(departments);
        return ResponseEntity.ok(ApiResponse.success("获取部门列表成功", response));
    }

    /**
     * 获取所有部门（不分页）
     *
     * @return 所有部门列表
     */
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<DepartmentDTO>>> getAllDepartments(
            @RequestParam(required = false) Boolean enabled) {
        log.info("获取所有部门，enabled={}", enabled);
        List<DepartmentDTO> departments = departmentService.getAllDepartments(enabled);
        return ResponseEntity.ok(ApiResponse.success("获取部门列表成功", departments));
    }

    /**
     * 获取部门详情
     *
     * @param id 部门ID
     * @return 部门详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<DepartmentDTO>> getDepartmentById(@PathVariable Long id) {
        log.info("获取部门详情，id={}", id);
        DepartmentDTO department = departmentService.getDepartmentById(id);
        return ResponseEntity.ok(ApiResponse.success("获取部门详情成功", department));
    }

    /**
     * 创建部门
     *
     * @param departmentDTO 部门信息
     * @return 创建的部门
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<DepartmentDTO>> createDepartment(@Valid @RequestBody DepartmentDTO departmentDTO) {
        log.info("创建部门，name={}", departmentDTO.getName());
        DepartmentDTO createdDepartment = departmentService.createDepartment(departmentDTO);
        return new ResponseEntity<>(ApiResponse.success("创建部门成功", createdDepartment), HttpStatus.CREATED);
    }

    /**
     * 更新部门
     *
     * @param id           部门ID
     * @param departmentDTO 部门信息
     * @return 更新后的部门
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<DepartmentDTO>> updateDepartment(
            @PathVariable Long id,
            @Valid @RequestBody DepartmentDTO departmentDTO) {
        log.info("更新部门，id={}, name={}", id, departmentDTO.getName());
        DepartmentDTO updatedDepartment = departmentService.updateDepartment(id, departmentDTO);
        return ResponseEntity.ok(ApiResponse.success("更新部门成功", updatedDepartment));
    }

    /**
     * 删除部门
     *
     * @param id 部门ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteDepartment(@PathVariable Long id) {
        log.info("删除部门，id={}", id);
        departmentService.deleteDepartment(id);
        return ResponseEntity.ok(ApiResponse.success("删除部门成功", null));
    }

    /**
     * 获取部门用户
     *
     * @param id 部门ID
     * @param pageable 分页参数
     * @return 部门用户列表
     */
    @GetMapping("/{id}/users")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Page<UserDTO>>> getDepartmentUsers(
            @PathVariable Long id,
            @PageableDefault(size = 10) Pageable pageable) {
        log.info("获取部门用户，departmentId={}", id);
        // 获取部门信息
        DepartmentDTO department = departmentService.getDepartmentById(id);
        // 使用部门名称查询用户
        Page<UserDTO> users = userService.getUsers(null, null, department.getName(), pageable);
        return ResponseEntity.ok(ApiResponse.success("获取部门用户成功", users));
    }
}
