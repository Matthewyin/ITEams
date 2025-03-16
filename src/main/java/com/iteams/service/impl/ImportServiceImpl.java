package com.iteams.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iteams.model.dto.ImportProgressDTO;
import com.iteams.model.dto.ImportResultDTO;
import com.iteams.model.entity.*;
import com.iteams.repository.*;
import com.iteams.service.CategoryService;
import com.iteams.service.ChangeTraceService;
import com.iteams.service.ImportService;
import com.iteams.service.SpaceService;
import com.iteams.service.WarrantyService;
import com.iteams.util.UuidGenerator;
import com.iteams.util.excel.ExcelParser;
import com.iteams.util.excel.RowProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Excel导入服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImportServiceImpl implements ImportService {

    private final ExcelParser excelParser;
    private final AssetRepository assetRepository;
    private final CategoryService categoryService;
    private final SpaceService spaceService;
    private final WarrantyService warrantyService;
    private final ChangeTraceService changeTraceService;
    private final ObjectMapper objectMapper;

    /**
     * 异步导入Excel文件，处理进度跟踪
     */
    @Override
    @Async
    public String importExcelAsync(MultipartFile file) throws IOException {
        String importBatch = UuidGenerator.generateImportBatchId();
        return excelParser.parseFile(file, new AssetRowProcessor(importBatch));
    }

    /**
     * 获取导入进度
     */
    @Override
    public ImportProgressDTO getImportProgress(String taskId) {
        ExcelParser.ImportTaskStatus status = excelParser.getTaskStatus(taskId);
        if (status == null) {
            return null;
        }

        ImportProgressDTO progress = new ImportProgressDTO();
        progress.setTaskId(taskId);
        progress.setState(status.getState().name());
        progress.setProgress(status.getProgress());
        progress.setTotalRows(status.getTotalRows());
        progress.setProcessedRows(status.getProcessedRows());
        progress.setSuccessRows(status.getSuccessRows());
        progress.setFailedRows(status.getFailedRows());
        progress.setErrors(status.getErrors());
        progress.setError(status.getError());
        
        return progress;
    }

    /**
     * 获取导入结果统计
     */
    @Override
    public ImportResultDTO getImportResult(String batchId) {
        long totalAssets = assetRepository.findByImportBatch(batchId).size();
        ImportResultDTO result = new ImportResultDTO();
        result.setBatchId(batchId);
        result.setImportTime(LocalDateTime.now());
        result.setTotalAssets(totalAssets);
        // 可以添加更多统计信息
        return result;
    }

    /**
     * Excel行处理器，实现RowProcessor接口，专门用于处理资产数据
     */
    private class AssetRowProcessor implements RowProcessor {
        
        private final String importBatch;
        private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        public AssetRowProcessor(String importBatch) {
            this.importBatch = importBatch;
        }
        
        @Override
        @Transactional
        public boolean processRow(Map<String, Object> rowData, int rowIndex) {
            try {
                // 1. 生成数据指纹，防止重复导入
                String rowHash = generateRowHash(rowData);
                
                // 检查是否已存在相同的数据指纹
                Optional<AssetMaster> existingAsset = assetRepository.findByExcelRowHash(rowHash);
                if (existingAsset.isPresent()) {
                    log.info("跳过重复数据: {}", rowData.get("资产编号"));
                    return true; // 跳过已导入的行，但视为成功
                }
                
                // 2. 处理分类信息
                Long level1CategoryId = getLongOrNull(rowData.get("一级分类"));
                Long level2CategoryId = getLongOrNull(rowData.get("二级分类"));
                Long level3CategoryId = getLongOrNull(rowData.get("三级分类"));
                
                Map<String, Object> categoryMap = new HashMap<>();
                categoryMap.put("l1", level1CategoryId);
                categoryMap.put("l2", level2CategoryId);
                categoryMap.put("l3", level3CategoryId);
                
                // 验证分类树是否完整
                if (!categoryService.validateCategoryPath(level1CategoryId, level2CategoryId, level3CategoryId)) {
                    log.error("分类路径无效: {}/{}/{}", level1CategoryId, level2CategoryId, level3CategoryId);
                    return false;
                }
                
                // 3. 创建资产主记录
                AssetMaster asset = new AssetMaster();
                asset.setAssetUuid(UuidGenerator.generateAssetUuid());
                asset.setAssetNo(getStringOrNull(rowData.get("资产编号")));
                asset.setAssetName(getStringOrNull(rowData.get("资产名称")));
                
                // 处理资产状态
                String statusStr = getStringOrNull(rowData.get("资产状态"));
                AssetMaster.AssetStatus status = mapAssetStatus(statusStr);
                asset.setCurrentStatus(status);
                
                // 设置分类JSON和指纹
                asset.setCategoryHierarchy(objectMapper.writeValueAsString(categoryMap));
                asset.setDataFingerprint(rowHash);
                asset.setImportBatch(importBatch);
                asset.setExcelRowHash(rowHash);
                
                // 4. 处理空间信息（新旧数据都要处理）
                SpaceTimeline currentSpace = createSpaceRecord(rowData, asset, false);
                spaceService.saveSpace(currentSpace);
                
                // 如果有变更后的空间信息，则创建但标记为非当前
                if (hasChangeSpaceInfo(rowData)) {
                    SpaceTimeline changedSpace = createSpaceRecord(rowData, asset, true);
                    spaceService.saveSpace(changedSpace);
                    
                    // 记录变更信息
                    recordSpaceChange(asset, currentSpace, changedSpace);
                }
                
                // 设置当前空间
                asset.setSpace(currentSpace);
                
                // 5. 处理维保信息
                WarrantyContract warranty = createWarrantyRecord(rowData, asset);
                if (warranty != null) {
                    warrantyService.saveWarranty(warranty);
                    asset.setWarranty(warranty);
                }
                
                // 6. 保存资产记录
                AssetMaster savedAsset = assetRepository.save(asset);
                
                // 7. 记录初始状态变更
                recordInitialState(savedAsset, rowData);
                
                return true;
                
            } catch (Exception e) {
                log.error("处理行{}时出错: {}", rowIndex, e.getMessage(), e);
                return false;
            }
        }
        
        // 根据字符串状态映射到枚举
        private AssetMaster.AssetStatus mapAssetStatus(String status) {
            if (status == null) return AssetMaster.AssetStatus.INVENTORY;
            
            switch (status) {
                case "使用中":
                    return AssetMaster.AssetStatus.IN_USE;
                case "维修":
                    return AssetMaster.AssetStatus.MAINTENANCE;
                case "报废":
                    return AssetMaster.AssetStatus.RETIRED;
                default:
                    return AssetMaster.AssetStatus.INVENTORY;
            }
        }
        
        // 创建空间记录
        private SpaceTimeline createSpaceRecord(Map<String, Object> rowData, AssetMaster asset, boolean isChanged) {
            SpaceTimeline space = new SpaceTimeline();
            
            String prefix = isChanged ? "变更后" : "";
            String dataCenter = getStringOrNull(rowData.get(prefix + "数据中心"));
            String roomName = getStringOrNull(rowData.get(prefix + "机房名称"));
            String cabinetNo = getStringOrNull(rowData.get(prefix + "机柜编号"));
            String uPosition = getStringOrNull(rowData.get(prefix + "U位编号"));
            String environment = getStringOrNull(rowData.get(prefix + "使用环境"));
            String keeper = getStringOrNull(rowData.get(prefix + "保管人"));
            
            // 构造位置路径
            StringBuilder locationPathBuilder = new StringBuilder();
            if (dataCenter != null) locationPathBuilder.append(dataCenter);
            if (roomName != null) locationPathBuilder.append("/").append(roomName);
            if (cabinetNo != null) locationPathBuilder.append("/").append(cabinetNo);
            if (uPosition != null) locationPathBuilder.append("/U").append(uPosition);
            
            space.setAsset(asset);
            space.setLocationPath(locationPathBuilder.toString());
            space.setDataCenter(dataCenter);
            space.setRoomName(roomName);
            space.setCabinetNo(cabinetNo);
            space.setUPosition(uPosition);
            space.setEnvironment(environment);
            space.setKeeper(keeper);
            space.setValidFrom(LocalDateTime.now());
            space.setIsCurrent(!isChanged); // 如果是变更后的记录，则不是当前记录
            
            return space;
        }
        
        // 创建维保记录
        private WarrantyContract createWarrantyRecord(Map<String, Object> rowData, AssetMaster asset) {
            String contractNo = getStringOrNull(rowData.get("合同号"));
            if (contractNo == null || contractNo.trim().isEmpty()) {
                return null;
            }
            
            WarrantyContract warranty = new WarrantyContract();
            warranty.setAsset(asset);
            warranty.setContractNo(contractNo);
            
            // 维保日期
            LocalDate startDate = parseDate(rowData.get("维保开始日期"));
            LocalDate endDate = parseDate(rowData.get("维保结束日期"));
            
            warranty.setStartDate(startDate != null ? startDate : LocalDate.now());
            warranty.setEndDate(endDate != null ? endDate : LocalDate.now().plusYears(1));
            
            // 其他字段
            warranty.setProvider(getStringOrNull(rowData.get("维保提供商")));
            warranty.setWarrantyStatus(getStringOrNull(rowData.get("维保状态")));
            warranty.setProviderLevel((byte)1); // 默认基础级别
            
            // 资产生命周期
            Double lifeYears = getDoubleOrNull(rowData.get("资产使用年限(年)"));
            warranty.setAssetLifeYears(lifeYears != null ? lifeYears.intValue() : 5);
            
            // 到货验收日期
            warranty.setAcceptanceDate(parseDate(rowData.get("到货验收日期")));
            
            // 是否当前有效
            warranty.setIsActive(true);
            
            return warranty;
        }
        
        // 记录空间变更
        private void recordSpaceChange(AssetMaster asset, SpaceTimeline oldSpace, SpaceTimeline newSpace) {
            try {
                // 构建变更差异JSON
                Map<String, Object> before = new HashMap<>();
                before.put("data_center", oldSpace.getDataCenter());
                before.put("room_name", oldSpace.getRoomName());
                before.put("cabinet", oldSpace.getCabinetNo());
                before.put("u_position", oldSpace.getUPosition());
                
                Map<String, Object> after = new HashMap<>();
                after.put("data_center", newSpace.getDataCenter());
                after.put("room_name", newSpace.getRoomName());
                after.put("cabinet", newSpace.getCabinetNo());
                after.put("u_position", newSpace.getUPosition());
                
                Map<String, Object> delta = new HashMap<>();
                delta.put("field", "space");
                delta.put("before", before);
                delta.put("after", after);
                
                String deltaJson = objectMapper.writeValueAsString(delta);
                
                // 创建变更记录
                ChangeTrace change = new ChangeTrace();
                change.setAsset(asset);
                change.setChangeType(ChangeTrace.ChangeType.SPACE);
                change.setDeltaSnapshot(deltaJson);
                change.setOperatedBy("EXCEL_IMPORT");
                
                changeTraceService.saveChange(change);
                
            } catch (Exception e) {
                log.error("记录变更失败", e);
            }
        }
        
        // 记录初始状态
        private void recordInitialState(AssetMaster asset, Map<String, Object> rowData) {
            try {
                Map<String, Object> initialData = new HashMap<>();
                initialData.put("asset_no", asset.getAssetNo());
                initialData.put("asset_name", asset.getAssetName());
                initialData.put("status", asset.getCurrentStatus().name());
                
                String initialJson = objectMapper.writeValueAsString(initialData);
                
                ChangeTrace initialChange = new ChangeTrace();
                initialChange.setAsset(asset);
                initialChange.setChangeType(ChangeTrace.ChangeType.INITIAL);
                initialChange.setDeltaSnapshot(initialJson);
                initialChange.setOperatedBy(getStringOrNull(rowData.get("创建人")));
                
                changeTraceService.saveChange(initialChange);
                
            } catch (Exception e) {
                log.error("记录初始状态失败", e);
            }
        }
        
        // 判断是否有变更后的空间信息
        private boolean hasChangeSpaceInfo(Map<String, Object> rowData) {
            return rowData.get("变更后数据中心") != null || 
                   rowData.get("变更后机房名称") != null || 
                   rowData.get("变更后机柜编号") != null || 
                   rowData.get("变更后U位编号") != null;
        }
        
        // 生成行数据的哈希值用于去重
        private String generateRowHash(Map<String, Object> rowData) {
            try {
                String assetNo = getStringOrNull(rowData.get("资产编号"));
                String assetName = getStringOrNull(rowData.get("资产名称"));
                String serialNo = getStringOrNull(rowData.get("序列号"));
                
                String data = assetNo + "|" + assetName + "|" + serialNo;
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                byte[] hash = md.digest(data.getBytes());
                
                StringBuilder hexString = new StringBuilder();
                for (byte b : hash) {
                    hexString.append(String.format("%02x", b));
                }
                
                return hexString.toString();
                
            } catch (NoSuchAlgorithmException e) {
                log.error("SHA-256算法不可用", e);
                return UUID.randomUUID().toString(); // 降级方案
            }
        }
        
        // 辅助方法：安全地解析日期
        private LocalDate parseDate(Object value) {
            if (value == null) return null;
            
            try {
                if (value instanceof Date) {
                    return ((Date) value).toInstant()
                            .atZone(TimeZone.getDefault().toZoneId())
                            .toLocalDate();
                } else if (value instanceof String) {
                    String strValue = (String) value;
                    if (strValue.trim().isEmpty()) return null;
                    return LocalDate.parse(strValue, DATE_FORMATTER);
                }
            } catch (Exception e) {
                log.warn("日期解析失败: {}", value);
            }
            
            return null;
        }
        
        // 辅助方法：安全地获取字符串
        private String getStringOrNull(Object value) {
            return value != null ? value.toString() : null;
        }
        
        // 辅助方法：安全地获取Long
        private Long getLongOrNull(Object value) {
            if (value == null) return null;
            
            try {
                if (value instanceof Number) {
                    return ((Number) value).longValue();
                } else if (value instanceof String) {
                    String strValue = (String) value;
                    if (strValue.trim().isEmpty()) return null;
                    return Long.parseLong(strValue);
                }
            } catch (Exception e) {
                log.warn("Long解析失败: {}", value);
            }
            
            return null;
        }
        
        // 辅助方法：安全地获取Double
        private Double getDoubleOrNull(Object value) {
            if (value == null) return null;
            
            try {
                if (value instanceof Number) {
                    return ((Number) value).doubleValue();
                } else if (value instanceof String) {
                    String strValue = (String) value;
                    if (strValue.trim().isEmpty()) return null;
                    return Double.parseDouble(strValue);
                }
            } catch (Exception e) {
                log.warn("Double解析失败: {}", value);
            }
            
            return null;
        }
    }
} 