package com.iteams.repository;

import com.iteams.model.entity.WarrantyContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarrantyRepository extends JpaRepository<WarrantyContract, Long> {
    
    List<WarrantyContract> findByAsset_AssetId(Long assetId);
    
    Optional<WarrantyContract> findByAsset_AssetIdAndIsActiveTrue(Long assetId);
    
    List<WarrantyContract> findByAsset_AssetIdOrderByEndDateDesc(Long assetId);
    
    /**
     * 通过合同号查找维保合约
     */
    Optional<WarrantyContract> findByContractNo(String contractNo);
} 