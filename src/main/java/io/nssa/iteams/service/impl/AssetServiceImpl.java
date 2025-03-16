package io.nssa.iteams.service.impl;

import io.nssa.iteams.entity.AssetMaster;
import io.nssa.iteams.entity.ChangeTrace;
import io.nssa.iteams.repository.AssetRepository;
import io.nssa.iteams.repository.ChangeTraceRepository;
import io.nssa.iteams.service.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** 资产服务实现类 */
@Service
public class AssetServiceImpl implements AssetService {

  private final ChangeTraceRepository changeTraceRepository;
  private final AssetRepository AssetRepository;

  @Autowired
  public AssetServiceImpl(
      ChangeTraceRepository changeTraceRepository, AssetRepository assetRepository) {
    this.changeTraceRepository = changeTraceRepository;
    this.AssetRepository = assetRepository;
  }

  @Override
  public List<AssetMaster> getAllAssets() {
    return AssetRepository.findAll();
  }

  @Override
  public Optional<AssetMaster> getAssetById(Long id) {
    return AssetRepository.findById(id);
  }

  @Override
  public Optional<AssetMaster> getAssetByUuid(String uuid) {
    return AssetRepository.findByUuid(uuid);
  }

  @Override
  public Optional<AssetMaster> getAssetByNo(String assetNo) {
    return AssetRepository.findByAssetNo(assetNo);
  }

  @Override
  @Transactional
  public AssetMaster createAsset(AssetMaster asset, Long operatorId) {
    // 生成UUID
    String uuid =
        "AST"
            + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
            + "-"
            + UUID.randomUUID();
    asset.setUuid(uuid);

    // 保存资产
    AssetMaster savedAsset = AssetRepository.save(asset);

    // 记录变更
    ChangeTrace trace = new ChangeTrace();
    trace.setAssetId(savedAsset.getAssetId());
    trace.setChangeType("CREATE");
    trace.setChangeDetails("{\"action\":\"资产创建\"}");
    trace.setOperatorId(operatorId);
    trace.setOperateTime(LocalDateTime.now());
    trace.setRemarks("新建资产");

    changeTraceRepository.save(trace);

    return savedAsset;
  }

  @Override
  @Transactional
  public AssetMaster updateAsset(AssetMaster asset, Long operatorId) {
    AssetMaster updatedAsset = AssetRepository.save(asset);

    // 记录变更
    ChangeTrace trace = new ChangeTrace();
    trace.setAssetId(updatedAsset.getAssetId());
    trace.setChangeType("UPDATE");
    trace.setChangeDetails("{\"action\":\"资产更新\"}");
    trace.setOperatorId(operatorId);
    trace.setOperateTime(LocalDateTime.now());
    trace.setRemarks("更新资产信息");

    changeTraceRepository.save(trace);

    return updatedAsset;
  }

  @Override
  public List<AssetMaster> getAssetsByStatus(AssetMaster.AssetStatus status) {
    return AssetRepository.findByStatus(status);
  }
}
