package com.cs.controller;

import com.cs.dto.Ga4SyncResultDto;
import com.cs.entity.Ga4SyncLog;
import com.cs.service.Ga4BigQueryService;
import com.cs.service.Ga4SyncLogService;
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
 * GA4 同步控制器
 *
 * @author LivePulse
 */
@Tag(name = "GA4 数据同步", description = "Google Analytics 4 数据同步管理接口")
@RestController
@RequestMapping("/api/ga4/sync")
@RequiredArgsConstructor
public class Ga4SyncController {

    private final Ga4BigQueryService bigQueryService;
    private final Ga4SyncLogService syncLogService;

    @Operation(summary = "触发同步", description = "手动触发指定日期范围的数据同步")
    @PostMapping("/trigger")
    public ResponseEntity<Ga4SyncResultDto> triggerSync(
            @Parameter(description = "GA4 属性 ID") @RequestParam Long ga4PropertyId,
            @Parameter(description = "开始日期 (yyyy-MM-dd)") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期 (yyyy-MM-dd)") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        Ga4SyncResultDto result = bigQueryService.syncEvents(ga4PropertyId, startDate, endDate);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "查询同步日志", description = "查询指定属性的同步日志")
    @GetMapping("/logs")
    public ResponseEntity<List<Ga4SyncLog>> getSyncLogs(
            @Parameter(description = "GA4 属性 ID") @RequestParam Long ga4PropertyId,
            @Parameter(description = "限制数量") @RequestParam(defaultValue = "10") int limit) {
        List<Ga4SyncLog> logs = syncLogService.findByPropertyId(ga4PropertyId, limit);
        return ResponseEntity.ok(logs);
    }

    @Operation(summary = "查询同步日志详情", description = "根据 ID 查询同步日志详情")
    @GetMapping("/logs/{id}")
    public ResponseEntity<Ga4SyncLog> getSyncLogDetail(
            @Parameter(description = "日志 ID") @PathVariable Long id) {
        Ga4SyncLog log = syncLogService.findById(id);
        if (log == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(log);
    }
}