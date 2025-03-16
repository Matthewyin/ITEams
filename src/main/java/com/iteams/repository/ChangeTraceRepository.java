package com.iteams.repository;

import com.iteams.model.entity.ChangeTrace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChangeTraceRepository extends JpaRepository<ChangeTrace, Long> {
    
    List<ChangeTrace> findByAssetId(Long assetId);
    
    List<ChangeTrace> findByAssetIdOrderByOperatedAtDesc(Long assetId);
} 