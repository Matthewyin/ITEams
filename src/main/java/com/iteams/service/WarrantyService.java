package com.iteams.service;

import com.iteams.model.entity.WarrantyContract;
import java.util.Optional;

/**
 * 维保合约服务接口
 */
public interface WarrantyService {

    /**
     * 保存维保记录
     * 
     * @param warranty 维保合约
     * @return 保存后的维保合约
     */
    WarrantyContract saveWarranty(WarrantyContract warranty);
    
    /**
     * 通过合同号查找维保合约
     * 
     * @param contractNo 合同号
     * @return 维保合约（如果存在）
     */
    Optional<WarrantyContract> findByContractNo(String contractNo);
} 