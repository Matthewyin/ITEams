package com.iteams.repository;

import com.iteams.model.entity.AssetMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 资产主表数据访问接口
 * <p>
 * 负责资产主表(asset_master)的数据库操作，提供资产信息的增删改查功能。
 * 资产主表是系统的核心实体，存储所有IT资产的基本信息和关联关系。
 * </p>
 * <p>
 * 该仓库提供了多种方式查询资产：
 * <ul>
 *   <li>通过业务标识查询（资产编号、UUID）</li>
 *   <li>通过导入批次号查询</li>
 *   <li>通过Excel行哈希值查询（用于去重）</li>
 *   <li>通过位置路径查询（关联空间表）</li>
 *   <li>通过分类统计资产数量</li>
 * </ul>
 * </p>
 */
@Repository
public interface AssetRepository extends JpaRepository<AssetMaster, Long> {

    /**
     * 根据资产编号查询资产
     * <p>
     * 资产编号是业务系统中的唯一标识，通常采用特定规则生成，
     * 格式如"IT01HR2403xxxx"（分类码+部门码+年月+序号）。
     * </p>
     * 
     * @param assetNo 资产编号，不能为null
     * @return 包含资产信息的Optional对象，如果不存在则为empty
     */
    Optional<AssetMaster> findByAssetNo(String assetNo);

    /**
     * 根据资产UUID查询资产
     * <p>
     * 资产UUID是系统内部的唯一标识，格式如"AST20240301-xxxxxxxx"。
     * 与资产编号不同，UUID主要用于系统内部的关联和跟踪。
     * </p>
     * 
     * @param assetUuid 资产UUID，不能为null
     * @return 包含资产信息的Optional对象，如果不存在则为empty
     */
    Optional<AssetMaster> findByAssetUuid(String assetUuid);

    /**
     * 查询指定批次导入的所有资产
     * <p>
     * 通过导入批次号关联查询所有在同一批次中导入的资产记录。
     * 批次号格式如"IMPORT-20240301120530-a1b2c3"，由导入服务生成。
     * </p>
     * 
     * @param batchId 导入批次号
     * @return 同批次导入的资产列表
     */
    @Query("SELECT a FROM AssetMaster a WHERE a.importBatch = ?1")
    List<AssetMaster> findByImportBatch(String batchId);

    /**
     * 根据Excel行哈希值查询资产
     * <p>
     * 用于Excel导入时的重复检测。系统会为每行Excel数据计算SHA-256哈希值，
     * 用于快速识别是否为重复导入的数据，即使资产编号不同。
     * </p>
     * 
     * @param rowHash Excel行数据的SHA-256哈希值
     * @return 包含资产信息的Optional对象，如果不存在则为empty
     */
    @Query("SELECT a FROM AssetMaster a WHERE a.excelRowHash = ?1")
    Optional<AssetMaster> findByExcelRowHash(String rowHash);

    /**
     * 根据位置路径模糊查询资产
     * <p>
     * 关联空间轨迹表，根据位置路径的部分匹配查询资产。
     * 例如，可以查询所有在"华东数据中心"的资产，而不需要指定完整路径。
     * 只会返回当前位置（is_current=1）匹配的资产。
     * </p>
     * 
     * @param locationPath 位置路径的部分或全部，如"华东数据中心"
     * @return 位置匹配的资产列表
     */
    @Query(value = "SELECT a.* FROM asset_master a INNER JOIN space_timeline s ON a.space_id = s.space_id " +
            "WHERE s.location_path LIKE %?1% AND s.is_current = 1", nativeQuery = true)
    List<AssetMaster> findByLocationPathContaining(String locationPath);

    /**
     * 统计指定一级分类下的资产数量
     * <p>
     * 通过JSON提取函数从category_hierarchy字段获取一级分类ID，
     * 并统计该分类下的资产总数。用于分类统计和报表生成。
     * </p>
     * 
     * @param categoryId 一级分类ID
     * @return 该分类下的资产数量
     */
    @Query("SELECT COUNT(a) FROM AssetMaster a WHERE CAST(json_extract(a.categoryHierarchy, '$.l1') AS integer) = ?1")
    Long countByTopCategory(Long categoryId);

    /**
     * 获取指定类别和部门下的最大序列号
     * <p>
     * 该方法通过JPQL查询获取特定类别和部门代码的资产中最大的序列号，
     * 主要用于生成新资产编号时确定序列号部分的值。
     * </p>
     * <p>
     * 查询会分析满足特定格式的资产编号，提取其中的序列号部分并返回最大值。
     * 如果没有匹配的资产，则返回0。
     * </p>
     *
     * @param categoryCode 资产类别代码，如"COMPUTER"、"SERVER"等
     * @param departmentCode 部门代码，如"IT"、"HR"等
     * @param yearMonth 年月，格式为"YYYYMM"
     * @return 最大序列号，如果没有匹配的资产则返回0
     */
    @Query("SELECT COALESCE(MAX(CAST(SUBSTRING(a.assetNo, LENGTH(:categoryCode) + LENGTH(:departmentCode) + LENGTH(:yearMonth) + 4) AS LONG)), 0) " +
           "FROM AssetMaster a " +
           "WHERE a.assetNo LIKE CONCAT(:categoryCode, '-', :departmentCode, '-', :yearMonth, '-%')")
    Long findMaxSequenceByCategoryAndDepartment(
            @Param("categoryCode") String categoryCode,
            @Param("departmentCode") String departmentCode,
            @Param("yearMonth") String yearMonth);
}