package com.iteams.controller;

import com.iteams.common.ApiResponse;
import com.iteams.model.dto.PasswordDTO;
import com.iteams.model.dto.UserDTO;
import com.iteams.service.FileStorageService;
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
import com.iteams.model.dto.pagination.PagedResponse;

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
    private final FileStorageService fileStorageService;

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
    public ResponseEntity<ApiResponse<PagedResponse<UserDTO>>> getUsers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String realName,
            @RequestParam(required = false) String department,
            @PageableDefault(size = 10) Pageable pageable) {
        log.info("获取用户列表，查询条件：username={}, realName={}, department={}", username, realName, department);
        Page<UserDTO> users = userService.getUsers(username, realName, department, pageable);
        PagedResponse<UserDTO> response = PagedResponse.of(users);
        return ResponseEntity.ok(ApiResponse.success("获取用户列表成功", response));
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
    
    /**
     * 更新用户头像
     *
     * @param id 用户ID
     * @param avatarUrl 头像URL
     * @return 更新后的用户
     */
    @PutMapping("/{id}/avatar")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<UserDTO>> updateUserAvatar(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        String avatarUrl = request.get("avatarUrl");
        if (avatarUrl == null || avatarUrl.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("头像URL不能为空"));
        }
        
        log.info("更新用户头像，用户ID：{}", id);
        UserDTO updatedUser = userService.updateUserAvatar(id, avatarUrl);
        return ResponseEntity.ok(ApiResponse.success("更新头像成功", updatedUser));
    }
    
    /**
     * 更新当前用户头像
     *
     * @param avatarUrl 头像URL
     * @return 更新后的用户
     */
    @PutMapping("/profile/avatar")
    public ResponseEntity<ApiResponse<UserDTO>> updateCurrentUserAvatar(
            @RequestBody Map<String, String> request) {
        String avatarUrl = request.get("avatarUrl");
        if (avatarUrl == null || avatarUrl.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("头像URL不能为空"));
        }
        
        log.info("更新当前用户头像");
        UserDTO updatedUser = userService.updateCurrentUserAvatar(avatarUrl);
        return ResponseEntity.ok(ApiResponse.success("更新头像成功", updatedUser));
    }
    
    /**
     * 上传当前用户头像文件
     *
     * @param file 头像文件
     * @return 更新后的用户
     */
    @PostMapping(value = "/profile/avatar/upload", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<UserDTO>> uploadCurrentUserAvatar(
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("文件不能为空"));
            }
            
            // 验证文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body(ApiResponse.error("只能上传图片文件"));
            }
            
            // 存储文件
            String filePath = fileStorageService.store(file, "avatars");
            log.info("当前用户头像上传成功: {}", filePath);
            
            // 更新用户头像
            UserDTO updatedUser = userService.updateCurrentUserAvatar("/api/files/view/avatars/" + filePath);
            return ResponseEntity.ok(ApiResponse.success("头像上传成功", updatedUser));
        } catch (Exception e) {
            log.error("头像上传失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("头像上传失败: " + e.getMessage()));
        }
    }
    
    /**
     * 上传用户头像文件
     *
     * @param id 用户ID
     * @param file 头像文件
     * @return 更新后的用户
     */
    @PostMapping(value = "/{id}/avatar/upload", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<UserDTO>> uploadUserAvatar(
            @PathVariable Long id,
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("文件不能为空"));
            }
            
            // 验证文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body(ApiResponse.error("只能上传图片文件"));
            }
            
            // 存储文件
            String filePath = fileStorageService.store(file, "avatars");
            log.info("用户头像上传成功, 用户ID: {}, 文件路径: {}", id, filePath);
            
            // 更新用户头像
            UserDTO updatedUser = userService.updateUserAvatar(id, "/api/files/view/avatars/" + filePath);
            return ResponseEntity.ok(ApiResponse.success("头像上传成功", updatedUser));
        } catch (Exception e) {
            log.error("头像上传失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("头像上传失败: " + e.getMessage()));
        }
    }
    
    /**
     * 批量创建用户
     *
     * @param userDTOs 用户数据列表
     * @return 创建后的用户列表
     */
    @PostMapping("/batch")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<UserDTO>>> batchCreateUsers(@Valid @RequestBody List<UserDTO> userDTOs) {
        log.info("批量创建用户，数量：{}", userDTOs.size());
        List<UserDTO> createdUsers = userService.batchCreateUsers(userDTOs);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("批量创建用户成功", createdUsers));
    }
    
    /**
     * 批量删除用户
     *
     * @param ids 用户ID列表
     * @return 响应
     */
    @DeleteMapping("/batch")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> batchDeleteUsers(@RequestBody List<Long> ids) {
        log.info("批量删除用户，数量：{}", ids.size());
        int count = userService.batchDeleteUsers(ids);
        return ResponseEntity.ok(ApiResponse.success("批量删除用户成功", Map.of("count", count)));
    }
    
    /**
     * 批量启用用户
     *
     * @param ids 用户ID列表
     * @return 响应
     */
    @PostMapping("/batch/enable")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> batchEnableUsers(@RequestBody List<Long> ids) {
        log.info("批量启用用户，数量：{}", ids.size());
        int count = userService.batchEnableUsers(ids);
        return ResponseEntity.ok(ApiResponse.success("批量启用用户成功", Map.of("count", count)));
    }
    
    /**
     * 批量禁用用户
     *
     * @param ids 用户ID列表
     * @return 响应
     */
    @PostMapping("/batch/disable")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> batchDisableUsers(@RequestBody List<Long> ids) {
        log.info("批量禁用用户，数量：{}", ids.size());
        int count = userService.batchDisableUsers(ids);
        return ResponseEntity.ok(ApiResponse.success("批量禁用用户成功", Map.of("count", count)));
    }
    
    /**
     * 批量分配角色
     *
     * @param request 请求数据，包含用户ID列表和角色ID列表
     * @return 响应
     */
    @PostMapping("/batch/roles")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> batchAssignRoles(
            @RequestBody Map<String, List<Long>> request) {
        List<Long> userIds = request.get("userIds");
        List<Long> roleIds = request.get("roleIds");
        
        if (userIds == null || userIds.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("用户ID列表不能为空"));
        }
        
        if (roleIds == null || roleIds.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("角色ID列表不能为空"));
        }
        
        log.info("批量分配角色，用户数量：{}，角色数量：{}", userIds.size(), roleIds.size());
        int count = userService.batchAssignRoles(userIds, roleIds);
        return ResponseEntity.ok(ApiResponse.success("批量分配角色成功", Map.of("count", count)));
    }
    
    /**
     * 解锁用户账户
     *
     * @param userId 用户ID
     * @return 解锁结果
     */
    @PostMapping("/{userId}/unlock")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<UserDTO>> unlockUserAccount(@PathVariable Long userId) {
        log.info("解锁用户账户, 用户ID: {}", userId);
        try {
            UserDTO userDTO = userService.unlockUserAccount(userId);
            return ResponseEntity.ok(ApiResponse.success("用户账户解锁成功", userDTO));
        } catch (SecurityException e) {
            log.warn("解锁用户账户失败: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("解锁用户账户失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("解锁用户账户失败: " + e.getMessage()));
        }
    }
    
    /**
     * 批量解锁用户账户
     *
     * @param request 包含用户ID列表的请求体
     * @return 解锁结果
     */
    @PostMapping("/batch/unlock")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> batchUnlockUserAccounts(
            @RequestBody Map<String, List<Long>> request) {
        List<Long> userIds = request.get("userIds");
        
        if (userIds == null || userIds.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("用户ID列表不能为空"));
        }
        
        log.info("批量解锁用户账户，用户数量：{}", userIds.size());
        
        int successCount = 0;
        for (Long userId : userIds) {
            try {
                userService.unlockUserAccount(userId);
                successCount++;
            } catch (Exception e) {
                log.warn("解锁用户[ID:{}]账户失败: {}", userId, e.getMessage());
            }
        }
        
        return ResponseEntity.ok(ApiResponse.success("批量解锁用户账户成功", Map.of("count", successCount)));
    }
} 