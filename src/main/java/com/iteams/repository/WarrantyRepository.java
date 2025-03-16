package com.iteams.repository;

import com.iteams.model.entity.WarrantyContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarrantyRepository extends JpaRepository<WarrantyContract, Long> {
    
    List<WarrantyContract> findByAssetId(Long assetId);
    
    Optional<WarrantyContract> findByAssetIdAndIsActiveTrue(Long assetId);
    
    List<WarrantyContract> findByAssetIdOrderByEndDateDesc(Long assetId);
} 