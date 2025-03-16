package com.iteams.repository;

import com.iteams.model.entity.AssetMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssetRepository extends JpaRepository<AssetMaster, Long> {

    Optional<AssetMaster> findByAssetNo(String assetNo);

    Optional<AssetMaster> findByAssetUuid(String assetUuid);

    @Query("SELECT a FROM AssetMaster a WHERE a.importBatch = ?1")
    List<AssetMaster> findByImportBatch(String batchId);

    @Query("SELECT a FROM AssetMaster a WHERE a.excelRowHash = ?1")
    Optional<AssetMaster> findByExcelRowHash(String rowHash);

    @Query(value = "SELECT a.* FROM asset_master a INNER JOIN space_timeline s ON a.space_id = s.space_id " +
            "WHERE s.location_path LIKE %?1% AND s.is_current = 1", nativeQuery = true)
    List<AssetMaster> findByLocationPathContaining(String locationPath);

    @Query("SELECT COUNT(a) FROM AssetMaster a WHERE CAST(json_extract(a.categoryHierarchy, '$.l1') AS integer) = ?1")
    Long countByTopCategory(Long categoryId);
}