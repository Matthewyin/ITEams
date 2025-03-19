package com.iteams.model.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户信息DTO
 */
@Data
public class UserInfoDTO {

    private Long id;
    private String username;
    private String realName;
    private String email;
    private String phone;
    private String avatarUrl;
    private LocalDateTime lastLoginTime;
    private List<String> roles = new ArrayList<>();
    private List<String> permissions = new ArrayList<>();
}
