package com.iteams.repository;

import com.iteams.model.entity.ChangeTrace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 资产变更溯源数据访问接口
 * <p>
 * 该接口扩展了Spring Data JPA的{@link JpaRepository}，提供对{@link ChangeTrace}实体的基本CRUD操作，
 * 以及针对资产变更历史管理特定需求的自定义查询方法。
 * </p>
 * <p>
 * 变更溯源表记录了资产从创建到退役整个生命周期内的所有重要变更记录，包括但不限于：
 * <ul>
 *   <li>资产初始化</li>
 *   <li>位置变更</li>
 *   <li>状态变更（在用、闲置、报废等）</li>
 *   <li>归属人变更</li>
 *   <li>维保变更</li>
 *   <li>属性变更</li>
 * </ul>
 * 每条变更记录包含变更类型、变更前值、变更后值、变更时间、变更操作人等信息，
 * 形成资产的完整变更历史链，满足审计和溯源需求。
 * </p>
 * <p>
 * 核心功能包括：
 * <ul>
 *   <li>查询特定资产的变更历史</li>
 *   <li>按变更类型筛选变更记录</li>
 *   <li>获取最新的变更记录</li>
 * </ul>
 * </p>
 */
@Repository
public interface ChangeTraceRepository extends JpaRepository<ChangeTrace, Long> {
    
    /**
     * 按资产ID查询所有变更记录
     * <p>
     * 检索指定资产的所有变更历史记录，返回的结果按系统默认排序（通常是按ID升序）。
     * 用于查看资产的完整变更历史，支持审计和追溯。
     * </p>
     * 
     * @param assetId 资产ID
     * @return 该资产的所有变更记录列表
     */
    List<ChangeTrace> findByAsset_AssetId(Long assetId);
    
    /**
     * 按资产ID查询所有变更记录，并按操作时间降序排序
     * <p>
     * 检索指定资产的所有变更历史记录，并按操作时间从新到旧排序。
     * 这种排序方式使最近的变更记录显示在前面，便于快速查看最新变更。
     * </p>
     * 
     * @param assetId 资产ID
     * @return 按操作时间降序排序的变更记录列表，最新的在前
     */
    List<ChangeTrace> findByAsset_AssetIdOrderByOperatedAtDesc(Long assetId);
} 