package com.iteams.repository;

import com.iteams.model.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 角色仓库接口
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {
    
    /**
     * 根据角色编码查询角色
     *
     * @param roleCode 角色编码
     * @return 角色
     */
    Optional<Role> findByRoleCode(String roleCode);
    
    /**
     * 检查角色编码是否存在
     *
     * @param roleCode 角色编码
     * @return 是否存在
     */
    boolean existsByRoleCode(String roleCode);
    
    /**
     * 获取角色列表，排除超级管理员
     *
     * @return 角色列表
     */
    @Query("SELECT r FROM Role r WHERE r.roleCode <> 'superadmin' ORDER BY r.sortOrder")
    List<Role> findAllExceptSuperAdmin();
    
    /**
     * 根据状态查询角色列表
     *
     * @param status 状态
     * @return 角色列表
     */
    List<Role> findByStatus(Integer status);
    
    /**
     * 根据用户ID查询用户的角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    @Query("SELECT r FROM Role r INNER JOIN UserRole ur ON r.id = ur.roleId WHERE ur.userId = :userId")
    List<Role> findByUserId(Long userId);
} 