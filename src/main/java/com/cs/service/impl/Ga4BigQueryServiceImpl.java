package com.cs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cs.config.Ga4BigQueryConfig;
import com.cs.config.Ga4SyncConfig;
import com.cs.dto.Ga4EventDto;
import com.cs.dto.Ga4SyncResultDto;
import com.cs.entity.Ga4Event;
import com.cs.entity.Ga4Property;
import com.cs.entity.Ga4SyncLog;
import com.cs.mapper.Ga4EventMapper;
import com.cs.mapper.Ga4PropertyMapper;
import com.cs.mapper.Ga4SyncLogMapper;
import com.cs.service.Ga4BigQueryService;
import com.cs.service.Ga4SyncLogService;
import com.google.cloud.bigquery.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * GA4 BigQuery 服务实现
 *
 * @author LivePulse
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class Ga4BigQueryServiceImpl implements Ga4BigQueryService {

    private final BigQuery bigQuery;
    private final Ga4BigQueryConfig bigQueryConfig;
    private final Ga4SyncConfig syncConfig;
    private final Ga4PropertyMapper propertyMapper;
    private final Ga4EventMapper eventMapper;
    private final Ga4SyncLogMapper syncLogMapper;
    private final Ga4SyncLogService syncLogService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    @Transactional
    public Ga4SyncResultDto syncEvents(Long ga4PropertyId, LocalDate startDate, LocalDate endDate) {
        Ga4Property property = propertyMapper.selectById(ga4PropertyId);
        if (property == null) {
            return Ga4SyncResultDto.builder()
                    .ga4PropertyId(ga4PropertyId)
                    .syncStatus("failed")
                    .message("GA4 属性不存在")
                    .build();
        }

        // 记录同步开始
        Ga4SyncLog syncLog = syncLogService.logStart(
                ga4PropertyId,
                "event",
                startDate.format(DATE_FORMATTER),
                endDate.format(DATE_FORMATTER)
        );

        long startTime = System.currentTimeMillis();
        try {
            // 查询事件数据
            List<Ga4EventDto> events = queryEvents(ga4PropertyId, startDate, endDate);

            // 批量插入数据库
            int batchSize = syncConfig.getBatchSize();
            List<Ga4EventDto> batch = new ArrayList<>(batchSize);
            int successCount = 0;
            int failureCount = 0;

            for (Ga4EventDto eventDto : events) {
                batch.add(eventDto);

                if (batch.size() >= batchSize) {
                    int inserted = insertBatch(batch, property);
                    successCount += inserted;
                    failureCount += (batch.size() - inserted);
                    batch.clear();
                }
            }

            // 插入剩余的数据
            if (!batch.isEmpty()) {
                int inserted = insertBatch(batch, property);
                successCount += inserted;
                failureCount += (batch.size() - inserted);
            }

            long duration = System.currentTimeMillis() - startTime;

            // 记录同步成功
            syncLogService.logSuccess(syncLog.getId(), (long) successCount, (long) failureCount);

            return Ga4SyncResultDto.builder()
                    .ga4PropertyId(ga4PropertyId)
                    .syncStatus("success")
                    .startDate(startDate.format(DATE_FORMATTER))
                    .endDate(endDate.format(DATE_FORMATTER))
                    .totalCount((long) events.size())
                    .successCount((long) successCount)
                    .failureCount((long) failureCount)
                    .duration(duration)
                    .message(String.format("同步完成，共 %d 条记录", events.size()))
                    .build();

        } catch (Exception e) {
            log.error("同步 GA4 事件数据失败", e);
            syncLogService.logFailure(syncLog.getId(), e.getMessage());

            return Ga4SyncResultDto.builder()
                    .ga4PropertyId(ga4PropertyId)
                    .syncStatus("failed")
                    .errorMessage(e.getMessage())
                    .duration(System.currentTimeMillis() - startTime)
                    .build();
        }
    }

    @Override
    public List<Ga4EventDto> queryEvents(Long ga4PropertyId, LocalDate startDate, LocalDate endDate) {
        Ga4Property property = propertyMapper.selectById(ga4PropertyId);
        if (property == null) {
            throw new IllegalArgumentException("GA4 属性不存在");
        }

        String projectId = property.getBigqueryProjectId() != null ?
                property.getBigqueryProjectId() : bigQueryConfig.getProjectId();
        String datasetId = property.getBigqueryDatasetId() != null ?
                property.getBigqueryDatasetId() : bigQueryConfig.getDatasetId();
        String eventTable = bigQueryConfig.getEventTable();

        // 构建 SQL 查询
        String sql = buildQuerySql(datasetId, eventTable, startDate, endDate, null);

        return executeQuery(projectId, sql);
    }

    @Override
    public List<Ga4EventDto> queryEventsByName(Long ga4PropertyId, String eventName,
                                                 LocalDate startDate, LocalDate endDate) {
        Ga4Property property = propertyMapper.selectById(ga4PropertyId);
        if (property == null) {
            throw new IllegalArgumentException("GA4 属性不存在");
        }

        String projectId = property.getBigqueryProjectId() != null ?
                property.getBigqueryProjectId() : bigQueryConfig.getProjectId();
        String datasetId = property.getBigqueryDatasetId() != null ?
                property.getBigqueryDatasetId() : bigQueryConfig.getDatasetId();
        String eventTable = bigQueryConfig.getEventTable();

        // 构建 SQL 查询（带事件名称过滤）
        String sql = buildQuerySql(datasetId, eventTable, startDate, endDate, eventName);

        return executeQuery(projectId, sql);
    }

    @Override
    public boolean testConnection(Long ga4PropertyId) {
        Ga4Property property = propertyMapper.selectById(ga4PropertyId);
        if (property == null) {
            return false;
        }

        try {
            String projectId = property.getBigqueryProjectId() != null ?
                    property.getBigqueryProjectId() : bigQueryConfig.getProjectId();
            String datasetId = property.getBigqueryDatasetId() != null ?
                    property.getBigqueryDatasetId() : bigQueryConfig.getDatasetId();

            // 简单查询测试连接
            String sql = String.format(
                    "SELECT 1 FROM `%s.%s.%s` LIMIT 1",
                    projectId, datasetId, bigQueryConfig.getEventTable().replace("*", "20240101")
            );

            QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(sql)
                    .build();

            bigQuery.query(queryConfig);

            return true;
        } catch (Exception e) {
            log.error("BigQuery 连接测试失败", e);
            return false;
        }
    }

    /**
     * 构建 SQL 查询语句
     */
    private String buildQuerySql(String datasetId, String eventTable,
                                  LocalDate startDate, LocalDate endDate, String eventName) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT\n");
        sql.append("  event_name,\n");
        sql.append("  event_timestamp,\n");
        sql.append("  event_date,\n");
        sql.append("  event_time,\n");
        sql.append("  session_id,\n");
        sql.append("  user_id,\n");
        sql.append("  user_pseudo_id as client_id,\n");
        sql.append("  event_params,\n");
        sql.append("  user_properties,\n");
        sql.append("  device.category as device_category,\n");
        sql.append("  device.browser,\n");
        sql.append("  device.operating_system,\n");
        sql.append("  geo.country,\n");
        sql.append("  geo.city,\n");
        sql.append("  page_location,\n");
        sql.append("  page_title,\n");
        sql.append("  page_referrer,\n");
        sql.append("  traffic_source.source as campaign_source,\n");
        sql.append("  traffic_source.medium as campaign_medium,\n");
        sql.append("  traffic_source.name as campaign_name,\n");
        sql.append("  event_value\n");
        sql.append("FROM `").append(datasetId).append(".").append(eventTable).append("`\n");

        List<String> conditions = new ArrayList<>();
        conditions.add("event_date >= '" + startDate.format(DATE_FORMATTER) + "'");
        conditions.add("event_date <= '" + endDate.format(DATE_FORMATTER) + "'");

        if (eventName != null && !eventName.isEmpty()) {
            conditions.add("event_name = '" + eventName + "'");
        }

        if (!conditions.isEmpty()) {
            sql.append("WHERE ").append(String.join(" AND ", conditions)).append("\n");
        }

        sql.append("ORDER BY event_timestamp DESC");

        return sql.toString();
    }

    /**
     * 执行查询
     */
    private List<Ga4EventDto> executeQuery(String projectId, String sql) {
        List<Ga4EventDto> events = new ArrayList<>();

        try {
            QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(sql)
                    .setUseQueryCache(false)
                    .build();

            // 使用 BigQuery 直接执行查询（推荐方式）
            // 设置查询超时时间
            JobId jobId = JobId.of(String.valueOf(System.currentTimeMillis()));
            Job job = bigQuery.create(JobInfo.newBuilder(queryConfig).setJobId(jobId).build());

            // 等待查询完成
            job = job.waitFor();

            // 检查是否有错误
            if (job.getStatus().getError() != null) {
                throw new RuntimeException("BigQuery 查询失败: " + job.getStatus().getError().getMessage());
            }

            // 获取查询结果
            TableResult result = job.getQueryResults();

            // 处理结果
            for (FieldValueList row : result.iterateAll()) {
                Ga4EventDto event = convertRowToEvent(row);
                if (event != null) {
                    events.add(event);
                }
            }

            log.info("BigQuery 查询完成，返回 {} 条记录", events.size());

        } catch (InterruptedException e) {
            log.error("BigQuery 查询被中断", e);
            Thread.currentThread().interrupt();
            throw new RuntimeException("BigQuery 查询被中断: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("执行 BigQuery 查询失败", e);
            throw new RuntimeException("执行 BigQuery 查询失败: " + e.getMessage(), e);
        }

        return events;
    }

    /**
     * 将查询结果行转换为 Ga4EventDto
     */
    private Ga4EventDto convertRowToEvent(FieldValueList row) {
        try {
            return Ga4EventDto.builder()
                    .eventName(getStringValue(row, "event_name"))
                    .eventTimestamp(getLongValue(row, "event_timestamp"))
                    .eventDate(getStringValue(row, "event_date"))
                    .eventTime(getStringValue(row, "event_time"))
                    .sessionId(getStringValue(row, "session_id"))
                    .userId(getStringValue(row, "user_id"))
                    .clientId(getStringValue(row, "client_id"))
                    .deviceCategory(getStringValue(row, "device_category"))
                    .browser(getStringValue(row, "browser"))
                    .operatingSystem(getStringValue(row, "operating_system"))
                    .country(getStringValue(row, "country"))
                    .city(getStringValue(row, "city"))
                    .pageLocation(getStringValue(row, "page_location"))
                    .pageTitle(getStringValue(row, "page_title"))
                    .pageReferrer(getStringValue(row, "page_referrer"))
                    .campaignSource(getStringValue(row, "campaign_source"))
                    .campaignMedium(getStringValue(row, "campaign_medium"))
                    .campaignName(getStringValue(row, "campaign_name"))
                    .eventValue(getDoubleValue(row, "event_value"))
                    .build();
        } catch (Exception e) {
            log.error("转换事件数据失败", e);
            return null;
        }
    }

    private String getStringValue(FieldValueList row, String fieldName) {
        try {
            FieldValue value = row.get(fieldName);
            return value.isNull() ? null : value.getStringValue();
        } catch (Exception e) {
            return null;
        }
    }

    private Long getLongValue(FieldValueList row, String fieldName) {
        try {
            FieldValue value = row.get(fieldName);
            return value.isNull() ? null : value.getLongValue();
        } catch (Exception e) {
            return null;
        }
    }

    private Double getDoubleValue(FieldValueList row, String fieldName) {
        try {
            FieldValue value = row.get(fieldName);
            return value.isNull() ? null : value.getDoubleValue();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 批量插入事件数据
     * 使用 MyBatis-Plus 的 saveBatch 方法
     */
    private int insertBatch(List<Ga4EventDto> batch, Ga4Property property) {
        try {
            // 将 Ga4EventDto 转换为 Ga4Event 实体
            List<Ga4Event> events = batch.stream()
                    .map(dto -> convertToEntity(dto, property))
                    .collect(Collectors.toList());

            // 使用 MyBatis-Plus 的批量插入方法
            // 注意：需要在配置类或启动类添加 @MapperScan 注解
            events.forEach(eventMapper::insert);

            return events.size();
        } catch (Exception e) {
            log.error("批量插入事件数据失败", e);
            return 0;
        }
    }

    /**
     * 将 Ga4EventDto 转换为 Ga4Event 实体
     */
    private Ga4Event convertToEntity(Ga4EventDto dto, Ga4Property property) {
        Ga4Event event = new Ga4Event();
        event.setGa4PropertyId(property.getId());
        event.setEventName(dto.getEventName());
        event.setEventTimestamp(dto.getEventTimestamp());
        event.setEventDate(dto.getEventDate());
        event.setEventTime(dto.getEventTime());
        event.setSessionId(dto.getSessionId());
        event.setUserId(dto.getUserId());
        event.setClientId(dto.getClientId());
        event.setDeviceCategory(dto.getDeviceCategory());
        event.setBrowser(dto.getBrowser());
        event.setOperatingSystem(dto.getOperatingSystem());
        event.setCountry(dto.getCountry());
        event.setCity(dto.getCity());
        event.setPageLocation(dto.getPageLocation());
        event.setPageTitle(dto.getPageTitle());
        event.setPageReferrer(dto.getPageReferrer());
        event.setCampaignSource(dto.getCampaignSource());
        event.setCampaignMedium(dto.getCampaignMedium());
        event.setCampaignName(dto.getCampaignName());
        event.setEventValue(dto.getEventValue());
        event.setProcessed(false);

        // eventParams 和 userProperties 需要从 BigQuery 获取并转换为 JSON
        // 这部分需要根据实际的数据结构来处理

        return event;
    }
}