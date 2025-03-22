package com.iteams.controller;

import com.iteams.model.dto.ApiResponse;
import com.iteams.model.dto.PasswordDTO;
import com.iteams.model.dto.UserDTO;
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
import java.util.Map;

/**
 * 用户管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 获取用户列表
     *
     * @param username   用户名（模糊查询）
     * @param realName   姓名（模糊查询）
     * @param department 部门（模糊查询）
     * @param pageable   分页参数
     * @return 用户列表
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Page<UserDTO>>> getUsers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String realName,
            @RequestParam(required = false) String department,
            @PageableDefault(size = 10) Pageable pageable) {
        log.info("获取用户列表，查询条件：username={}, realName={}, department={}", username, realName, department);
        Page<UserDTO> users = userService.getUsers(username, realName, department, pageable);
        return ResponseEntity.ok(ApiResponse.success("获取用户列表成功", users));
    }

    /**
     * 获取用户详情
     *
     * @param id 用户ID
     * @return 用户详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable Long id) {
        log.info("获取用户详情，ID：{}", id);
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success("获取用户详情成功", user));
    }

    /**
     * 创建用户
     *
     * @param userDTO 用户数据
     * @return 创建后的用户
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<UserDTO>> createUser(@Valid @RequestBody UserDTO userDTO) {
        log.info("创建用户：{}", userDTO.getUsername());
        UserDTO createdUser = userService.createUser(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("创建用户成功", createdUser));
    }

    /**
     * 更新用户
     *
     * @param id      用户ID
     * @param userDTO 用户数据
     * @return 更新后的用户
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<UserDTO>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserDTO userDTO) {
        log.info("更新用户，ID：{}", id);
        UserDTO updatedUser = userService.updateUser(id, userDTO);
        return ResponseEntity.ok(ApiResponse.success("更新用户成功", updatedUser));
    }

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return 响应
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        log.info("删除用户，ID：{}", id);
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("删除用户成功", null));
    }

    /**
     * 重置用户密码
     *
     * @param id 用户ID
     * @return 重置后的密码
     */
    @PostMapping("/reset-password/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, String>>> resetPassword(@PathVariable Long id) {
        log.info("重置用户密码，ID：{}", id);
        String password = userService.resetPassword(id);
        return ResponseEntity.ok(ApiResponse.success("重置密码成功", Map.of("password", password)));
    }

    /**
     * 分配用户角色
     *
     * @param userId  用户ID
     * @param roleIds 角色ID列表
     * @return 响应
     */
    @PostMapping("/{userId}/roles")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> assignRoles(
            @PathVariable Long userId,
            @RequestBody List<Long> roleIds) {
        log.info("分配用户角色，用户ID：{}，角色IDs：{}", userId, roleIds);
        userService.assignRoles(userId, roleIds);
        return ResponseEntity.ok(ApiResponse.success("分配角色成功", null));
    }

    /**
     * 更新当前用户信息
     *
     * @param userDTO 用户数据
     * @return 更新后的用户
     */
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserDTO>> updateCurrentUser(@Valid @RequestBody UserDTO userDTO) {
        log.info("更新当前用户信息");
        UserDTO updatedUser = userService.updateCurrentUser(userDTO);
        return ResponseEntity.ok(ApiResponse.success("更新个人信息成功", updatedUser));
    }

    /**
     * 修改当前用户密码
     *
     * @param passwordDTO 密码数据
     * @return 响应
     */
    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@Valid @RequestBody PasswordDTO passwordDTO) {
        log.info("修改当前用户密码");
        userService.changePassword(passwordDTO);
        return ResponseEntity.ok(ApiResponse.success("密码修改成功", null));
    }
} 