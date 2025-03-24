package com.iteams.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * 用户DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Long id;

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50个字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    private String username;

    private String password;

    @NotBlank(message = "姓名不能为空")
    @Size(max = 50, message = "姓名长度不能超过50个字符")
    private String realName;

    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String email;
    
    private String phone;

    /**
     * 部门ID
     */
    private Long departmentId;
    
    /**
     * 部门名称（仅用于显示）
     */
    private String departmentName;
    
    /**
     * 头像URL
     */
    private String avatarUrl;

    /**
     * 用户状态，1表示启用，0表示禁用
     */
    private Integer status;

    @Builder.Default
    private Set<String> roles = new HashSet<>();
    
    /**
     * 用户所属用户组ID列表
     */
    @Builder.Default
    private Set<Long> groupIds = new HashSet<>();
    
    /**
     * 用户所属用户组名称列表（仅用于显示）
     */
    @Builder.Default
    private Set<String> groupNames = new HashSet<>();
} 