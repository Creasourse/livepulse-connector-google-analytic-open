package com.cs.service;

import com.cs.entity.Ga4Property;
import com.cs.dto.Ga4SyncResultDto;

import java.util.List;

/**
 * GA4 属性服务接口
 *
 * @author LivePulse
 */
public interface Ga4PropertyService {

    /**
     * 根据 ID 查询
     */
    Ga4Property findById(Long id);

    /**
     * 根据 Property ID 查询
     */
    Ga4Property findByPropertyId(String propertyId);

    /**
     * 查询所有启用的属性
     */
    List<Ga4Property> findEnabled();

    /**
     * 创建
     */
    Ga4Property create(Ga4Property ga4Property);

    /**
     * 更新
     */
    Ga4Property update(Ga4Property ga4Property);

    /**
     * 删除
     */
    void delete(Long id);

    /**
     * 测试连接
     */
    boolean testConnection(Long id);

    /**
     * 手动触发同步
     */
    Ga4SyncResultDto manualSync(Long id, String startDate, String endDate);
}