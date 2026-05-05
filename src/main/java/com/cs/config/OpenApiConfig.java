package com.cs.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI 配置类
 * 用于配置 Swagger UI 和 OpenAPI 3 文档
 *
 * @author LivePulse
 */
@Configuration
public class OpenApiConfig {

    @Value("${spring.application.name:google-analytic-open-connector-server}")
    private String applicationName;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Google Analytics 4 连接器 API")
                        .description("通过 BigQuery 获取 GA4 事件数据，支持标准事件和自定义事件订阅")
                        .version("2.1")
                        .contact(new Contact()
                                .name("LivePulse")
                                .email("support@livepuls.com.cn"))
                        .license(new License()
                                .name("LivePulse License")
                                .url("https://www.livepuls.com.cn")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:23006")
                                .description("本地开发环境"),
                        new Server()
                                .url("https://api.livepuls.com.cn")
                                .description("生产环境")
                ));
    }
}