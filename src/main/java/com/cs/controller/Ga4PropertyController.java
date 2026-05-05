package com.cs.controller;

import com.cs.dto.Ga4SyncResultDto;
import com.cs.entity.Ga4Property;
import com.cs.service.Ga4PropertyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * GA4 属性控制器
 *
 * @author LivePulse
 */
@Tag(name = "GA4 属性管理", description = "Google Analytics 4 属性配置管理接口")
@RestController
@RequestMapping("/api/ga4/properties")
@RequiredArgsConstructor
public class Ga4PropertyController {

    private final Ga4PropertyService ga4PropertyService;

    @Operation(summary = "查询属性列表", description = "查询所有启用的 GA4 属性")
    @GetMapping("/enabled")
    public ResponseEntity<List<Ga4Property>> getEnabledProperties() {
        List<Ga4Property> properties = ga4PropertyService.findEnabled();
        return ResponseEntity.ok(properties);
    }

    @Operation(summary = "查询属性详情", description = "根据 ID 查询 GA4 属性详情")
    @GetMapping("/{id}")
    public ResponseEntity<Ga4Property> getProperty(
            @Parameter(description = "属性 ID") @PathVariable Long id) {
        Ga4Property property = ga4PropertyService.findById(id);
        if (property == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(property);
    }

    @Operation(summary = "创建属性", description = "创建新的 GA4 属性配置")
    @PostMapping
    public ResponseEntity<Ga4Property> createProperty(@RequestBody Ga4Property ga4Property) {
        Ga4Property created = ga4PropertyService.create(ga4Property);
        return ResponseEntity.ok(created);
    }

    @Operation(summary = "更新属性", description = "更新 GA4 属性配置")
    @PutMapping("/{id}")
    public ResponseEntity<Ga4Property> updateProperty(
            @Parameter(description = "属性 ID") @PathVariable Long id,
            @RequestBody Ga4Property ga4Property) {
        ga4Property.setId(id);
        Ga4Property updated = ga4PropertyService.update(ga4Property);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "删除属性", description = "删除 GA4 属性配置")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProperty(
            @Parameter(description = "属性 ID") @PathVariable Long id) {
        ga4PropertyService.delete(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "测试连接", description = "测试 BigQuery 连接是否正常")
    @PostMapping("/{id}/test-connection")
    public ResponseEntity<Boolean> testConnection(
            @Parameter(description = "属性 ID") @PathVariable Long id) {
        boolean result = ga4PropertyService.testConnection(id);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "手动同步", description = "手动触发数据同步")
    @PostMapping("/{id}/sync")
    public ResponseEntity<Ga4SyncResultDto> manualSync(
            @Parameter(description = "属性 ID") @PathVariable Long id,
            @Parameter(description = "开始日期 (yyyy-MM-dd)") @RequestParam String startDate,
            @Parameter(description = "结束日期 (yyyy-MM-dd)") @RequestParam String endDate) {
        Ga4SyncResultDto result = ga4PropertyService.manualSync(id, startDate, endDate);
        return ResponseEntity.ok(result);
    }
}