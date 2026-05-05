package com.cs.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * BigQuery 配置类
 * 用于配置 Google BigQuery 客户端
 *
 * @author LivePulse
 */
@Slf4j
@Configuration
public class BigQueryConfig {

    @Value("${ga4.bigquery.project-id}")
    private String projectId;

    @Value("${ga4.bigquery.use-default-credentials:false}")
    private boolean useDefaultCredentials;

    @Value("${ga4.bigquery.credentials-location:}")
    private String credentialsLocation;

    /**
     * 创建 BigQuery 客户端 Bean
     * 优先使用默认凭证（Workload Identity 或 gcloud auth）
     * 如果配置了凭证文件路径，则使用指定的服务账号密钥文件
     */
    @Bean
    @ConditionalOnMissingBean
    public BigQuery bigQuery() throws IOException {
        BigQueryOptions.Builder builder = BigQueryOptions.newBuilder();

        if (useDefaultCredentials) {
            log.info("使用默认 Google 凭证（Workload Identity 或 gcloud auth）");
            builder.setProjectId(projectId);
        } else if (credentialsLocation != null && !credentialsLocation.isEmpty()) {
            log.info("使用服务账号密钥文件: {}", credentialsLocation);
            GoogleCredentials credentials;
            if (credentialsLocation.startsWith("classpath:")) {
                // 从 classpath 加载
                String resourcePath = credentialsLocation.substring("classpath:".length());
                credentials = GoogleCredentials.fromStream(new ClassPathResource(resourcePath).getInputStream());
            } else {
                // 从文件系统加载
                credentials = GoogleCredentials.fromStream(new FileInputStream(credentialsLocation));
            }
            builder.setProjectId(projectId).setCredentials(credentials);
        } else {
            log.warn("未配置 BigQuery 凭证，尝试使用默认凭证");
            builder.setProjectId(projectId);
        }

        return builder.build().getService();
    }
}