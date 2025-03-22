package com.iteams.controller;

import com.iteams.model.dto.ApiResponse;
import com.iteams.model.dto.PageResult;
import com.iteams.model.entity.User;
import com.iteams.model.enums.ModuleType;
import com.iteams.model.enums.OperationType;
import com.iteams.service.LogService;
import com.iteams.service.UserService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户管理控制器
 */
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final LogService logService;

    @Autowired
    public UserController(UserService userService, LogService logService) {
        this.userService = userService;
        this.logService = logService;
    }


    /**
     * 获取用户列表
     *
     * @param query 查询关键字
     * @param role 角色筛选
     * @param status 状态筛选
     * @param page 页码
     * @param limit 每页数量
     * @return 分页用户列表
     */
    @GetMapping
    @PreAuthorize("hasAuthority('user:view')")
    public ResponseEntity<ApiResponse<PageResult<User>>> getUsers(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, "createdTime"));
        Page<User> userPage = userService.getUsers(query, role, status, pageable);
        
        PageResult<User> pageResult = new PageResult<>();
        pageResult.setItems(userPage.getContent());
        pageResult.setTotal(userPage.getTotalElements());
        
        return ResponseEntity.ok(new ApiResponse<>(true, "获取用户列表成功", pageResult));
    }
    
    /**
     * 获取用户详情
     *
     * @param id 用户ID
     * @return 用户详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('user:view')")
    public ResponseEntity<ApiResponse<User>> getUser(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(user -> ResponseEntity.ok(new ApiResponse<>(true, "获取用户详情成功", user)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "用户不存在", null)));
    }
    
    /**
     * 创建用户
     *
     * @param user 用户数据
     * @param principal 当前登录用户
     * @return 创建结果
     */
    @PostMapping
    @PreAuthorize("hasAuthority('user:create')")
    public ResponseEntity<ApiResponse<User>> createUser(
            @Valid @RequestBody User user,
            Principal principal) {
        
        // 检查用户名是否已存在
        if (userService.existsByUsername(user.getUsername())) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "用户名已存在", null));
        }

        
        User createdUser = userService.createUser(user);
        
        // 记录操作日志
        logService.logOperation(
                principal.getName(),
                ModuleType.USER,
                OperationType.CREATE,
                "创建用户: " + user.getUsername()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "用户创建成功", createdUser));
    }
    
    /**
     * 更新用户
     *
     * @param id 用户ID
     * @param user 用户数据
     * @param principal 当前登录用户
     * @return 更新结果
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('user:edit')")
    public ResponseEntity<ApiResponse<User>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody User user,
            Principal principal) {
        

        
        User updatedUser = userService.updateUser(id, user);
        
        // 记录操作日志
        logService.logOperation(
                principal.getName(),
                ModuleType.USER,
                OperationType.UPDATE,
                "更新用户: " + updatedUser.getUsername()
        );
        
        return ResponseEntity.ok(new ApiResponse<>(true, "用户更新成功", updatedUser));
    }
    
    /**
     * 删除用户
     *
     * @param id 用户ID
     * @param principal 当前登录用户
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('user:delete')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @PathVariable Long id,
            Principal principal) {
        
        // 获取要删除的用户名
        String username = userService.getUserById(id)
                .map(User::getUsername)
                .orElse("未知用户");
        
        userService.deleteUser(id);
        
        // 记录操作日志
        logService.logOperation(
                principal.getName(),
                ModuleType.USER,
                OperationType.DELETE,
                "删除用户: " + username
        );
        
        return ResponseEntity.ok(new ApiResponse<>(true, "用户删除成功", null));
    }
    
    /**
     * 重置用户密码
     *
     * @param id 用户ID
     * @param principal 当前登录用户
     * @return 重置结果
     */
    @PostMapping("/reset-password/{id}")
    @PreAuthorize("hasAuthority('user:reset-password')")
    public ResponseEntity<ApiResponse<Map<String, String>>> resetPassword(
            @PathVariable Long id,
            Principal principal) {
        
        String newPassword = userService.resetPassword(id);
        
        // 获取用户名
        String username = userService.getUserById(id)
                .map(User::getUsername)
                .orElse("未知用户");
        
        // 记录操作日志
        logService.logOperation(
                principal.getName(),
                ModuleType.USER,
                OperationType.UPDATE,
                "重置用户密码: " + username
        );
        
        Map<String, String> result = new HashMap<>();
        result.put("password", newPassword);
        
        return ResponseEntity.ok(new ApiResponse<>(true, "密码重置成功", result));
    }
    
    /**
     * 获取所有角色列表
     *
     * @return 角色列表
     */
    @GetMapping("/roles")
    @PreAuthorize("hasAuthority('user:view')")
    public ResponseEntity<ApiResponse<List<UserService.RoleInfo>>> getRoles() {
        List<UserService.RoleInfo> roles = userService.getAllRoles();
        return ResponseEntity.ok(new ApiResponse<>(true, "获取角色列表成功", roles));
    }
} 