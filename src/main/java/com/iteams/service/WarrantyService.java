package com.iteams.service;

import com.iteams.model.entity.WarrantyContract;
import java.util.Optional;

/**
 * 维保合约服务接口
 * <p>
 * 该服务负责管理IT资产的维保合约信息，包括维保时间范围、服务级别、提供商等。
 * 维保信息对资产全生命周期管理至关重要，直接影响资产维护策略和成本控制。
 * 系统支持一个资产关联多个维保合约，但同一时间只有一个有效维保。
 * </p>
 * <p>
 * 核心功能包括：
 * <ul>
 *   <li>维保合约的创建与管理</li>
 *   <li>合约有效性判断与状态更新</li>
 *   <li>维保到期预警</li>
 *   <li>合约查询与统计</li>
 *   <li>维保供应商管理</li>
 * </ul>
 * </p>
 * <p>
 * 维保状态包括：
 * <ul>
 *   <li>有效：当前日期在维保期限内</li>
 *   <li>即将过期：距离过期不足30天</li>
 *   <li>已过期：当前日期超过维保结束日期</li>
 *   <li>未生效：当前日期早于维保开始日期</li>
 * </ul>
 * </p>
 */
public interface WarrantyService {

    /**
     * 保存维保合约记录
     * <p>
     * 创建或更新资产的维保合约信息。当为资产创建新的维保合约时，系统会自动
     * 检查是否存在其他有效维保，如果存在，则将其标记为失效。保证同一资产
     * 在任一时间点只有一份有效的维保合约。
     * </p>
     * <p>
     * 该方法会执行以下操作：
     * <ul>
     *   <li>验证维保日期的有效性（开始日期必须早于结束日期）</li>
     *   <li>计算并设置维保状态（有效/即将过期/已过期）</li>
     *   <li>对于新合约，自动将资产的其他有效维保设置为无效</li>
     *   <li>更新资产主表的当前有效维保ID</li>
     *   <li>记录维保变更到变更溯源表</li>
     * </ul>
     * </p>
     * 
     * @param warranty 维保合约实体，包含合同编号、时间范围、服务级别等
     * @return 保存后的维保合约，包含系统生成的warranty_id
     * @throws IllegalArgumentException 如果参数为null或必要字段缺失
     * @throws IllegalStateException 如果合同日期无效（如开始日期晚于结束日期）
     */
    WarrantyContract saveWarranty(WarrantyContract warranty);
    
    /**
     * 通过合同号查找维保合约
     * <p>
     * 根据合同编号查找对应的维保合约记录。合同编号是维保合约的业务唯一标识，
     * 通常由外部系统或供应商提供，格式如"WTY-2024-0001"。该方法主要用于
     * 在导入数据时检查合同是否已存在，防止重复导入。
     * </p>
     * <p>
     * 根据业务规则，同一个合同编号只能对应一个维保记录，合同编号在系统中
     * 必须保持唯一性。该查询方法区分大小写，并进行精确匹配。
     * </p>
     * 
     * @param contractNo 合同编号，不能为null或空字符串
     * @return 包装在Optional中的维保合约，如果不存在则返回Optional.empty()
     * @throws IllegalArgumentException 如果contractNo为null或空
     */
    Optional<WarrantyContract> findByContractNo(String contractNo);
} 