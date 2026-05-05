package com.cs.service;

import com.cs.entity.Ga4SyncLog;

import java.util.List;

/**
 * GA4 同步日志服务接口
 *
 * @author LivePulse
 */
public interface Ga4SyncLogService {

    /**
     * 根据 ID 查询
     */
    Ga4SyncLog findById(Long id);

    /**
     * 根据 GA4 属性 ID 查询最近的同步日志
     */
    List<Ga4SyncLog> findByPropertyId(Long ga4PropertyId, int limit);

    /**
     * 创建
     */
    Ga4SyncLog create(Ga4SyncLog ga4SyncLog);

    /**
     * 更新
     */
    Ga4SyncLog update(Ga4SyncLog ga4SyncLog);

    /**
     * 记录同步开始
     */
    Ga4SyncLog logStart(Long ga4PropertyId, String syncType, String startDate, String endDate);

    /**
     * 记录同步成功
     */
    void logSuccess(Long logId, Long successCount, Long failureCount);

    /**
     * 记录同步失败
     */
    void logFailure(Long logId, String errorMessage);
}