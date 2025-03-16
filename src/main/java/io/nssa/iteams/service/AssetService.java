package io.nssa.iteams.service;

import io.nssa.iteams.entity.AssetMaster;

import java.util.List;
import java.util.Optional;

/**
 * 资产服务接口，定义资产管理相关的业务操作
 */
public interface AssetService {
    
    /**
     * 获取所有资产列表
     * @return 资产列表
     */
    List<AssetMaster> getAllAssets();

    /**
     * 根据ID获取资产
     * @param id 资产ID
     * @return 可选的资产对象
     */
    Optional<AssetMaster> getAssetById(Long id);

    /**
     * 根据UUID获取资产
     * @param uuid 资产UUID
     * @return 可选的资产对象
     */
    Optional<AssetMaster> getAssetByUuid(String uuid);

    /**
     * 根据资产编号获取资产
     * @param assetNo 资产编号
     * @return 可选的资产对象
     */
    Optional<AssetMaster> getAssetByNo(String assetNo);

    /**
     * 创建新资产
     * @param asset 资产对象
     * @param operatorId 操作人ID
     * @return 保存后的资产对象
     */
    AssetMaster createAsset(AssetMaster asset, Long operatorId);

    /**
     * 更新资产信息
     * @param asset 资产对象
     * @param operatorId 操作人ID
     * @return 更新后的资产对象
     */
    AssetMaster updateAsset(AssetMaster asset, Long operatorId);

    /**
     * 根据状态获取资产列表
     * @param status 资产状态
     * @return 状态对应的资产列表
     */
    List<AssetMaster> getAssetsByStatus(AssetMaster.AssetStatus status);
}