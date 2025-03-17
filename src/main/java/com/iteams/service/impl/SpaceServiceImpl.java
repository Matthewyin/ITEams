package com.iteams.service.impl;

import com.iteams.model.entity.SpaceTimeline;
import com.iteams.repository.SpaceRepository;
import com.iteams.service.SpaceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 空间轨迹服务实现类
 * <p>
 * 该类实现了{@link SpaceService}接口定义的空间轨迹服务功能，负责管理IT资产的物理位置和空间信息，
 * 记录资产在不同时间点的位置变化，实现资产位置的时间维度追溯。
 * </p>
 * <p>
 * 空间轨迹管理采用时间轴模式，每次位置变更会创建新的时间轴记录，而非覆盖原有数据。
 * 空间位置结构通常包括：数据中心 -> 机房 -> 机柜 -> U位，同时支持地理坐标和保管人信息。
 * </p>
 * <p>
 * 核心功能是保存资产的空间位置记录，同时维护位置记录的有效期和当前状态标识，
 * 确保同一资产在任一时间点只有一条有效的位置记录。
 * </p>
 */
@Service
public class SpaceServiceImpl implements SpaceService {

    /**
     * 空间轨迹数据访问对象
     */
    private final SpaceRepository spaceRepository;
    
    /**
     * 构造函数，通过依赖注入获取SpaceRepository实例
     * 
     * @param spaceRepository 空间轨迹数据访问对象
     */
    public SpaceServiceImpl(SpaceRepository spaceRepository) {
        this.spaceRepository = spaceRepository;
    }
    
    /**
     * {@inheritDoc}
     * <p>
     * 实现思路：
     * <ol>
     *   <li>保存空间位置记录到数据库，生成空间记录ID</li>
     *   <li>位置记录包含资产ID、位置路径、有效期等信息</li>
     *   <li>使用事务确保数据一致性</li>
     * </ol>
     * </p>
     * <p>
     * 注意：当前实现为简单保存，完整实现应包括：
     * <ul>
     *   <li>检查并处理该资产的旧位置记录（设置失效时间和is_current标志）</li>
     *   <li>确认新位置记录的valid_from晚于已有记录</li>
     *   <li>设置新记录的is_current=true</li>
     *   <li>更新资产主表的当前空间ID</li>
     *   <li>记录位置变更到变更溯源表</li>
     * </ul>
     * </p>
     */
    @Override
    @Transactional
    public SpaceTimeline saveSpace(SpaceTimeline space) {
        return spaceRepository.save(space);
    }
} 