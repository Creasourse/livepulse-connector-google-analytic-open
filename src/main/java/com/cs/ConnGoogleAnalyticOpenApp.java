package com.cs;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Google Analytics 4 连接器应用主类
 * 通过 BigQuery 获取 GA4 事件数据，支持标准事件和自定义事件订阅，实现用户行为分析
 *
 * @author LivePulse
 * @version 2.1
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
@MapperScan("com.cs.mapper")
public class ConnGoogleAnalyticOpenApp {

    public static void main(String[] args) {
        SpringApplication.run(ConnGoogleAnalyticOpenApp.class, args);
    }
}