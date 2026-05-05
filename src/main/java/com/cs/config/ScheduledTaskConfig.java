package com.cs.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * GA4 调度任务配置属性
 *
 * @author LivePulse
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ga4.scheduled")
public class ScheduledTaskConfig {

    /**
     * 是否启用调度任务
     */
    private boolean enabled = true;

    /**
     * 事件数据同步配置
     */
    private EventSyncConfig eventSync = new EventSyncConfig();

    /**
     * 用户属性同步配置
     */
    private UserPropertySyncConfig userPropertySync = new UserPropertySyncConfig();

    @Data
    public static class EventSyncConfig {
        private boolean enabled = true;
        private String cron = "0 0 1 * * ?";
        private long initialDelay = 30;
        private long fixedDelay = 1800;
    }

    @Data
    public static class UserPropertySyncConfig {
        private boolean enabled = true;
        private String cron = "0 0 2 * * ?";
        private long initialDelay = 60;
        private long fixedDelay = 3600;
    }
}