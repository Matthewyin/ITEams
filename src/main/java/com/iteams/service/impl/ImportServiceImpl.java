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
import org.springframework.dao.DataIntegrityViolationException;
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
import java.util.concurrent.CompletableFuture;

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
    private final CategoryRepository categoryRepository;

    /**
     * 异步导入Excel文件，处理进度跟踪
     */
    @Override
    @Async
    public CompletableFuture<String> importExcelAsync(MultipartFile file) throws IOException {
        String importBatch = UuidGenerator.generateImportBatchId();
        String taskId = excelParser.parseFile(file, new AssetRowProcessor(importBatch));
        
        // 在任务状态中存储导入批次ID，以便前端能够获取
        ExcelParser.ImportTaskStatus status = excelParser.getTaskStatus(taskId);
        if (status != null) {
            status.addExtraData("importBatch", importBatch);
        }
        
        return CompletableFuture.completedFuture(taskId);
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
        
        // 从任务状态的extraData中获取导入批次ID
        if (status.getExtraData().containsKey("importBatch")) {
            Object batchIdObj = status.getExtraData().get("importBatch");
            if (batchIdObj != null) {
                progress.setBatchId(batchIdObj.toString());
            }
        }
        
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
                // 0. 提前验证必填字段，避免SQL错误
                if (!validateRequiredFields(rowData, rowIndex)) {
                    return false;
                }
                
                // 1. 生成数据指纹，防止重复导入
                String rowHash = generateRowHash(rowData);
                
                // 检查是否已存在相同的数据指纹
                Optional<AssetMaster> existingAsset = assetRepository.findByExcelRowHash(rowHash);
                if (existingAsset.isPresent()) {
                    log.info("跳过重复数据: {}", rowData.get("资产编号"));
                    return true; // 跳过已导入的行，但视为成功
                }
                
                // 2. 处理分类信息 - 修改为使用字符串形式存储分类名称
                String level1Category = getStringOrNull(rowData.get("一级分类"));
                String level2Category = getStringOrNull(rowData.get("二级分类"));
                String level3Category = getStringOrNull(rowData.get("三级分类"));
                
                // 如果一级分类为空，使用默认分类
                if (level1Category == null || level1Category.trim().isEmpty()) {
                    level1Category = "未分类";
                    level2Category = "";
                    level3Category = "";
                    log.info("使用默认分类'未分类'，行: {}", rowIndex);
                }
                
                Map<String, Object> categoryMap = new HashMap<>();
                categoryMap.put("l1", level1Category);
                categoryMap.put("l2", level2Category != null ? level2Category : "");
                categoryMap.put("l3", level3Category != null ? level3Category : "");
                
                // 使用字符串分类名称进行验证
                if (!categoryService.validateCategoryPathByName(level1Category, level2Category, level3Category)) {
                    log.warn("分类路径验证失败: {}/{}/{}, 行: {}", level1Category, level2Category, level3Category, rowIndex);
                    // 预先创建分类
                    ensureCategoryExists(level1Category, level2Category, level3Category);
                }
                
                // 3. 创建资产主记录
                AssetMaster asset = new AssetMaster();
                asset.setAssetUuid(UuidGenerator.generateAssetUuid());
                
                // 资产编号必填，已在validateRequiredFields中验证
                String assetNo = getStringOrNull(rowData.get("资产编号"));
                
                // 检查资产编号是否已存在
                Optional<AssetMaster> existingByAssetNo = assetRepository.findByAssetNo(assetNo);
                if (existingByAssetNo.isPresent()) {
                    log.warn("资产编号已存在，跳过: {}, 行: {}", assetNo, rowIndex);
                    return false;
                }
                
                asset.setAssetNo(assetNo);
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
                
                // 4. 创建空间信息（但不立即保存）
                SpaceTimeline currentSpace = createSpaceRecord(rowData, asset, false);
                SpaceTimeline changedSpace = null;
                if (hasChangeSpaceInfo(rowData)) {
                    changedSpace = createSpaceRecord(rowData, asset, true);
                }
                
                // 5. 处理维保信息（但不立即保存）
                WarrantyContract warranty = createWarrantyRecord(rowData, asset);
                
                // 6. 先保存资产主记录 - 不设置关联关系
                // 移除关联，避免Hibernate尝试级联保存未持久化的对象
                asset.setSpace(null);
                asset.setWarranty(null);
                AssetMaster savedAsset;
                try {
                    savedAsset = assetRepository.save(asset);
                } catch (DataIntegrityViolationException e) {
                    log.error("保存资产记录时违反数据完整性约束: {}, 行: {}", e.getMessage(), rowIndex);
                    return false;
                }
                
                // 7. 再保存关联实体
                SpaceTimeline savedSpace = null;
                try {
                    // 现在可以安全地保存空间记录，因为AssetMaster已经持久化
                    if (currentSpace != null) {
                        currentSpace.setAsset(savedAsset);
                        savedSpace = spaceService.saveSpace(currentSpace);
                    }
                } catch (Exception e) {
                    log.error("保存空间记录时出错: {}, 行: {}", e.getMessage(), rowIndex);
                    // 继续处理，不返回false
                }
                
                // 8. 更新资产记录中的空间引用
                if (savedSpace != null) {
                    savedAsset.setSpace(savedSpace);
                    try {
                        savedAsset = assetRepository.save(savedAsset);
                    } catch (Exception e) {
                        log.error("更新资产空间引用时出错: {}, 行: {}", e.getMessage(), rowIndex);
                    }
                }
                
                if (changedSpace != null) {
                    try {
                        changedSpace.setAsset(savedAsset);
                        SpaceTimeline savedChangedSpace = spaceService.saveSpace(changedSpace);
                        
                        // 记录变更信息
                        if (savedSpace != null) {
                            recordSpaceChange(savedAsset, savedSpace, savedChangedSpace);
                        }
                    } catch (Exception e) {
                        log.error("保存变更空间记录时出错: {}, 行: {}", e.getMessage(), rowIndex);
                    }
                }
                
                // 保存维保信息
                if (warranty != null) {
                    try {
                        // 检查是否已存在相同合同号的维保记录
                        String contractNo = warranty.getContractNo();
                        if (contractNo != null && !contractNo.trim().isEmpty()) {
                            Optional<WarrantyContract> existingWarranty = warrantyService.findByContractNo(contractNo);
                            
                            WarrantyContract savedWarranty;
                            if (existingWarranty.isPresent()) {
                                // 如果已存在，则更新现有记录而不是创建新记录
                                WarrantyContract existing = existingWarranty.get();
                                // 更新现有记录的字段
                                existing.setStartDate(warranty.getStartDate());
                                existing.setEndDate(warranty.getEndDate());
                                existing.setProvider(warranty.getProvider());
                                existing.setProviderLevel(warranty.getProviderLevel());
                                existing.setWarrantyStatus(warranty.getWarrantyStatus());
                                existing.setAssetLifeYears(warranty.getAssetLifeYears());
                                existing.setAcceptanceDate(warranty.getAcceptanceDate());
                                // 保持原有的资产关联，不更新asset字段
                                
                                savedWarranty = warrantyService.saveWarranty(existing);
                                log.info("更新已存在的维保合约: {}", contractNo);
                            } else {
                                // 如果不存在，则设置资产关联并保存新记录
                                warranty.setAsset(savedAsset);
                                savedWarranty = warrantyService.saveWarranty(warranty);
                            }
                            
                            // 更新资产记录中的维保引用
                            savedAsset.setWarranty(savedWarranty);
                            try {
                                assetRepository.save(savedAsset);
                            } catch (Exception e) {
                                log.error("更新资产维保引用时出错: {}, 行: {}", e.getMessage(), rowIndex);
                            }
                        }
                    } catch (DataIntegrityViolationException e) {
                        log.error("保存维保记录时违反数据完整性约束: {}, 行: {}", e.getMessage(), rowIndex);
                        // 继续处理，不返回false
                    } catch (Exception e) {
                        log.error("保存维保记录时出错: {}, 行: {}", e.getMessage(), rowIndex);
                    }
                }
                
                // 9. 记录初始状态变更
                try {
                    recordInitialState(savedAsset, rowData);
                } catch (Exception e) {
                    log.error("记录初始状态变更时出错: {}, 行: {}", e.getMessage(), rowIndex);
                }
                
                return true;
                
            } catch (Exception e) {
                log.error("处理行{}时出错: {}", rowIndex, e.getMessage(), e);
                return false;
            }
        }
        
        /**
         * 验证必填字段，确保关键字段不为空
         */
        private boolean validateRequiredFields(Map<String, Object> rowData, int rowIndex) {
            String assetNo = getStringOrNull(rowData.get("资产编号"));
            String assetName = getStringOrNull(rowData.get("资产名称"));
            
            if (assetNo == null || assetNo.trim().isEmpty()) {
                log.error("资产编号不能为空，行: {}", rowIndex);
                return false;
            }
            
            if (assetName == null || assetName.trim().isEmpty()) {
                log.error("资产名称不能为空，行: {}", rowIndex);
                return false;
            }
            
            return true;
        }
        
        /**
         * 确保分类存在，如果不存在则创建
         */
        private void ensureCategoryExists(String level1, String level2, String level3) {
            if (level1 == null || level1.trim().isEmpty()) {
                return;
            }
            
            // 查找或创建一级分类
            CategoryMetadata l1 = categoryRepository.findByNameAndLevel(level1, (byte) 1)
                    .orElseGet(() -> {
                        CategoryMetadata newL1 = new CategoryMetadata();
                        newL1.setName(level1);
                        newL1.setLevel((byte) 1);
                        return categoryRepository.save(newL1);
                    });
            
            if (level2 == null || level2.trim().isEmpty()) {
                return;
            }
            
            // 查找或创建二级分类
            CategoryMetadata l2 = categoryRepository.findByNameAndLevel(level2, (byte) 2)
                    .orElseGet(() -> {
                        CategoryMetadata newL2 = new CategoryMetadata();
                        newL2.setName(level2);
                        newL2.setLevel((byte) 2);
                        newL2.setParent(l1);
                        return categoryRepository.save(newL2);
                    });
            
            if (level3 == null || level3.trim().isEmpty()) {
                return;
            }
            
            // 查找或创建三级分类
            categoryRepository.findByNameAndLevel(level3, (byte) 3)
                    .orElseGet(() -> {
                        CategoryMetadata newL3 = new CategoryMetadata();
                        newL3.setName(level3);
                        newL3.setLevel((byte) 3);
                        newL3.setParent(l2);
                        return categoryRepository.save(newL3);
                    });
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
            // 如果是变更后的空间，使用不同的字段
            String dcPrefix = isChanged ? "变更后数据中心" : "数据中心";
            String roomPrefix = isChanged ? "变更后机房" : "机房";
            String cabinetPrefix = isChanged ? "变更后机柜" : "机柜";
            String uPositionPrefix = isChanged ? "变更后U位" : "U位";
            
            String dataCenter = getStringOrNull(rowData.get(dcPrefix));
            String roomName = getStringOrNull(rowData.get(roomPrefix));
            String cabinetNo = getStringOrNull(rowData.get(cabinetPrefix));
            String uPosition = getStringOrNull(rowData.get(uPositionPrefix));
            
            // 如果所有空间信息都为空，则不创建空间记录
            if ((dataCenter == null || dataCenter.trim().isEmpty()) && 
                (roomName == null || roomName.trim().isEmpty()) && 
                (cabinetNo == null || cabinetNo.trim().isEmpty()) && 
                (uPosition == null || uPosition.trim().isEmpty())) {
                return null;
            }
            
            SpaceTimeline space = new SpaceTimeline();
            space.setAsset(asset);
            space.setDataCenter(dataCenter != null ? dataCenter : "");
            space.setRoomName(roomName != null ? roomName : "");
            space.setCabinetNo(cabinetNo != null ? cabinetNo : "");
            space.setUPosition(uPosition != null ? uPosition : "");
            
            // 拼接位置路径
            StringBuilder pathBuilder = new StringBuilder();
            if (dataCenter != null && !dataCenter.trim().isEmpty()) {
                pathBuilder.append(dataCenter);
            }
            if (roomName != null && !roomName.trim().isEmpty()) {
                if (pathBuilder.length() > 0) pathBuilder.append("/");
                pathBuilder.append(roomName);
            }
            if (cabinetNo != null && !cabinetNo.trim().isEmpty()) {
                if (pathBuilder.length() > 0) pathBuilder.append("/");
                pathBuilder.append(cabinetNo);
            }
            if (uPosition != null && !uPosition.trim().isEmpty()) {
                if (pathBuilder.length() > 0) pathBuilder.append("/");
                pathBuilder.append(uPosition);
            }
            
            space.setLocationPath(pathBuilder.toString());
            space.setIsCurrent(true);
            return space;
        }
        
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
                Map<String, Object> data = new HashMap<>();
                data.put("source", "EXCEL_IMPORT");
                data.put("batch", importBatch);
                data.put("import_time", LocalDateTime.now().toString());
                
                String deltaJson = objectMapper.writeValueAsString(data);
                
                ChangeTrace change = new ChangeTrace();
                change.setAsset(asset);
                change.setChangeType(ChangeTrace.ChangeType.INITIAL);
                change.setDeltaSnapshot(deltaJson);
                change.setOperatedBy("EXCEL_IMPORT");
                
                changeTraceService.saveChange(change);
                
            } catch (Exception e) {
                log.error("记录初始状态失败", e);
            }
        }
        
        // 检查是否有变更的空间信息
        private boolean hasChangeSpaceInfo(Map<String, Object> rowData) {
            return rowData.containsKey("变更后数据中心") || rowData.containsKey("变更后机房") || 
                   rowData.containsKey("变更后机柜") || rowData.containsKey("变更后U位");
        }
        
        private String generateRowHash(Map<String, Object> rowData) {
            try {
                String assetNo = getStringOrNull(rowData.get("资产编号"));
                String assetName = getStringOrNull(rowData.get("资产名称"));
                String serialNo = getStringOrNull(rowData.get("序列号"));
                String modelNo = getStringOrNull(rowData.get("型号"));
                
                // 增强哈希计算，加入更多字段，提高唯一性
                StringBuilder dataBuilder = new StringBuilder();
                if (assetNo != null) dataBuilder.append(assetNo);
                dataBuilder.append("|");
                if (assetName != null) dataBuilder.append(assetName);
                dataBuilder.append("|");
                if (serialNo != null) dataBuilder.append(serialNo);
                dataBuilder.append("|");
                if (modelNo != null) dataBuilder.append(modelNo);
                
                String data = dataBuilder.toString();
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
                    
                    // 支持多种日期格式
                    DateTimeFormatter[] formatters = new DateTimeFormatter[] {
                        DATE_FORMATTER,  // yyyy-MM-dd
                        DateTimeFormatter.ofPattern("yyyy/MM/dd"),  // yyyy/MM/dd
                        DateTimeFormatter.ofPattern("dd/MM/yyyy"),  // dd/MM/yyyy
                        DateTimeFormatter.ofPattern("MM/dd/yyyy"),  // MM/dd/yyyy
                        DateTimeFormatter.ofPattern("yyyy年MM月dd日")  // 中文格式
                    };
                    
                    // 尝试所有支持的格式
                    for (DateTimeFormatter formatter : formatters) {
                        try {
                            return LocalDate.parse(strValue, formatter);
                        } catch (Exception e) {
                            // 继续尝试下一种格式
                        }
                    }
                    
                    // 所有格式都失败，记录日志
                    log.warn("无法解析日期 '{}' 为任何支持的格式", strValue);
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