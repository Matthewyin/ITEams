package com.iteams.repository;

import com.iteams.model.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.lang.NonNull;

/**
 * 角色数据访问接口
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * 根据角色编码查找角色
     *
     * @param code 角色编码
     * @return 角色
     */
    Optional<Role> findByCode(String code);

    /**
     * 检查角色编码是否存在
     *
     * @param code 角色编码
     * @return 是否存在
     */
    boolean existsByCode(String code);

    /**
     * 分页查询角色列表（带条件）
     *
     * @param name 角色名称（模糊查询）
     * @param pageable 分页参数
     * @return 角色分页列表
     */
    @Query("SELECT r FROM Role r WHERE (:name IS NULL OR r.name LIKE %:name%)")
    Page<Role> findByConditions(@Param("name") String name, Pageable pageable);

    /**
     * 查询所有角色（不分页）
     *
     * @return 角色列表
     */
    @Override
    @NonNull
    List<Role> findAll();
} 