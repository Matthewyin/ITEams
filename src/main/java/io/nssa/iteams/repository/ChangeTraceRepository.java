package io.nssa.iteams.repository;

import io.nssa.iteams.entity.ChangeTrace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChangeTraceRepository extends JpaRepository<ChangeTrace, Long> {
    
    List<ChangeTrace> findByAssetIdOrderByOperateTimeDesc(Long assetId);
    
    List<ChangeTrace> findByChangeType(String changeType);
    
    List<ChangeTrace> findByOperatorId(Long operatorId);
}