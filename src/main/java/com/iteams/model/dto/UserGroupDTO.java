package com.iteams.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户组数据传输对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserGroupDTO {

    /**
     * 用户组ID
     */
    private Long id;

    /**
     * 用户组名称
     */
    private String name;

    /**
     * 用户组代码
     */
    private String code;

    /**
     * 用户组描述
     */
    private String description;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 用户组中的用户ID列表
     */
    private List<Long> userIds;

    /**
     * 用户组中的用户数量
     */
    private Integer userCount;
}
