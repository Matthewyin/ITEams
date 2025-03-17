package com.iteams.service;

import com.iteams.model.entity.ChangeTrace;

/**
 * 变更溯源服务接口
 * <p>
 * 该服务负责记录和管理资产全生命周期中的变更历史，实现资产变更操作的可追溯性和审计能力。
 * 每次资产信息发生变更时（如位置变更、状态变更、维保变更等），系统会自动记录变更前后的差异
 * 以及变更操作的详细信息，形成完整的资产变更轨迹。
 * </p>
 * <p>
 * 核心功能包括：
 * <ul>
 *   <li>资产变更记录的创建与存储</li>
 *   <li>变更差异的快照保存</li>
 *   <li>变更历史的查询与回溯</li>
 *   <li>审计日志的生成与管理</li>
 * </ul>
 * </p>
 * <p>
 * 变更类型包括但不限于：
 * <ul>
 *   <li>SPACE: 位置变更</li>
 *   <li>STATUS: 状态变更</li>
 *   <li>WARRANTY: 维保变更</li>
 *   <li>PROPERTY: 属性变更</li>
 *   <li>OWNER: 归属变更</li>
 *   <li>INITIAL: 初始化</li>
 * </ul>
 * </p>
 */
public interface ChangeTraceService {

    /**
     * 保存资产变更记录
     * <p>
     * 将资产的变更信息保存到变更溯源表中，包括变更类型、变更内容差异和操作信息等。
     * 系统会自动计算并存储变更前后数据的差异快照，形成变更轨迹。
     * </p>
     * <p>
     * 该方法通常在以下场景被调用：
     * <ul>
     *   <li>资产信息编辑后</li>
     *   <li>资产位置变更后</li>
     *   <li>资产状态流转时</li>
     *   <li>维保信息更新时</li>
     *   <li>资产初始化创建时</li>
     * </ul>
     * </p>
     * 
     * @param changeTrace 变更记录实体，包含变更类型、资产ID、操作人等信息
     * @return 保存后的变更记录，包含系统生成的trace_id
     * @throws IllegalArgumentException 如果参数为null或必要字段缺失
     */
    ChangeTrace saveChange(ChangeTrace changeTrace);
} 