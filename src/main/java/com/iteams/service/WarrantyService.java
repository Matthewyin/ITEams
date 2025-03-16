package com.iteams.service;

import com.iteams.model.entity.WarrantyContract;

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
} 