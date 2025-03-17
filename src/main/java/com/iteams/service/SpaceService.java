package com.iteams.service;

import com.iteams.model.entity.SpaceTimeline;

/**
 * 空间轨迹服务接口
 * <p>
 * 该服务负责管理IT资产的物理位置和空间信息，记录资产在不同时间点的位置变化，
 * 实现资产位置的时间维度追溯。系统采用时间轴模式记录位置变更历史，每次位置
 * 变更会创建新的时间轴记录，而非覆盖原有数据。
 * </p>
 * <p>
 * 核心功能包括：
 * <ul>
 *   <li>资产空间位置的记录与管理</li>
 *   <li>位置变更历史的时间轴存储</li>
 *   <li>当前位置和历史位置的查询</li>
 *   <li>位置信息的地理坐标支持</li>
 * </ul>
 * </p>
 * <p>
 * 空间数据结构包括：
 * <ul>
 *   <li>数据中心 -> 机房 -> 机柜 -> U位的层级结构</li>
 *   <li>地理坐标（经纬度）</li>
 *   <li>保管人和使用环境</li>
 *   <li>时间维度（有效期）</li>
 * </ul>
 * </p>
 */
public interface SpaceService {

    /**
     * 保存资产空间位置记录
     * <p>
     * 将资产的空间位置信息保存到数据库，采用时间轴模式。当资产位置发生变更时，
     * 系统会自动将原位置记录标记为历史（设置valid_to时间并将is_current设为false），
     * 然后创建新的位置记录作为当前有效记录。
     * </p>
     * <p>
     * 该方法的实现应确保数据的完整性和一致性，特别是：
     * <ul>
     *   <li>同一资产在任一时间点只有一条有效的位置记录</li>
     *   <li>新位置记录的valid_from应晚于已有记录</li>
     *   <li>位置变更时自动处理原记录的valid_to和is_current标识</li>
     *   <li>位置路径的格式化和标准化</li>
     * </ul>
     * </p>
     * 
     * @param space 空间轨迹记录实体，包含资产ID、位置信息和有效期
     * @return 保存后的空间轨迹记录，包含系统生成的space_id
     * @throws IllegalArgumentException 如果参数为null或必要字段缺失
     */
    SpaceTimeline saveSpace(SpaceTimeline space);
} 