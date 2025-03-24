package com.iteams.controller;

import com.iteams.common.ApiResponse;
import com.iteams.model.dto.UserDTO;
import com.iteams.model.dto.UserGroupDTO;
import com.iteams.model.dto.pagination.PagedResponse;
import com.iteams.service.UserGroupService;
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
 * 用户组管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class UserGroupController {

    private final UserGroupService userGroupService;
    private final UserService userService;

    /**
     * 获取用户组列表
     *
     * @param name     用户组名称（模糊查询）
     * @param pageable 分页参数
     * @return 用户组列表
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<PagedResponse<UserGroupDTO>>> getUserGroups(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) Boolean enabled,
            @PageableDefault(size = 10) Pageable pageable) {
        log.info("获取用户组列表，查询条件：name={}, code={}, enabled={}", name, code, enabled);
        Page<UserGroupDTO> userGroups = userGroupService.getUserGroups(name, code, enabled, pageable);
        PagedResponse<UserGroupDTO> response = PagedResponse.of(userGroups);
        return ResponseEntity.ok(ApiResponse.success("获取用户组列表成功", response));
    }

    /**
     * 获取所有用户组（不分页）
     *
     * @return 所有用户组列表
     */
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<UserGroupDTO>>> getAllUserGroups(
            @RequestParam(required = false) Boolean enabled) {
        log.info("获取所有用户组，enabled={}", enabled);
        List<UserGroupDTO> userGroups = userGroupService.getAllUserGroups(enabled);
        return ResponseEntity.ok(ApiResponse.success("获取用户组列表成功", userGroups));
    }

    /**
     * 获取用户组详情
     *
     * @param id 用户组ID
     * @return 用户组详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<UserGroupDTO>> getUserGroupById(@PathVariable Long id) {
        log.info("获取用户组详情，id={}", id);
        UserGroupDTO userGroup = userGroupService.getUserGroupById(id);
        return ResponseEntity.ok(ApiResponse.success("获取用户组详情成功", userGroup));
    }

    /**
     * 创建用户组
     *
     * @param userGroupDTO 用户组信息
     * @return 创建的用户组
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<UserGroupDTO>> createUserGroup(@Valid @RequestBody UserGroupDTO userGroupDTO) {
        log.info("创建用户组，name={}", userGroupDTO.getName());
        UserGroupDTO createdUserGroup = userGroupService.createUserGroup(userGroupDTO);
        return new ResponseEntity<>(ApiResponse.success("创建用户组成功", createdUserGroup), HttpStatus.CREATED);
    }

    /**
     * 更新用户组
     *
     * @param id           用户组ID
     * @param userGroupDTO 用户组信息
     * @return 更新后的用户组
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<UserGroupDTO>> updateUserGroup(
            @PathVariable Long id,
            @Valid @RequestBody UserGroupDTO userGroupDTO) {
        log.info("更新用户组，id={}, name={}", id, userGroupDTO.getName());
        UserGroupDTO updatedUserGroup = userGroupService.updateUserGroup(id, userGroupDTO);
        return ResponseEntity.ok(ApiResponse.success("更新用户组成功", updatedUserGroup));
    }

    /**
     * 删除用户组
     *
     * @param id 用户组ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUserGroup(@PathVariable Long id) {
        log.info("删除用户组，id={}", id);
        userGroupService.deleteUserGroup(id);
        return ResponseEntity.ok(ApiResponse.success("删除用户组成功", null));
    }

    /**
     * 获取用户组用户
     *
     * @param id 用户组ID
     * @param pageable 分页参数
     * @return 用户组用户列表
     */
    @GetMapping("/{id}/users")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Page<UserDTO>>> getUserGroupUsers(
            @PathVariable Long id,
            @PageableDefault(size = 10) Pageable pageable) {
        log.info("获取用户组用户，userGroupId={}", id);
        // 使用用户组ID查询用户
        // 注意：这里暂时返回所有用户，实际实现时需要根据用户组ID过滤
        Page<UserDTO> users = userService.getUsers(null, null, null, pageable);
        return ResponseEntity.ok(ApiResponse.success("获取用户组用户成功", users));
    }

    /**
     * 添加用户到用户组
     *
     * @param id      用户组ID
     * @param userIds 用户ID列表
     * @return 操作结果
     */
    @PostMapping("/{id}/users")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<UserGroupDTO>> addUsersToGroup(
            @PathVariable Long id,
            @RequestBody List<Long> userIds) {
        log.info("添加用户到用户组，userGroupId={}, userIds={}", id, userIds);
        UserGroupDTO updatedGroup = userGroupService.assignUsers(id, userIds);
        return ResponseEntity.ok(ApiResponse.success("添加用户到用户组成功", updatedGroup));
    }

    /**
     * 从用户组移除用户
     *
     * @param id      用户组ID
     * @param userIds 用户ID列表
     * @return 操作结果
     */
    @DeleteMapping("/{id}/users")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<UserGroupDTO>> removeUsersFromGroup(
            @PathVariable Long id,
            @RequestBody List<Long> userIds) {
        log.info("从用户组移除用户，userGroupId={}, userIds={}", id, userIds);
        UserGroupDTO updatedGroup = userGroupService.removeUsers(id, userIds);
        return ResponseEntity.ok(ApiResponse.success("从用户组移除用户成功", updatedGroup));
    }
}
