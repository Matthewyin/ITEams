package com.iteams.repository;

import com.iteams.model.entity.SpaceTimeline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 空间轨迹数据访问接口
 * <p>
 * 负责空间轨迹表(space_timeline)的数据库操作，提供对资产位置信息的存储和查询功能。
 * 空间轨迹表采用时间轴模式，记录资产在不同时间点的位置变化，实现位置变更的历史追溯。
 * </p>
 * <p>
 * 该仓库主要实现以下功能：
 * <ul>
 *   <li>保存资产位置记录</li>
 *   <li>查询资产的当前位置</li>
 *   <li>查询资产的历史位置变更</li>
 * </ul>
 * </p>
 * <p>
 * 位置数据包括数据中心、机房、机柜、U位的层级结构，以及地理坐标和保管人信息。
 * 每次位置变更会创建新记录，而非覆盖原有数据，保证历史可追溯性。
 * </p>
 */
@Repository
public interface SpaceRepository extends JpaRepository<SpaceTimeline, Long> {
    
    /**
     * 按资产ID查询所有空间位置记录
     * <p>
     * 检索指定资产的所有位置记录（包括历史记录和当前记录）。
     * 结果按系统默认排序（通常按ID升序），用于查看资产的完整位置变更历史。
     * </p>
     * 
     * @param assetId 资产ID
     * @return 该资产的所有空间位置记录列表
     */
    List<SpaceTimeline> findByAsset_AssetId(Long assetId);
    
    /**
     * 查询资产的当前有效位置记录
     * <p>
     * 检索指定资产当前有效的位置记录（is_current=true），每个资产
     * 在任一时间点只有一条有效的位置记录。该查询用于获取资产的当前位置信息。
     * </p>
     * 
     * @param assetId 资产ID
     * @return 包含当前位置信息的Optional对象，如果不存在则为empty
     */
    Optional<SpaceTimeline> findByAsset_AssetIdAndIsCurrentTrue(Long assetId);
    
    /**
     * 按资产ID查询所有空间位置记录，并按生效时间降序排序
     * <p>
     * 检索指定资产的所有位置记录，并按位置生效时间从新到旧排序。
     * 这种排序方式使最近的位置记录显示在前面，便于查看资产的位置变更轨迹。
     * </p>
     * 
     * @param assetId 资产ID
     * @return 按生效时间降序排序的位置记录列表，最新的在前
     */
    List<SpaceTimeline> findByAsset_AssetIdOrderByValidFromDesc(Long assetId);
} 