package com.cs.service;

import com.cs.dto.Ga4EventDto;
import com.cs.dto.Ga4SyncResultDto;

import java.time.LocalDate;
import java.util.List;

/**
 * GA4 BigQuery 服务接口
 * 用于从 BigQuery 获取 GA4 事件数据
 *
 * @author LivePulse
 */
public interface Ga4BigQueryService {

    /**
     * 同步指定日期范围的事件数据
     *
     * @param ga4PropertyId GA4 属性 ID
     * @param startDate 起始日期
     * @param endDate 结束日期
     * @return 同步结果
     */
    Ga4SyncResultDto syncEvents(Long ga4PropertyId, LocalDate startDate, LocalDate endDate);

    /**
     * 从 BigQuery 查询事件数据
     *
     * @param ga4PropertyId GA4 属性 ID
     * @param startDate 起始日期
     * @param endDate 结束日期
     * @return 事件列表
     */
    List<Ga4EventDto> queryEvents(Long ga4PropertyId, LocalDate startDate, LocalDate endDate);

    /**
     * 根据事件名称查询
     *
     * @param ga4PropertyId GA4 属性 ID
     * @param eventName 事件名称
     * @param startDate 起始日期
     * @param endDate 结束日期
     * @return 事件列表
     */
    List<Ga4EventDto> queryEventsByName(Long ga4PropertyId, String eventName,
                                         LocalDate startDate, LocalDate endDate);

    /**
     * 测试 BigQuery 连接
     *
     * @param ga4PropertyId GA4 属性 ID
     * @return 是否连接成功
     */
    boolean testConnection(Long ga4PropertyId);
}