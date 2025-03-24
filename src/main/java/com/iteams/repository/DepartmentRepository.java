package com.iteams.repository;

import com.iteams.model.entity.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 部门Repository
 */
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    /**
     * 根据部门名称查找部门
     *
     * @param name 部门名称
     * @return 部门
     */
    Optional<Department> findByName(String name);

    /**
     * 根据部门代码查找部门
     *
     * @param code 部门代码
     * @return 部门
     */
    Optional<Department> findByCode(String code);

    /**
     * 判断部门名称是否存在
     *
     * @param name 部门名称
     * @return 是否存在
     */
    boolean existsByName(String name);

    /**
     * 判断部门代码是否存在
     *
     * @param code 部门代码
     * @return 是否存在
     */
    boolean existsByCode(String code);

    /**
     * 根据父部门ID查找子部门
     *
     * @param parentId 父部门ID
     * @return 子部门列表
     */
    List<Department> findByParentId(Long parentId);

    /**
     * 根据父部门ID和启用状态查找子部门
     *
     * @param parentId 父部门ID
     * @param enabled 启用状态
     * @return 子部门列表
     */
    List<Department> findByParentIdAndEnabled(Long parentId, Boolean enabled);

    /**
     * 查找顶级部门（parentId为null的部门）
     *
     * @return 顶级部门列表
     */
    List<Department> findByParentIdIsNull();

    /**
     * 根据启用状态查找顶级部门
     *
     * @param enabled 启用状态
     * @return 顶级部门列表
     */
    List<Department> findByParentIdIsNullAndEnabled(Boolean enabled);

    /**
     * 根据名称、代码和启用状态分页查询部门
     *
     * @param name 部门名称（模糊查询）
     * @param code 部门代码（模糊查询）
     * @param enabled 启用状态
     * @param pageable 分页参数
     * @return 部门分页结果
     */
    @Query("SELECT d FROM Department d WHERE " +
            "(:name IS NULL OR d.name LIKE %:name%) AND " +
            "(:code IS NULL OR d.code LIKE %:code%) AND " +
            "(:enabled IS NULL OR d.enabled = :enabled)")
    Page<Department> findByConditions(
            @Param("name") String name,
            @Param("code") String code,
            @Param("enabled") Boolean enabled,
            Pageable pageable);
}
