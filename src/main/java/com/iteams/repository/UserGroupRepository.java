package com.iteams.repository;

import com.iteams.model.entity.UserGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户组Repository
 */
@Repository
public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {

    /**
     * 根据用户组名称查找用户组
     *
     * @param name 用户组名称
     * @return 用户组
     */
    Optional<UserGroup> findByName(String name);

    /**
     * 根据用户组代码查找用户组
     *
     * @param code 用户组代码
     * @return 用户组
     */
    Optional<UserGroup> findByCode(String code);

    /**
     * 判断用户组名称是否存在
     *
     * @param name 用户组名称
     * @return 是否存在
     */
    boolean existsByName(String name);

    /**
     * 判断用户组代码是否存在
     *
     * @param code 用户组代码
     * @return 是否存在
     */
    boolean existsByCode(String code);

    /**
     * 根据启用状态查找用户组
     *
     * @param enabled 启用状态
     * @return 用户组列表
     */
    List<UserGroup> findByEnabled(Boolean enabled);

    /**
     * 根据名称、代码和启用状态分页查询用户组
     *
     * @param name 用户组名称（模糊查询）
     * @param code 用户组代码（模糊查询）
     * @param enabled 启用状态
     * @param pageable 分页参数
     * @return 用户组分页结果
     */
    @Query("SELECT g FROM UserGroup g WHERE " +
            "(:name IS NULL OR g.name LIKE %:name%) AND " +
            "(:code IS NULL OR g.code LIKE %:code%) AND " +
            "(:enabled IS NULL OR g.enabled = :enabled)")
    Page<UserGroup> findByConditions(
            @Param("name") String name,
            @Param("code") String code,
            @Param("enabled") Boolean enabled,
            Pageable pageable);
}
