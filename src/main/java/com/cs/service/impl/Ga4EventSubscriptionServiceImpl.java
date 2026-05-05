package com.cs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cs.entity.Ga4EventSubscription;
import com.cs.mapper.Ga4EventSubscriptionMapper;
import com.cs.service.Ga4EventSubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * GA4 事件订阅服务实现
 *
 * @author LivePulse
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class Ga4EventSubscriptionServiceImpl implements Ga4EventSubscriptionService {

    private final Ga4EventSubscriptionMapper subscriptionMapper;

    @Override
    public Ga4EventSubscription findById(Long id) {
        return subscriptionMapper.selectById(id);
    }

    @Override
    public List<Ga4EventSubscription> findByPropertyId(Long ga4PropertyId) {
        LambdaQueryWrapper<Ga4EventSubscription> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Ga4EventSubscription::getGa4PropertyId, ga4PropertyId);
        return subscriptionMapper.selectList(wrapper);
    }

    @Override
    public List<Ga4EventSubscription> findEnabledByPropertyId(Long ga4PropertyId) {
        LambdaQueryWrapper<Ga4EventSubscription> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Ga4EventSubscription::getGa4PropertyId, ga4PropertyId)
                .eq(Ga4EventSubscription::getEnabled, true);
        return subscriptionMapper.selectList(wrapper);
    }

    @Override
    public Ga4EventSubscription create(Ga4EventSubscription subscription) {
        subscription.setCreateTime(LocalDateTime.now());
        subscription.setUpdateTime(LocalDateTime.now());
        subscriptionMapper.insert(subscription);
        return subscription;
    }

    @Override
    public Ga4EventSubscription update(Ga4EventSubscription subscription) {
        subscription.setUpdateTime(LocalDateTime.now());
        subscriptionMapper.updateById(subscription);
        return subscription;
    }

    @Override
    public void delete(Long id) {
        subscriptionMapper.deleteById(id);
    }

    @Override
    public boolean isEventSubscribed(Long ga4PropertyId, String eventName) {
        List<Ga4EventSubscription> subscriptions = findEnabledByPropertyId(ga4PropertyId);

        // 检查是否有通配符订阅（订阅所有事件）
        for (Ga4EventSubscription subscription : subscriptions) {
            String subscribedEvent = subscription.getEventName();
            if ("*".equals(subscribedEvent) || "*".equals(subscribedEvent.trim())) {
                return true;
            }
        }

        // 检查精确匹配
        for (Ga4EventSubscription subscription : subscriptions) {
            String subscribedEvent = subscription.getEventName();
            if (eventName != null && eventName.equals(subscribedEvent)) {
                return true;
            }
        }

        return false;
    }
}