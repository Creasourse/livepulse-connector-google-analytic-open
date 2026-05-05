package com.cs.task;

import com.cs.config.ScheduledTaskConfig;
import com.cs.entity.Ga4Property;
import com.cs.service.Ga4BigQueryService;
import com.cs.service.Ga4PropertyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * GA4 数据同步调度任务
 * 按照配置的 Cron 表达式定期同步 GA4 事件数据
 *
 * @author LivePulse
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "ga4.scheduled", name = "enabled", havingValue = "true", matchIfMissing = true)
public class Ga4SyncScheduledTask {

    private final Ga4PropertyService propertyService;
    private final Ga4BigQueryService bigQueryService;
    private final ScheduledTaskConfig taskConfig;

    /**
     * 事件数据同步任务
     * 每天凌晨 1 点执行
     */
    @Scheduled(cron = "${ga4.scheduled.event-sync.cron:0 0 1 * * ?}")
    public void syncEventData() {
        if (!taskConfig.getEventSync().isEnabled()) {
            log.debug("事件数据同步任务已禁用");
            return;
        }

        log.info("开始执行 GA4 事件数据同步任务");

        try {
            // 查询所有启用的属性
            var properties = propertyService.findEnabled();

            if (properties.isEmpty()) {
                log.info("没有启用的 GA4 属性，跳过同步");
                return;
            }

            // 同步前一天的数据
            LocalDate yesterday = LocalDate.now().minusDays(1);

            for (Ga4Property property : properties) {
                try {
                    log.info("开始同步属性 {} 的数据，日期: {}",
                            property.getPropertyName(), yesterday);

                    bigQueryService.syncEvents(property.getId(), yesterday, yesterday);

                    log.info("属性 {} 数据同步完成", property.getPropertyName());

                } catch (Exception e) {
                    log.error("同步属性 {} 数据失败: {}",
                            property.getPropertyName(), e.getMessage(), e);
                }
            }

            log.info("GA4 事件数据同步任务完成");

        } catch (Exception e) {
            log.error("GA4 事件数据同步任务执行失败", e);
        }
    }

    /**
     * 用户属性同步任务
     * 每天凌晨 2 点执行
     */
    @Scheduled(cron = "${ga4.scheduled.user-property-sync.cron:0 0 2 * * ?}")
    public void syncUserProperties() {
        if (!taskConfig.getUserPropertySync().isEnabled()) {
            log.debug("用户属性同步任务已禁用");
            return;
        }

        log.info("开始执行 GA4 用户属性同步任务");

        // TODO: 实现用户属性同步逻辑

        log.info("GA4 用户属性同步任务完成");
    }
}