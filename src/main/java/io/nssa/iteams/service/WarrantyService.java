package io.nssa.iteams.service;

import io.nssa.iteams.entity.WarrantyContract;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 保修合同服务接口
 */
public interface WarrantyService {
    
    /**
     * 获取所有保修合同
     * @return 保修合同列表
     */
    List<WarrantyContract> getAllWarranties();
    
    /**
     * 根据ID获取保修合同
     * @param id 保修合同ID
     * @return 保修合同对象
     */
    Optional<WarrantyContract> getWarrantyById(Long id);
    
    /**
     * 根据合同编号获取保修合同
     * @param contractNo 合同编号
     * @return 保修合同对象
     */
    Optional<WarrantyContract> getWarrantyByContractNo(String contractNo);
    
    /**
     * 根据资产ID获取所有保修合同
     * @param assetId 资产ID
     * @return 保修合同列表
     */
    List<WarrantyContract> getWarrantiesByAssetId(Long assetId);
    
    /**
     * 获取指定资产的有效保修合同
     * @param assetId 资产ID
     * @return 有效的保修合同列表
     */
    List<WarrantyContract> getValidWarrantiesByAssetId(Long assetId);
    
    /**
     * 创建新的保修合同
     * @param warranty 保修合同对象
     * @param operatorId 操作员ID
     * @return 创建后的保修合同对象
     */
    WarrantyContract createWarranty(WarrantyContract warranty, Long operatorId);
    
    /**
     * 更新保修合同
     * @param warranty 保修合同对象
     * @param operatorId 操作员ID
     * @return 更新后的保修合同对象
     */
    WarrantyContract updateWarranty(WarrantyContract warranty, Long operatorId);
    
    /**
     * 删除保修合同
     * @param id 保修合同ID
     */
    void deleteWarranty(Long id);
    
    /**
     * 获取即将到期的保修合同
     * @param thresholdDate 阈值日期
     * @return 即将到期的保修合同列表
     */
    List<WarrantyContract> getExpiringWarranties(LocalDate thresholdDate);
}