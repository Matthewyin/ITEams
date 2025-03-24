package com.iteams.service;

import com.iteams.model.dto.PasswordDTO;
import com.iteams.model.dto.UserDTO;
import com.iteams.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService extends UserDetailsService {

    /**
     * 获取用户分页列表
     *
     * @param username   用户名（模糊查询）
     * @param realName   姓名（模糊查询）
     * @param department 部门（模糊查询）
     * @param pageable   分页参数
     * @return 用户分页列表
     */
    Page<UserDTO> getUsers(String username, String realName, String department, Pageable pageable);

    /**
     * 根据ID获取用户
     *
     * @param id 用户ID
     * @return 用户
     */
    UserDTO getUserById(Long id);

    /**
     * 根据用户名获取用户
     *
     * @param username 用户名
     * @return 用户
     */
    User getUserByUsername(String username);

    /**
     * 创建用户
     *
     * @param userDTO 用户数据
     * @return 创建后的用户
     */
    UserDTO createUser(UserDTO userDTO);

    /**
     * 更新用户
     *
     * @param id      用户ID
     * @param userDTO 用户数据
     * @return 更新后的用户
     */
    UserDTO updateUser(Long id, UserDTO userDTO);

    /**
     * 更新当前用户信息
     *
     * @param userDTO 用户数据
     * @return 更新后的用户
     */
    UserDTO updateCurrentUser(UserDTO userDTO);

    /**
     * 删除用户
     *
     * @param id 用户ID
     */
    void deleteUser(Long id);

    /**
     * 重置用户密码
     *
     * @param id 用户ID
     * @return 重置后的随机密码
     */
    String resetPassword(Long id);

    /**
     * 修改当前用户密码
     *
     * @param passwordDTO 密码数据
     */
    void changePassword(PasswordDTO passwordDTO);

    /**
     * 分配用户角色
     *
     * @param userId  用户ID
     * @param roleIds 角色ID列表
     */
    void assignRoles(Long userId, List<Long> roleIds);
    
    /**
     * 更新用户头像
     *
     * @param userId 用户ID
     * @param avatarUrl 头像URL
     * @return 更新后的用户
     */
    UserDTO updateUserAvatar(Long userId, String avatarUrl);
    
    /**
     * 更新当前用户头像
     *
     * @param avatarUrl 头像URL
     * @return 更新后的用户
     */
    UserDTO updateCurrentUserAvatar(String avatarUrl);
    
    /**
     * 批量创建用户
     *
     * @param userDTOs 用户数据列表
     * @return 创建后的用户列表
     */
    List<UserDTO> batchCreateUsers(List<UserDTO> userDTOs);
    
    /**
     * 批量删除用户
     *
     * @param ids 用户ID列表
     * @return 成功删除的用户数量
     */
    int batchDeleteUsers(List<Long> ids);
    
    /**
     * 批量启用用户
     *
     * @param ids 用户ID列表
     * @return 成功启用的用户数量
     */
    int batchEnableUsers(List<Long> ids);
    
    /**
     * 批量禁用用户
     *
     * @param ids 用户ID列表
     * @return 成功禁用的用户数量
     */
    int batchDisableUsers(List<Long> ids);
    
    /**
     * 批量分配角色
     *
     * @param userIds 用户ID列表
     * @param roleIds 角色ID列表
     * @return 成功分配角色的用户数量
     */
    int batchAssignRoles(List<Long> userIds, List<Long> roleIds);
    
    /**
     * 解锁用户账户
     *
     * @param userId 用户ID
     * @return 解锁后的用户
     */
    UserDTO unlockUserAccount(Long userId);
} 