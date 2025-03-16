package io.nssa.iteams.repository;

import io.nssa.iteams.entity.WarrantyContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 保修合同数据访问接口
 */
@Repository
public interface WarrantyContractRepository extends JpaRepository<WarrantyContract, Long> {

    /**
     * 根据合同编号查找保修合同
     * @param contractNo 合同编号
     * @return 保修合同对象
     */
    Optional<WarrantyContract> findByContractNo(String contractNo);

    /**
     * 根据资产ID查找保修合同列表
     * @param assetId 资产ID
     * @return 保修合同列表
     */
    List<WarrantyContract> findByAssetId(Long assetId);

    /**
     * 查找指定资产的有效保修合同
     * @param assetId 资产ID
     * @param currentDate 当前日期
     * @return 有效的保修合同列表
     */
    @Query("SELECT w FROM WarrantyContract w WHERE w.assetId = :assetId AND w.startDate <= :currentDate AND w.endDate >= :currentDate")
    List<WarrantyContract> findValidWarrantiesByAssetId(@Param("assetId") Long assetId, @Param("currentDate") LocalDate currentDate);

    /**
     * 查找即将到期的保修合同
     * @param thresholdDate 阈值日期
     * @return 即将到期的保修合同列表
     */
    @Query("SELECT w FROM WarrantyContract w WHERE w.endDate BETWEEN CURRENT_DATE AND :thresholdDate")
    List<WarrantyContract> findExpiringWarranties(@Param("thresholdDate") LocalDate thresholdDate);
}