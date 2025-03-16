# Excel资产导入功能实现思路 (实际实现版本)

针对Excel资产导入功能，我们采用以下方案设计和实现：

## 1. 整体架构设计

采用分层架构设计这个功能：

- **Controller层**：接收前端上传的Excel文件，返回导入任务ID
- **Service层**：处理Excel文件解析与数据验证逻辑，执行异步处理
- **Repository层**：持久化处理后的资产数据，支持批量操作

## 2. 技术选型

### Excel解析技术

- Apache POI：功能完整的Excel处理库
- 自定义包装类：ExcelParser和RowProcessor接口提供流式处理

### 数据处理策略

- 异步处理：使用Spring的@Async注解实现异步任务处理
- 任务状态跟踪：使用内存状态映射表记录导入进度
- 事务管理：确保每行数据处理的原子性

## 3. 功能实现步骤

### 3.1 文件上传接收

```java
@PostMapping("/import")
public ResponseEntity<Map<String, String>> importExcel(@RequestParam("file") MultipartFile file) {
    try {
        String taskId = importService.importExcelAsync(file).get();
        return ResponseEntity.ok(Map.of("taskId", taskId));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
    }
}
```

### 3.2 Excel文件解析

1. 封装Excel解析逻辑到专用的ExcelParser类
2. 使用RowProcessor接口处理每一行数据
3. 构建内部类AssetRowProcessor实现接口方法

```java
public interface RowProcessor {
    boolean processRow(Map<String, Object> rowData, int rowIndex);
}
```

### 3.3 数据验证与去重

实现了多层次的数据验证和去重逻辑：

1. **行级数据指纹**
   ```java
   // 生成行数据的哈希值用于去重
   private String generateRowHash(Map<String, Object> rowData) {
       String assetNo = getStringOrNull(rowData.get("资产编号"));
       String assetName = getStringOrNull(rowData.get("资产名称"));
       String serialNo = getStringOrNull(rowData.get("序列号"));
       
       String data = assetNo + "|" + assetName + "|" + serialNo;
       MessageDigest md = MessageDigest.getInstance("SHA-256");
       // ... 生成哈希值
   }
   ```

2. **重复资产检查**
   ```java
   // 检查是否已存在相同的数据指纹
   Optional<AssetMaster> existingAsset = assetRepository.findByExcelRowHash(rowHash);
   if (existingAsset.isPresent()) {
       log.info("跳过重复数据: {}", rowData.get("资产编号"));
       return true; // 跳过已导入的行，但视为成功
   }
   ```

3. **维保合同号去重**
   ```java
   // 检查是否已存在相同合同号的维保记录
   String contractNo = warranty.getContractNo();
   Optional<WarrantyContract> existingWarranty = warrantyService.findByContractNo(contractNo);
   
   if (existingWarranty.isPresent()) {
       // 如果已存在，则更新现有记录而不是创建新记录
       WarrantyContract existing = existingWarranty.get();
       // 更新现有记录的字段
       existing.setStartDate(warranty.getStartDate());
       existing.setEndDate(warranty.getEndDate());
       // ... 更新其他字段
       
       savedWarranty = warrantyService.saveWarranty(existing);
       log.info("更新已存在的维保合约: {}", contractNo);
   } else {
       // 如果不存在，则设置资产关联并保存新记录
       warranty.setAsset(savedAsset);
       savedWarranty = warrantyService.saveWarranty(warranty);
   }
   ```

### 3.4 优化实体保存顺序

为解决JPA级联保存中的循环依赖问题，采用特定的保存顺序：

```java
// 1. 先保存资产主记录 - 不设置关联关系
// 移除关联，避免Hibernate尝试级联保存未持久化的对象
asset.setSpace(null);
asset.setWarranty(null);
AssetMaster savedAsset = assetRepository.save(asset);

// 2. 再保存关联实体
// 现在可以安全地保存空间记录，因为AssetMaster已经持久化
currentSpace.setAsset(savedAsset);
SpaceTimeline savedSpace = spaceService.saveSpace(currentSpace);

// 3. 最后更新资产记录中的空间引用
savedAsset.setSpace(savedSpace);
assetRepository.save(savedAsset);
```

### 3.5 多格式日期处理

支持多种常见日期格式的自动识别和解析：

```java
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
```

### 3.6 变更记录自动化

自动记录各类资产变更，提供完整变更历史：

```java
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
```

## 4. 导入进度跟踪

实现了完整的导入进度跟踪系统：

```java
@GetMapping("/progress/{taskId}")
public ResponseEntity<ImportProgressDTO> getImportProgress(@PathVariable String taskId) {
    ImportProgressDTO progress = importService.getImportProgress(taskId);
    if (progress == null) {
        return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(progress);
}
```

导入进度DTO对象：

```java
public class ImportProgressDTO {
    private String taskId;
    private String state;
    private double progress;
    private int totalRows;
    private int processedRows;
    private int successRows;
    private int failedRows;
    private List<String> errors;
    private String error;
}
```

## 5. 导入结果统计

完成导入后提供结果统计信息：

```java
@GetMapping("/result/{batchId}")
public ResponseEntity<ImportResultDTO> getImportResult(@PathVariable String batchId) {
    ImportResultDTO result = importService.getImportResult(batchId);
    return ResponseEntity.ok(result);
}
```

## 6. 实际优化经验

通过实际实现，我们发现了并解决了以下关键问题：

1. **JPA级联保存问题**
   - 避免双向关联的循环引用问题
   - 采用先保存主实体，再保存关联实体的方式

2. **并发处理考量**
   - 使用批次ID标记每次导入操作
   - 异步处理大量数据，不阻塞用户界面

3. **数据一致性保障**
   - 使用事务确保每行数据处理的原子性
   - 实现合同号去重机制，优先更新现有合同

4. **容错性设计**
   - 记录每一行的处理状态和错误信息
   - 单行错误不影响整体导入过程

5. **效率优化**
   - 使用SHA-256哈希快速判断重复
   - 批量提交减少数据库写入次数

## 7. 未来优化方向

基于当前实现，未来可考虑的优化方向：

1. 使用消息队列替代内存中的导入队列，提高可靠性
2. 实现基于WebSocket的实时进度推送
3. 添加可配置的字段映射，支持多种Excel模板
4. 增加并行处理能力，加速大文件导入