package com.iteams.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 权限DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionDTO {

    private Long id;

    @NotBlank(message = "权限名称不能为空")
    @Size(max = 50, message = "权限名称长度不能超过50个字符")
    private String name;

    @NotBlank(message = "权限编码不能为空")
    @Size(max = 50, message = "权限编码长度不能超过50个字符")
    @Pattern(regexp = "^[A-Z_]+$", message = "权限编码只能包含大写字母和下划线")
    private String code;

    @Size(max = 200, message = "权限描述长度不能超过200个字符")
    private String description;

    @Size(max = 20, message = "权限类型长度不能超过20个字符")
    private String type;
} 