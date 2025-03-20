package com.iteams.repository;

import com.iteams.model.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户角色关联仓库接口
 */
@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UserRole.UserRoleId> {
    
    /**
     * 根据用户ID查询用户角色关联列表
     *
     * @param userId 用户ID
     * @return 用户角色关联列表
     */
    List<UserRole> findByUserId(Long userId);
    
    /**
     * 根据角色ID查询用户角色关联列表
     *
     * @param roleId 角色ID
     * @return 用户角色关联列表
     */
    List<UserRole> findByRoleId(Long roleId);
    
    /**
     * 根据用户ID删除用户角色关联
     *
     * @param userId 用户ID
     * @return 影响行数
     */
    @Modifying
    @Query("DELETE FROM UserRole ur WHERE ur.userId = :userId")
    int deleteByUserId(Long userId);
    
    /**
     * 根据角色ID删除用户角色关联
     *
     * @param roleId 角色ID
     * @return 影响行数
     */
    @Modifying
    @Query("DELETE FROM UserRole ur WHERE ur.roleId = :roleId")
    int deleteByRoleId(Long roleId);
    
    /**
     * 检查用户是否拥有指定角色
     *
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 是否存在
     */
    boolean existsByUserIdAndRoleId(Long userId, Long roleId);
    
    /**
     * 统计角色的用户数量
     *
     * @param roleId 角色ID
     * @return 用户数量
     */
    long countByRoleId(Long roleId);
} 