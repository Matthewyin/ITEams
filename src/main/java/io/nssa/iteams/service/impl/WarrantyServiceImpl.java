package io.nssa.iteams.service.impl;

import io.nssa.iteams.entity.WarrantyContract;
import io.nssa.iteams.repository.WarrantyContractRepository;
import io.nssa.iteams.service.WarrantyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 保修合同服务实现类
 */
@Service
public class WarrantyServiceImpl implements WarrantyService {
    
    private final WarrantyContractRepository warrantyRepository;
    
    @Autowired
    public WarrantyServiceImpl(WarrantyContractRepository warrantyRepository) {
        this.warrantyRepository = warrantyRepository;
    }
    
    @Override
    public List<WarrantyContract> getAllWarranties() {
        return warrantyRepository.findAll();
    }
    
    @Override
    public Optional<WarrantyContract> getWarrantyById(Long id) {
        return warrantyRepository.findById(id);
    }
    
    @Override
    public Optional<WarrantyContract> getWarrantyByContractNo(String contractNo) {
        return warrantyRepository.findByContractNo(contractNo);
    }
    
    @Override
    public List<WarrantyContract> getWarrantiesByAssetId(Long assetId) {
        return warrantyRepository.findByAssetId(assetId);
    }
    
    @Override
    public List<WarrantyContract> getValidWarrantiesByAssetId(Long assetId) {
        return warrantyRepository.findValidWarrantiesByAssetId(assetId, LocalDate.now());
    }
    
    @Override
    @Transactional
    public WarrantyContract createWarranty(WarrantyContract warranty, Long operatorId) {
        warranty.setCreatedBy(operatorId);
        warranty.setUpdatedBy(operatorId);
        warranty.setCreatedAt(LocalDateTime.now());
        warranty.setUpdatedAt(LocalDateTime.now());
        return warrantyRepository.save(warranty);
    }
    
    @Override
    @Transactional
    public WarrantyContract updateWarranty(WarrantyContract warranty, Long operatorId) {
        warranty.setUpdatedBy(operatorId);
        warranty.setUpdatedAt(LocalDateTime.now());
        return warrantyRepository.save(warranty);
    }
    
    @Override
    @Transactional
    public void deleteWarranty(Long id) {
        warrantyRepository.deleteById(id);
    }
    
    @Override
    public List<WarrantyContract> getExpiringWarranties(LocalDate thresholdDate) {
        return warrantyRepository.findExpiringWarranties(thresholdDate);
    }
}