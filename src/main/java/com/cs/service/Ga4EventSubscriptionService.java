package com.cs.service;

import com.cs.entity.Ga4EventSubscription;

import java.util.List;

/**
 * GA4 事件订阅服务接口
 *
 * @author LivePulse
 */
public interface Ga4EventSubscriptionService {

    /**
     * 根据 ID 查询
     */
    Ga4EventSubscription findById(Long id);

    /**
     * 根据 GA4 属性 ID 查询所有订阅
     */
    List<Ga4EventSubscription> findByPropertyId(Long ga4PropertyId);

    /**
     * 根据 GA4 属性 ID 查询启用的订阅
     */
    List<Ga4EventSubscription> findEnabledByPropertyId(Long ga4PropertyId);

    /**
     * 创建
     */
    Ga4EventSubscription create(Ga4EventSubscription subscription);

    /**
     * 更新
     */
    Ga4EventSubscription update(Ga4EventSubscription subscription);

    /**
     * 删除
     */
    void delete(Long id);

    /**
     * 检查事件是否匹配订阅
     */
    boolean isEventSubscribed(Long ga4PropertyId, String eventName);
}