package com.cs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cs.entity.Ga4SyncLog;
import com.cs.mapper.Ga4SyncLogMapper;
import com.cs.service.Ga4SyncLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * GA4 同步日志服务实现
 *
 * @author LivePulse
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class Ga4SyncLogServiceImpl implements Ga4SyncLogService {

    private final Ga4SyncLogMapper syncLogMapper;

    @Override
    public Ga4SyncLog findById(Long id) {
        return syncLogMapper.selectById(id);
    }

    @Override
    public List<Ga4SyncLog> findByPropertyId(Long ga4PropertyId, int limit) {
        LambdaQueryWrapper<Ga4SyncLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Ga4SyncLog::getGa4PropertyId, ga4PropertyId)
                .orderByDesc(Ga4SyncLog::getCreateTime)
                .last("LIMIT " + limit);
        return syncLogMapper.selectList(wrapper);
    }

    @Override
    public Ga4SyncLog create(Ga4SyncLog ga4SyncLog) {
        ga4SyncLog.setCreateTime(LocalDateTime.now());
        syncLogMapper.insert(ga4SyncLog);
        return ga4SyncLog;
    }

    @Override
    public Ga4SyncLog update(Ga4SyncLog ga4SyncLog) {
        syncLogMapper.updateById(ga4SyncLog);
        return ga4SyncLog;
    }

    @Override
    public Ga4SyncLog logStart(Long ga4PropertyId, String syncType, String startDate, String endDate) {
        Ga4SyncLog syncLog = new Ga4SyncLog();
        syncLog.setGa4PropertyId(ga4PropertyId);
        syncLog.setSyncType(syncType);
        syncLog.setStartDate(startDate);
        syncLog.setEndDate(endDate);
        syncLog.setSyncStatus("running");
        syncLog.setStartTime(LocalDateTime.now());
        return create(syncLog);
    }

    @Override
    public void logSuccess(Long logId, Long successCount, Long failureCount) {
        Ga4SyncLog syncLog = findById(logId);
        if (syncLog != null) {
            syncLog.setSyncStatus("success");
            syncLog.setSuccessCount(successCount);
            syncLog.setFailureCount(failureCount);
            syncLog.setTotalCount(successCount + failureCount);
            syncLog.setEndTime(LocalDateTime.now());
            if (syncLog.getStartTime() != null) {
                long duration = java.time.Duration.between(syncLog.getStartTime(), syncLog.getEndTime()).toMillis();
                syncLog.setDuration(duration);
            }
            update(syncLog);
        }
    }

    @Override
    public void logFailure(Long logId, String errorMessage) {
        Ga4SyncLog syncLog = findById(logId);
        if (syncLog != null) {
            syncLog.setSyncStatus("failed");
            syncLog.setErrorMessage(errorMessage);
            syncLog.setEndTime(LocalDateTime.now());
            if (syncLog.getStartTime() != null) {
                long duration = java.time.Duration.between(syncLog.getStartTime(), syncLog.getEndTime()).toMillis();
                syncLog.setDuration(duration);
            }
            update(syncLog);
        }
    }
}