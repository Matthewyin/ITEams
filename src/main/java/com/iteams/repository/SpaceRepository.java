package com.iteams.repository;

import com.iteams.model.entity.SpaceTimeline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpaceRepository extends JpaRepository<SpaceTimeline, Long> {
    
    List<SpaceTimeline> findByAsset_AssetId(Long assetId);
    
    Optional<SpaceTimeline> findByAsset_AssetIdAndIsCurrentTrue(Long assetId);
    
    List<SpaceTimeline> findByAsset_AssetIdOrderByValidFromDesc(Long assetId);
} 