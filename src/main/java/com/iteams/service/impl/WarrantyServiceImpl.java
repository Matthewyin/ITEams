package com.iteams.service.impl;

import com.iteams.model.entity.WarrantyContract;
import com.iteams.repository.WarrantyRepository;
import com.iteams.service.WarrantyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
public class WarrantyServiceImpl implements WarrantyService {

    private final WarrantyRepository warrantyRepository;
    
    public WarrantyServiceImpl(WarrantyRepository warrantyRepository) {
        this.warrantyRepository = warrantyRepository;
    }
    
    @Override
    @Transactional
    public WarrantyContract saveWarranty(WarrantyContract warranty) {
        return warrantyRepository.save(warranty);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<WarrantyContract> findByContractNo(String contractNo) {
        return warrantyRepository.findByContractNo(contractNo);
    }
} 