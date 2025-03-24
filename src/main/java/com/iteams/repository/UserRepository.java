package com.iteams.repository;

import com.iteams.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户数据访问接口
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 根据用户名查找用户
     *
     * @param username 用户名
     * @return 用户
     */
    Optional<User> findByUsername(String username);

    /**
     * 检查用户名是否存在
     *
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 根据邮箱查找用户
     *
     * @param email 邮箱
     * @return 用户
     */
    Optional<User> findByEmail(String email);

    /**
     * 分页查询用户列表（带条件）
     *
     * @param username 用户名（模糊查询）
     * @param realName 姓名（模糊查询）
     * @param departmentId 部门ID
     * @param pageable 分页参数
     * @return 用户分页列表
     */
    @Query("SELECT u FROM User u WHERE " +
            "(:username IS NULL OR u.username LIKE %:username%) AND " +
            "(:realName IS NULL OR u.realName LIKE %:realName%) AND " +
            "(:departmentId IS NULL OR u.department.id = :departmentId)")
    Page<User> findByConditions(
            @Param("username") String username,
            @Param("realName") String realName,
            @Param("departmentId") Long departmentId,
            Pageable pageable);
    

    /**
     * 根据部门ID统计用户数量
     *
     * @param departmentId 部门ID
     * @return 用户数量
     */
    long countByDepartmentId(Long departmentId);
    
    /**
     * 根据用户组ID查找用户列表
     *
     * @param groupId 用户组ID
     * @return 用户列表
     */
    @Query("SELECT u FROM User u JOIN u.groups g WHERE g.id = :groupId")
    List<User> findByGroupId(@Param("groupId") Long groupId);
    
    /**
     * 根据用户组ID统计用户数量
     *
     * @param groupId 用户组ID
     * @return 用户数量
     */
    @Query("SELECT COUNT(u) FROM User u JOIN u.groups g WHERE g.id = :groupId")
    long countByGroupId(@Param("groupId") Long groupId);
}