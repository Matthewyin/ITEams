package io.nssa.iteams.repository;

import io.nssa.iteams.entity.SpaceTimeline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpaceTimelineRepository extends JpaRepository<SpaceTimeline, Long> {
    
    List<SpaceTimeline> findByAssetId(Long assetId);
    
    @Query("SELECT s FROM SpaceTimeline s WHERE s.assetId = ?1 AND s.validTo IS NULL")
    Optional<SpaceTimeline> findCurrentSpaceByAssetId(Long assetId);
}