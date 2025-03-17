package com.iteams.service.impl;

import com.iteams.model.entity.WarrantyContract;
import com.iteams.repository.WarrantyRepository;
import com.iteams.service.WarrantyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

/**
 * 维保合约服务实现类
 * <p>
 * 该类实现了{@link WarrantyService}接口定义的维保服务功能，负责管理IT资产的维保合约信息，
 * 包括维保时间范围、服务级别、提供商等。维保信息对资产全生命周期管理至关重要，
 * 直接影响资产维护策略和成本控制。
 * </p>
 * <p>
 * 系统支持一个资产关联多个维保合约，但同一时间只有一个有效维保。维保状态包括：
 * <ul>
 *   <li>有效：当前日期在维保期限内</li>
 *   <li>即将过期：距离过期不足30天</li>
 *   <li>已过期：当前日期超过维保结束日期</li>
 *   <li>未生效：当前日期早于维保开始日期</li>
 * </ul>
 * </p>
 * <p>
 * 核心功能包括保存维保合约记录和通过合同号查询维保合约。
 * </p>
 */
@Service
public class WarrantyServiceImpl implements WarrantyService {

    /**
     * 维保合约数据访问对象
     */
    private final WarrantyRepository warrantyRepository;
    
    /**
     * 构造函数，通过依赖注入获取WarrantyRepository实例
     * 
     * @param warrantyRepository 维保合约数据访问对象
     */
    public WarrantyServiceImpl(WarrantyRepository warrantyRepository) {
        this.warrantyRepository = warrantyRepository;
    }
    
    /**
     * {@inheritDoc}
     * <p>
     * 实现思路：
     * <ol>
     *   <li>保存维保合约记录到数据库，生成维保ID</li>
     *   <li>维保记录包含合同编号、资产ID、维保时间范围等信息</li>
     *   <li>使用事务确保数据一致性</li>
     * </ol>
     * </p>
     * <p>
     * 注意：当前实现为简单保存，完整实现应包括：
     * <ul>
     *   <li>验证维保日期的有效性（开始日期必须早于结束日期）</li>
     *   <li>计算并设置维保状态（有效/即将过期/已过期）</li>
     *   <li>对于新合约，自动将资产的其他有效维保设置为无效</li>
     *   <li>更新资产主表的当前有效维保ID</li>
     *   <li>记录维保变更到变更溯源表</li>
     * </ul>
     * </p>
     */
    @Override
    @Transactional
    public WarrantyContract saveWarranty(WarrantyContract warranty) {
        return warrantyRepository.save(warranty);
    }
    
    /**
     * {@inheritDoc}
     * <p>
     * 实现思路：
     * <ol>
     *   <li>直接调用仓库层提供的合同号查询方法</li>
     *   <li>返回包装在Optional中的维保合约，如果不存在则为empty</li>
     *   <li>使用只读事务，不修改数据库内容</li>
     * </ol>
     * </p>
     * <p>
     * 该方法主要用于Excel导入时检查合同是否已存在，防止重复导入，
     * 同时也支持通过合同号直接查询维保详情。
     * </p>
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<WarrantyContract> findByContractNo(String contractNo) {
        return warrantyRepository.findByContractNo(contractNo);
    }
} 