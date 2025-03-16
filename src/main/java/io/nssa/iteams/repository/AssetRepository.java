package io.nssa.iteams.repository;

import io.nssa.iteams.entity.AssetMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssetRepository extends JpaRepository<AssetMaster, Long> {

    Optional<AssetMaster> findByUuid(String uuid);

    Optional<AssetMaster> findByAssetNo(String assetNo);

    List<AssetMaster> findByStatus(AssetMaster.AssetStatus status);
}