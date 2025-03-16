package io.nssa.iteams.controller;

import io.nssa.iteams.entity.WarrantyContract;
import io.nssa.iteams.service.WarrantyService;
import io.nssa.iteams.util.ExcelHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/warranties")
public class WarrantyController {
  private final WarrantyService warrantyService;

  @Autowired
  public WarrantyController(WarrantyService warrantyService) {
    this.warrantyService = warrantyService;
  }

  @GetMapping
  public ResponseEntity<List<WarrantyContract>> getAllWarranties() {
    return ResponseEntity.ok(warrantyService.getAllWarranties());
  }

  @GetMapping("/{id}")
  public ResponseEntity<WarrantyContract> getWarrantyById(@PathVariable Long id) {
    return warrantyService
        .getWarrantyById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/contract/{contractNo}")
  public ResponseEntity<WarrantyContract> getWarrantyByContractNo(@PathVariable String contractNo) {
    return warrantyService
        .getWarrantyByContractNo(contractNo)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/asset/{assetId}")
  public ResponseEntity<List<WarrantyContract>> getWarrantiesByAssetId(@PathVariable Long assetId) {
    return ResponseEntity.ok(warrantyService.getWarrantiesByAssetId(assetId));
  }

  @GetMapping("/asset/{assetId}/valid")
  public ResponseEntity<List<WarrantyContract>> getValidWarrantiesByAssetId(
      @PathVariable Long assetId) {
    return ResponseEntity.ok(warrantyService.getValidWarrantiesByAssetId(assetId));
  }

  @PostMapping
  public ResponseEntity<WarrantyContract> createWarranty(
      @RequestBody WarrantyContract warranty, @RequestHeader("X-Operator-Id") Long operatorId) {
    return new ResponseEntity<>(
        warrantyService.createWarranty(warranty, operatorId), HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity<WarrantyContract> updateWarranty(
      @PathVariable Long id,
      @RequestBody WarrantyContract warranty,
      @RequestHeader("X-Operator-Id") Long operatorId) {
    if (!id.equals(warranty.getWarrantyId())) {
      return ResponseEntity.badRequest().build();
    }

    return warrantyService
        .getWarrantyById(id)
        .map(
            existingWarranty ->
                ResponseEntity.ok(warrantyService.updateWarranty(warranty, operatorId)))
        .orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteWarranty(@PathVariable Long id) {
    if (warrantyService.getWarrantyById(id).isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    warrantyService.deleteWarranty(id);
    return ResponseEntity.noContent().build();
  }

  // Excel 导出功能
  @GetMapping("/export/excel")
  public ResponseEntity<Resource> exportToExcel() {
    List<WarrantyContract> warranties = warrantyService.getAllWarranties();

    InputStreamResource file = new InputStreamResource(ExcelHelper.warrantiesToExcel(warranties));

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=warranties.xlsx")
        .contentType(
            MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
        .body(file);
  }

  // 根据资产ID导出保修合同
  @GetMapping("/export/excel/asset/{assetId}")
  public ResponseEntity<Resource> exportAssetWarrantiesToExcel(@PathVariable Long assetId) {
    List<WarrantyContract> warranties = warrantyService.getWarrantiesByAssetId(assetId);

    InputStreamResource file = new InputStreamResource(ExcelHelper.warrantiesToExcel(warranties));

    return ResponseEntity.ok()
        .header(
            HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=asset_" + assetId + "_warranties.xlsx")
        .contentType(
            MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
        .body(file);
  }

  // Excel 导入功能
  @PostMapping("/import/excel")
  public ResponseEntity<?> importFromExcel(
      @RequestParam("file") MultipartFile file, @RequestHeader("X-Operator-Id") Long operatorId) {
    if (ExcelHelper.isExcelFile(file)) {
      try {
        List<WarrantyContract> warrantyList = ExcelHelper.excelToWarranties(file.getInputStream());
        for (WarrantyContract warranty : warrantyList) {
          warrantyService.createWarranty(warranty, operatorId);
        }

        return ResponseEntity.status(HttpStatus.OK)
            .body(Map.of("message", "导入成功: " + warrantyList.size() + "条保修合同记录"));
      } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
            .body(Map.of("message", "导入失败: " + e.getMessage()));
      }
    }

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(Map.of("message", "请上传Excel文件(.xlsx)"));
  }

  // 获取即将过期的保修合同
  @GetMapping("/expiring")
  public ResponseEntity<List<WarrantyContract>> getExpiringWarranties(
      @RequestParam(defaultValue = "30") Integer daysThreshold) {
    LocalDate thresholdDate = LocalDate.now().plusDays(daysThreshold);
    return ResponseEntity.ok(warrantyService.getExpiringWarranties(thresholdDate));
  }
}
