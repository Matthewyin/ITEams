package com.iteams.repository;

import com.iteams.model.entity.WarrantyContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 维保合约数据访问接口
 * <p>
 * 负责维保合约表(warranty_contract)的数据库操作，提供对资产维保信息的存储和查询功能。
 * 维保合约表记录资产的维保时间范围、服务级别、提供商等信息，支持维保到期预警和服务跟踪。
 * </p>
 * <p>
 * 该仓库主要实现以下功能：
 * <ul>
 *   <li>保存维保合约记录</li>
 *   <li>查询资产的当前有效维保</li>
 *   <li>查询资产的历史维保合约</li>
 *   <li>通过合同号查找维保记录</li>
 * </ul>
 * </p>
 * <p>
 * 系统支持一个资产关联多个维保合约，但同一时间只有一个有效维保（is_active=true）。
 * 每次维保变更会创建新记录，并将原维保标记为失效，确保历史可追溯性。
 * </p>
 */
@Repository
public interface WarrantyRepository extends JpaRepository<WarrantyContract, Long> {
    
    /**
     * 按资产ID查询所有维保合约记录
     * <p>
     * 检索指定资产的所有维保记录（包括历史记录和当前记录）。
     * 结果按系统默认排序（通常按ID升序），用于查看资产的完整维保历史。
     * </p>
     * 
     * @param assetId 资产ID
     * @return 该资产的所有维保合约记录列表
     */
    List<WarrantyContract> findByAsset_AssetId(Long assetId);
    
    /**
     * 查询资产的当前有效维保合约
     * <p>
     * 检索指定资产当前有效的维保合约（is_active=true），每个资产
     * 在任一时间点只有一份有效的维保合约。该查询用于获取资产的当前维保状态。
     * </p>
     * 
     * @param assetId 资产ID
     * @return 包含当前维保信息的Optional对象，如果不存在则为empty
     */
    Optional<WarrantyContract> findByAsset_AssetIdAndIsActiveTrue(Long assetId);
    
    /**
     * 按资产ID查询所有维保合约，并按结束日期降序排序
     * <p>
     * 检索指定资产的所有维保记录，并按维保结束日期从晚到早排序。
     * 这种排序方式使最长效期的维保记录显示在前面，便于查看资产的维保覆盖情况。
     * </p>
     * 
     * @param assetId 资产ID
     * @return 按结束日期降序排序的维保记录列表，结束日期最晚的在前
     */
    List<WarrantyContract> findByAsset_AssetIdOrderByEndDateDesc(Long assetId);
    
    /**
     * 通过合同号查找维保合约
     * <p>
     * 根据合同编号精确查询维保合约记录。合同编号是维保合约的业务唯一标识，
     * 通常由外部系统或供应商提供，格式如"WTY-2024-0001"。
     * </p>
     * <p>
     * 该方法主要用于：
     * <ul>
     *   <li>Excel数据导入时检查合同是否已存在</li>
     *   <li>关联外部系统的维保信息</li>
     *   <li>通过合同号直接查询维保详情</li>
     * </ul>
     * </p>
     * 
     * @param contractNo 合同编号，不能为null或空
     * @return 包含维保合约信息的Optional对象，如果不存在则为empty
     */
    Optional<WarrantyContract> findByContractNo(String contractNo);
} 