package com.cs.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * GA4 数据同步配置属性
 *
 * @author LivePulse
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ga4.sync")
public class Ga4SyncConfig {

    /**
     * 每次查询的最大天数（BigQuery 限制）
     */
    private int maxQueryDays = 30;

    /**
     * 每次查询的批次大小（行数）
     */
    private int batchSize = 10000;

    /**
     * 查询超时时间（秒）
     */
    private int queryTimeout = 300;
}