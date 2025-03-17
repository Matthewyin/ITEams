package com.iteams.repository;

import com.iteams.model.entity.CategoryMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 资产分类元数据访问接口
 * <p>
 * 该接口扩展了Spring Data JPA的{@link JpaRepository}，提供对{@link CategoryMetadata}实体的基本CRUD操作，
 * 以及针对资产分类管理特定需求的自定义查询方法。
 * </p>
 * <p>
 * 分类元数据表存储资产分类的层次结构信息，支持最多三级分类（大类、中类、小类），
 * 每个分类节点包含名称、代码、层级等信息。系统通过这些分类信息对资产进行组织和管理。
 * </p>
 * <p>
 * 核心功能包括：
 * <ul>
 *   <li>按名称和路径查询分类节点</li>
 *   <li>获取指定父节点的所有子分类</li>
 *   <li>验证分类路径的有效性</li>
 *   <li>按层级获取分类列表</li>
 * </ul>
 * </p>
 */
@Repository
public interface CategoryRepository extends JpaRepository<CategoryMetadata, Long> {
    
    /**
     * 根据分类名称查询分类信息
     * <p>
     * 分类名称在同一父节点下必须唯一，系统允许不同父节点下存在同名分类。
     * 该方法用于验证新增分类时名称的唯一性，以及通过名称快速定位分类信息。
     * </p>
     *
     * @param name 分类名称，不可为null
     * @return 包含分类信息的Optional对象，如果未找到则为empty
     */
    Optional<CategoryMetadata> findByName(String name);
    
    /**
     * 根据分类路径查询分类信息
     * <p>
     * 分类路径是从根节点到当前节点的完整路径，格式为"根分类/二级分类/三级分类"。
     * 路径在整个分类体系中必须唯一，用于Excel导入时验证分类的有效性。
     * </p>
     *
     * @param path 分类路径，格式为"根分类/二级分类/三级分类"，不可为null
     * @return 包含分类信息的Optional对象，如果未找到则为empty
     */
    @Query(value = "WITH RECURSIVE category_path AS (" +
           "SELECT c.category_id, c.name, c.level, CAST(c.name AS CHAR(255)) AS path " +
           "FROM category_metadata c WHERE c.parent_id IS NULL " +
           "UNION ALL " +
           "SELECT c.category_id, c.name, c.level, CONCAT(cp.path, '/', c.name) " +
           "FROM category_metadata c JOIN category_path cp ON c.parent_id = cp.category_id" +
           ") " +
           "SELECT c.* FROM category_metadata c JOIN category_path cp ON c.category_id = cp.category_id " +
           "WHERE cp.path = ?1 LIMIT 1", nativeQuery = true)
    Optional<CategoryMetadata> findByPath(String path);
    
    /**
     * 查询指定父节点的所有直接子分类
     * <p>
     * 获取特定父分类下的所有直接子分类，用于构建分类树形结构和级联选择器。
     * </p>
     *
     * @param parentId 父分类ID，如果为null则查询顶级分类（无父节点的分类）
     * @return 子分类列表，如果没有子分类则返回空列表
     */
    @Query("SELECT c FROM CategoryMetadata c WHERE c.parent.categoryId = ?1")
    List<CategoryMetadata> findByParentId(Long parentId);
    
    /**
     * 根据分类层级查询分类列表
     * <p>
     * 获取指定层级的所有分类节点，层级从1开始（1=一级分类，2=二级分类，3=三级分类）。
     * 用于统计报表和按层级筛选分类。
     * </p>
     *
     * @param level 分类层级，有效值为1、2、3
     * @return 该层级的所有分类列表
     */
    @Query("SELECT c FROM CategoryMetadata c WHERE c.level = ?1")
    List<CategoryMetadata> findByLevel(Integer level);
    
    /**
     * 验证分类路径是否有效
     * <p>
     * 该方法根据分类的完整路径检查是否存在匹配的分类。
     * 使用动态生成的fullPath进行比较，而不是直接存储的属性。
     * </p>
     *
     * @param path 待验证的分类路径
     * @return 路径存在返回1，否则返回0
     */
    @Query(value = "WITH RECURSIVE category_path AS (" +
           "SELECT c.category_id, c.name, c.level, CAST(c.name AS CHAR(255)) AS path " +
           "FROM category_metadata c WHERE c.parent_id IS NULL " +
           "UNION ALL " +
           "SELECT c.category_id, c.name, c.level, CONCAT(cp.path, '/', c.name) " +
           "FROM category_metadata c JOIN category_path cp ON c.parent_id = cp.category_id" +
           ") " +
           "SELECT COUNT(*) FROM category_path WHERE path = ?1", nativeQuery = true)
    Integer validatePath(String path);
    
    /**
     * 查询指定父分类下的所有子分类
     * <p>
     * 根据父分类ID查询其直接子分类，用于构建分类级联选择和分类树。
     * 例如，查询一级分类下的所有二级分类，或二级分类下的所有三级分类。
     * </p>
     * 
     * @param parentId 父分类ID
     * @return 该父分类下的所有子分类列表
     */
    @Query("SELECT c FROM CategoryMetadata c WHERE c.parent.categoryId = ?1")
    List<CategoryMetadata> findByParentCategoryId(Long parentId);
    
    /**
     * 根据分类ID和层级查询分类
     * <p>
     * 同时指定分类ID和层级进行精确查询，确保查询到的分类
     * 不仅ID匹配，而且层级也正确，增强查询的准确性和安全性。
     * </p>
     * 
     * @param categoryId 分类ID
     * @param level 分类层级，1-一级，2-二级，3-三级
     * @return 包含分类信息的Optional对象，如果不存在则为empty
     */
    Optional<CategoryMetadata> findByCategoryIdAndLevel(Long categoryId, Byte level);
    
    /**
     * 检查指定ID和层级的分类是否存在
     * <p>
     * 使用计数查询优化的方法验证给定的分类ID和层级组合是否存在，
     * 相比于findByCategoryIdAndLevel方法更高效，因为只返回布尔值而非实体对象。
     * </p>
     * 
     * @param categoryId 要验证的分类ID
     * @param level 要验证的分类层级
     * @return 如果存在返回true，否则返回false
     */
    @Query("SELECT COUNT(c) > 0 FROM CategoryMetadata c WHERE c.categoryId = :categoryId AND c.level = :level")
    boolean existsByCategoryIdAndLevel(@Param("categoryId") Long categoryId, @Param("level") Byte level);
    
    /**
     * 通过分类名称和级别查找分类
     * <p>
     * 根据分类名称和层级查询分类信息，主要用于Excel导入时通过分类名称找到对应的分类ID。
     * 由于分类名称在同一层级内应唯一，因此使用Optional返回值。
     * </p>
     * 
     * @param name 分类名称，如"服务器设备"
     * @param level 分类级别，1-一级，2-二级，3-三级
     * @return 包含分类信息的Optional对象，如果不存在则为empty
     */
    Optional<CategoryMetadata> findByNameAndLevel(String name, Byte level);
} 