package com.cs.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * GA4 BigQuery 配置属性
 *
 * @author LivePulse
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ga4.bigquery")
public class Ga4BigQueryConfig {

    /**
     * Google Cloud 项目 ID
     */
    private String projectId;

    /**
     * BigQuery 数据集 ID
     */
    private String datasetId;

    /**
     * 事件表名称（支持通配符，如 events_*）
     */
    private String eventTable;

    /**
     * 服务账号密钥文件路径
     */
    private String credentialsLocation;

    /**
     * 是否使用默认凭证（Workload Identity 或 gcloud auth）
     */
    private boolean useDefaultCredentials = false;
}