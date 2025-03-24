package com.iteams.service;

import com.iteams.model.dto.UserGroupDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 用户组服务接口
 */
public interface UserGroupService {

    /**
     * 获取用户组分页列表
     *
     * @param name 用户组名称（模糊查询）
     * @param code 用户组代码（模糊查询）
     * @param enabled 启用状态
     * @param pageable 分页参数
     * @return 用户组分页列表
     */
    Page<UserGroupDTO> getUserGroups(String name, String code, Boolean enabled, Pageable pageable);

    /**
     * 获取所有用户组列表
     *
     * @param enabled 启用状态，为null时获取全部
     * @return 用户组列表
     */
    List<UserGroupDTO> getAllUserGroups(Boolean enabled);

    /**
     * 根据ID获取用户组
     *
     * @param id 用户组ID
     * @return 用户组
     */
    UserGroupDTO getUserGroupById(Long id);

    /**
     * 创建用户组
     *
     * @param userGroupDTO 用户组DTO
     * @return 创建后的用户组
     */
    UserGroupDTO createUserGroup(UserGroupDTO userGroupDTO);

    /**
     * 更新用户组
     *
     * @param id 用户组ID
     * @param userGroupDTO 用户组DTO
     * @return 更新后的用户组
     */
    UserGroupDTO updateUserGroup(Long id, UserGroupDTO userGroupDTO);

    /**
     * 删除用户组
     *
     * @param id 用户组ID
     */
    void deleteUserGroup(Long id);

    /**
     * 启用用户组
     *
     * @param id 用户组ID
     * @return 启用后的用户组
     */
    UserGroupDTO enableUserGroup(Long id);

    /**
     * 禁用用户组
     *
     * @param id 用户组ID
     * @return 禁用后的用户组
     */
    UserGroupDTO disableUserGroup(Long id);

    /**
     * 批量删除用户组
     *
     * @param ids 用户组ID列表
     */
    void batchDeleteUserGroups(List<Long> ids);

    /**
     * 批量启用用户组
     *
     * @param ids 用户组ID列表
     */
    void batchEnableUserGroups(List<Long> ids);

    /**
     * 批量禁用用户组
     *
     * @param ids 用户组ID列表
     */
    void batchDisableUserGroups(List<Long> ids);

    /**
     * 为用户组分配用户
     *
     * @param groupId 用户组ID
     * @param userIds 用户ID列表
     * @return 更新后的用户组
     */
    UserGroupDTO assignUsers(Long groupId, List<Long> userIds);

    /**
     * 从用户组移除用户
     *
     * @param groupId 用户组ID
     * @param userIds 用户ID列表
     * @return 更新后的用户组
     */
    UserGroupDTO removeUsers(Long groupId, List<Long> userIds);
}
