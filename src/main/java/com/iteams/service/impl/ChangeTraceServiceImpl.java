package com.iteams.service.impl;

import com.iteams.model.entity.ChangeTrace;
import com.iteams.repository.ChangeTraceRepository;
import com.iteams.service.ChangeTraceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 变更溯源服务实现类
 * <p>
 * 该类实现了{@link ChangeTraceService}接口定义的变更溯源服务功能，负责记录和管理
 * 资产全生命周期中的变更历史，实现资产变更操作的可追溯性和审计能力。
 * </p>
 * <p>
 * 变更溯源的核心功能是保存资产各类型的变更记录，包括：
 * <ul>
 *   <li>SPACE：位置变更</li>
 *   <li>STATUS：状态变更</li>
 *   <li>WARRANTY：维保变更</li>
 *   <li>PROPERTY：属性变更</li>
 *   <li>OWNER：归属变更</li>
 *   <li>INITIAL：初始化</li>
 * </ul>
 * </p>
 * <p>
 * 每条变更记录都包含变更前后的差异快照和操作信息，支持完整的历史回溯和审计跟踪。
 * </p>
 */
@Service
public class ChangeTraceServiceImpl implements ChangeTraceService {

    /**
     * 变更溯源数据访问对象
     */
    private final ChangeTraceRepository changeTraceRepository;
    
    /**
     * 构造函数，通过依赖注入获取ChangeTraceRepository实例
     * 
     * @param changeTraceRepository 变更溯源数据访问对象
     */
    public ChangeTraceServiceImpl(ChangeTraceRepository changeTraceRepository) {
        this.changeTraceRepository = changeTraceRepository;
    }
    
    /**
     * {@inheritDoc}
     * <p>
     * 实现思路：
     * <ol>
     *   <li>保存变更记录到数据库，生成变更ID</li>
     *   <li>变更记录包含变更类型、资产ID、变更差异快照等信息</li>
     *   <li>使用事务确保数据一致性</li>
     * </ol>
     * </p>
     * <p>
     * 该方法通常由其他服务调用，如资产服务、位置服务、维保服务等，
     * 以记录资产发生的各类变更。系统自动捕获变更前后的差异，形成完整变更轨迹。
     * </p>
     */
    @Override
    @Transactional
    public ChangeTrace saveChange(ChangeTrace changeTrace) {
        return changeTraceRepository.save(changeTrace);
    }
} 