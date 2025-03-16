package com.iteams.service.impl;

import com.iteams.model.entity.ChangeTrace;
import com.iteams.repository.ChangeTraceRepository;
import com.iteams.service.ChangeTraceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChangeTraceServiceImpl implements ChangeTraceService {

    private final ChangeTraceRepository changeTraceRepository;
    
    public ChangeTraceServiceImpl(ChangeTraceRepository changeTraceRepository) {
        this.changeTraceRepository = changeTraceRepository;
    }
    
    @Override
    @Transactional
    public ChangeTrace saveChange(ChangeTrace changeTrace) {
        return changeTraceRepository.save(changeTrace);
    }
} 