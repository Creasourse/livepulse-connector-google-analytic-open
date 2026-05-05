package com.cs.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * GA4 事件数据传输对象
 * 用于接收和发送 GA4 事件数据
 *
 * @author LivePulse
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ga4EventDto {

    /**
     * 事件名称
     */
    @JsonProperty("event_name")
    private String eventName;

    /**
     * 事件时间戳（微秒）
     */
    @JsonProperty("event_timestamp")
    private Long eventTimestamp;

    /**
     * 事件日期（YYYYMMDD 格式）
     */
    @JsonProperty("event_date")
    private String eventDate;

    /**
     * 事件时间（HHMMSS 格式）
     */
    @JsonProperty("event_time")
    private String eventTime;

    /**
     * 会话 ID
     */
    @JsonProperty("session_id")
    private String sessionId;

    /**
     * 用户 ID
     */
    @JsonProperty("user_id")
    private String userId;

    /**
     * 客户端 ID
     */
    @JsonProperty("client_id")
    private String clientId;

    /**
     * 事件参数
     */
    @JsonProperty("event_params")
    private Map<String, Object> eventParams;

    /**
     * 用户属性
     */
    @JsonProperty("user_properties")
    private Map<String, Object> userProperties;

    /**
     * 设备类别
     */
    @JsonProperty("device_category")
    private String deviceCategory;

    /**
     * 浏览器
     */
    @JsonProperty("browser")
    private String browser;

    /**
     * 操作系统
     */
    @JsonProperty("operating_system")
    private String operatingSystem;

    /**
     * 国家
     */
    @JsonProperty("country")
    private String country;

    /**
     * 城市
     */
    @JsonProperty("city")
    private String city;

    /**
     * 页面位置
     */
    @JsonProperty("page_location")
    private String pageLocation;

    /**
     * 页面标题
     */
    @JsonProperty("page_title")
    private String pageTitle;

    /**
     * 页面引用来源
     */
    @JsonProperty("page_referrer")
    private String pageReferrer;

    /**
     * 营销活动来源
     */
    @JsonProperty("campaign_source")
    private String campaignSource;

    /**
     * 营销活动媒介
     */
    @JsonProperty("campaign_medium")
    private String campaignMedium;

    /**
     * 营销活动名称
     */
    @JsonProperty("campaign_name")
    private String campaignName;

    /**
     * 事件值
     */
    @JsonProperty("event_value")
    private Double eventValue;
}