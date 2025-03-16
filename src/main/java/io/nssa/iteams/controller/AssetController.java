package io.nssa.iteams.controller;

import io.nssa.iteams.entity.AssetMaster;
import io.nssa.iteams.service.AssetService;
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

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/assets")
public class AssetController {

    @Autowired
    private AssetService assetService;

    @GetMapping
    public ResponseEntity<List<AssetMaster>> getAllAssets() {
        return ResponseEntity.ok(assetService.getAllAssets());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssetMaster> getAssetById(@PathVariable Long id) {
        return assetService.getAssetById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/uuid/{uuid}")
    public ResponseEntity<AssetMaster> getAssetByUuid(@PathVariable String uuid) {
        return assetService.getAssetByUuid(uuid)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/no/{assetNo}")
    public ResponseEntity<AssetMaster> getAssetByNo(@PathVariable String assetNo) {
        return assetService.getAssetByNo(assetNo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AssetMaster> createAsset(
            @RequestBody AssetMaster asset,
            @RequestHeader("X-Operator-Id") Long operatorId) {
        return new ResponseEntity<>(assetService.createAsset(asset, operatorId), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AssetMaster> updateAsset(
            @PathVariable Long id,
            @RequestBody AssetMaster asset,
            @RequestHeader("X-Operator-Id") Long operatorId) {
        if (!id.equals(asset.getAssetId())) {
            return ResponseEntity.badRequest().build();
        }

        return assetService.getAssetById(id)
                .map(existingAsset -> ResponseEntity.ok(assetService.updateAsset(asset, operatorId)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<AssetMaster>> getAssetsByStatus(
            @PathVariable AssetMaster.AssetStatus status) {
        return ResponseEntity.ok(assetService.getAssetsByStatus(status));
    }

    // Excel 导出功能
    @GetMapping("/export/excel")
    public ResponseEntity<Resource> exportToExcel() {
        List<AssetMaster> assets = assetService.getAllAssets();

        InputStreamResource file = new InputStreamResource(ExcelHelper.assetsToExcel(assets));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=assets.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(file);
    }

    // Excel 导入功能
    @PostMapping("/import/excel")
    public ResponseEntity<?> importFromExcel(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("X-Operator-Id") Long operatorId) {
        if (ExcelHelper.isExcelFile(file)) {
            try {
                List<AssetMaster> assetList = ExcelHelper.excelToAssets(file.getInputStream());
                for (AssetMaster asset : assetList) {
                    assetService.createAsset(asset, operatorId);
                }

                return ResponseEntity.status(HttpStatus.OK)
                        .body(Map.of("message", "导入成功: " + assetList.size() + "条资产记录"));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                        .body(Map.of("message", "导入失败: " + e.getMessage()));
            }
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", "请上传Excel文件(.xlsx)"));
    }
}