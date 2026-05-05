package com.cs.controller;

import com.cs.dto.Ga4EventDto;
import com.cs.entity.Ga4Event;
import com.cs.service.Ga4BigQueryService;
import com.cs.service.Ga4EventSubscriptionService;
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
 * GA4 事件控制器
 *
 * @author LivePulse
 */
@Tag(name = "GA4 事件管理", description = "Google Analytics 4 事件数据查询接口")
@RestController
@RequestMapping("/api/ga4/events")
@RequiredArgsConstructor
public class Ga4EventController {

    private final Ga4BigQueryService bigQueryService;
    private final Ga4EventSubscriptionService subscriptionService;

    @Operation(summary = "查询事件数据", description = "从 BigQuery 查询指定日期范围的事件数据")
    @GetMapping("/query")
    public ResponseEntity<List<Ga4EventDto>> queryEvents(
            @Parameter(description = "GA4 属性 ID") @RequestParam Long ga4PropertyId,
            @Parameter(description = "开始日期 (yyyy-MM-dd)") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期 (yyyy-MM-dd)") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        List<Ga4EventDto> events = bigQueryService.queryEvents(ga4PropertyId, startDate, endDate);
        return ResponseEntity.ok(events);
    }

    @Operation(summary = "按事件名称查询", description = "根据事件名称查询事件数据")
    @GetMapping("/query/by-name")
    public ResponseEntity<List<Ga4EventDto>> queryEventsByName(
            @Parameter(description = "GA4 属性 ID") @RequestParam Long ga4PropertyId,
            @Parameter(description = "事件名称") @RequestParam String eventName,
            @Parameter(description = "开始日期 (yyyy-MM-dd)") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期 (yyyy-MM-dd)") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        List<Ga4EventDto> events = bigQueryService.queryEventsByName(
                ga4PropertyId, eventName, startDate, endDate);
        return ResponseEntity.ok(events);
    }

    @Operation(summary = "检查事件订阅", description = "检查指定事件是否已订阅")
    @GetMapping("/check-subscription")
    public ResponseEntity<Boolean> checkSubscription(
            @Parameter(description = "GA4 属性 ID") @RequestParam Long ga4PropertyId,
            @Parameter(description = "事件名称") @RequestParam String eventName) {
        boolean subscribed = subscriptionService.isEventSubscribed(ga4PropertyId, eventName);
        return ResponseEntity.ok(subscribed);
    }
}