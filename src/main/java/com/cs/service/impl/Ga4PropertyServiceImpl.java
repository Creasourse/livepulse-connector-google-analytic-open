package com.cs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cs.dto.Ga4SyncResultDto;
import com.cs.entity.Ga4Property;
import com.cs.mapper.Ga4PropertyMapper;
import com.cs.service.Ga4BigQueryService;
import com.cs.service.Ga4PropertyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * GA4 属性服务实现
 *
 * @author LivePulse
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class Ga4PropertyServiceImpl implements Ga4PropertyService {

    private final Ga4PropertyMapper propertyMapper;
    private final Ga4BigQueryService bigQueryService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public Ga4Property findById(Long id) {
        return propertyMapper.selectById(id);
    }

    @Override
    public Ga4Property findByPropertyId(String propertyId) {
        LambdaQueryWrapper<Ga4Property> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Ga4Property::getPropertyId, propertyId);
        return propertyMapper.selectOne(wrapper);
    }

    @Override
    public List<Ga4Property> findEnabled() {
        LambdaQueryWrapper<Ga4Property> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Ga4Property::getEnabled, true);
        return propertyMapper.selectList(wrapper);
    }

    @Override
    public Ga4Property create(Ga4Property ga4Property) {
        ga4Property.setCreateTime(java.time.LocalDateTime.now());
        ga4Property.setUpdateTime(java.time.LocalDateTime.now());
        propertyMapper.insert(ga4Property);
        return ga4Property;
    }

    @Override
    public Ga4Property update(Ga4Property ga4Property) {
        ga4Property.setUpdateTime(java.time.LocalDateTime.now());
        propertyMapper.updateById(ga4Property);
        return ga4Property;
    }

    @Override
    public void delete(Long id) {
        propertyMapper.deleteById(id);
    }

    @Override
    public boolean testConnection(Long id) {
        return bigQueryService.testConnection(id);
    }

    @Override
    public Ga4SyncResultDto manualSync(Long id, String startDate, String endDate) {
        Ga4Property property = findById(id);
        if (property == null) {
            return Ga4SyncResultDto.builder()
                    .ga4PropertyId(id)
                    .syncStatus("failed")
                    .message("GA4 属性不存在")
                    .build();
        }

        try {
            LocalDate start = LocalDate.parse(startDate, DATE_FORMATTER);
            LocalDate end = LocalDate.parse(endDate, DATE_FORMATTER);
            return bigQueryService.syncEvents(id, start, end);
        } catch (Exception e) {
            log.error("手动同步失败", e);
            return Ga4SyncResultDto.builder()
                    .ga4PropertyId(id)
                    .syncStatus("failed")
                    .errorMessage(e.getMessage())
                    .build();
        }
    }
}