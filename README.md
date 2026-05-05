# Google Analytics 4 连接器

通过 BigQuery 获取 GA4 事件数据，支持标准事件和自定义事件订阅，实现用户行为分析。

## 版本

v2.1

## 功能特性

- 📊 **BigQuery 集成**：通过 Google BigQuery 获取 GA4 导出的事件数据
- 🔄 **自动同步**：支持定时自动同步 GA4 事件数据
- 🎯 **事件订阅**：支持订阅标准事件和自定义事件
- 📈 **用户行为分析**：提供用户行为数据分析能力
- 🔐 **安全认证**：支持 Workload Identity 和服务账号密钥认证

## 技术栈

- Spring Boot 3.x
- Google Cloud BigQuery
- MyBatis
- Nacos (配置中心/注册中心)
- Kafka
- Swagger/OpenAPI

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.8+
- Google Cloud 项目（已启用 BigQuery API）
- GA4 属性已配置 BigQuery 导出

### 配置说明

在 `bootstrap.yml` 中配置以下参数：

```yaml
ga4:
  bigquery:
    project-id: your-gcp-project-id
    dataset-id: analytics_123456789
    event-table: events_*
    use-default-credentials: false
    credentials-location: /path/to/service-account-key.json
```

### 构建

```bash
mvn clean package
```

### 运行

```bash
java -jar target/livepulse-connector-google-analytic-open.jar
```

## API 文档

启动服务后访问 Swagger UI：

```
http://localhost:23006/swagger-ui.html
```

## 核心接口

### GA4 属性管理

- `GET /api/ga4/properties/enabled` - 查询所有启用的属性
- `GET /api/ga4/properties/{id}` - 查询属性详情
- `POST /api/ga4/properties` - 创建属性
- `PUT /api/ga4/properties/{id}` - 更新属性
- `DELETE /api/ga4/properties/{id}` - 删除属性

### 数据同步

- `POST /api/ga4/sync/trigger` - 手动触发数据同步
- `GET /api/ga4/sync/logs` - 查询同步日志

### 事件查询

- `GET /api/ga4/events/query` - 查询事件数据
- `GET /api/ga4/events/query/by-name` - 按事件名称查询

## 调度任务

系统内置以下调度任务：

- **事件数据同步**：每天凌晨 1 点执行，同步前一天的事件数据
- **用户属性同步**：每天凌晨 2 点执行（待实现）

## 支持

如有问题请联系：support@livepuls.com.cn

## 许可证

LivePulse License