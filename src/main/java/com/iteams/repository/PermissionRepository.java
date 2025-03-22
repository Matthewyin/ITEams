package com.iteams.repository;

import com.iteams.model.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 权限数据访问接口
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    /**
     * 根据权限编码查找权限
     *
     * @param code 权限编码
     * @return 权限
     */
    Optional<Permission> findByCode(String code);

    /**
     * 检查权限编码是否存在
     *
     * @param code 权限编码
     * @return 是否存在
     */
    boolean existsByCode(String code);

    /**
     * 查询所有权限（排序）
     *
     * @return 权限列表
     */
    @Query("SELECT p FROM Permission p ORDER BY p.code")
    List<Permission> findAllOrderByCode();

    /**
     * 根据角色ID查询权限
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    @Query("SELECT p FROM Permission p JOIN p.roles r WHERE r.id = :roleId")
    Set<Permission> findByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据角色ID集合查询权限
     *
     * @param roleIds 角色ID集合
     * @return 权限列表
     */
    @Query("SELECT DISTINCT p FROM Permission p JOIN p.roles r WHERE r.id IN :roleIds")
    Set<Permission> findByRoleIds(@Param("roleIds") List<Long> roleIds);
} 