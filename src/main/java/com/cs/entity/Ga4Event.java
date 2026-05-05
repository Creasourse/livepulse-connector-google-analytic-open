package com.cs.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * GA4 事件实体
 * 对应 BigQuery 中的事件数据
 *
 * @author LivePulse
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("ga4_event")
@Schema(name = "Ga4Event", description = "GA4 事件数据表")
public class Ga4Event extends Model<Ga4Event> {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键 ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "关联的 GA4 属性 ID")
    @TableField("ga4_property_id")
    private Long ga4PropertyId;

    @Schema(description = "事件名称 (如: page_view, session_start, purchase)")
    @TableField("event_name")
    private String eventName;

    @Schema(description = "事件时间戳（微秒）")
    @TableField("event_timestamp")
    private Long eventTimestamp;

    @Schema(description = "事件日期 (YYYYMMDD 格式)")
    @TableField("event_date")
    private String eventDate;

    @Schema(description = "事件时间 (HHMMSS 格式)")
    @TableField("event_time")
    private String eventTime;

    @Schema(description = "会话 ID")
    @TableField("session_id")
    private String sessionId;

    @Schema(description = "用户 ID")
    @TableField("user_id")
    private String userId;

    @Schema(description = "客户端 ID")
    @TableField("client_id")
    private String clientId;

    @Schema(description = "事件参数 (JSONB 格式)")
    @TableField("event_params")
    private Map<String, Object> eventParams;

    @Schema(description = "用户属性 (JSONB 格式)")
    @TableField("user_properties")
    private Map<String, Object> userProperties;

    @Schema(description = "设备类别")
    @TableField("device_category")
    private String deviceCategory;

    @Schema(description = "浏览器")
    @TableField("browser")
    private String browser;

    @Schema(description = "操作系统")
    @TableField("operating_system")
    private String operatingSystem;

    @Schema(description = "国家")
    @TableField("country")
    private String country;

    @Schema(description = "城市")
    @TableField("city")
    private String city;

    @Schema(description = "页面位置 (URL)")
    @TableField("page_location")
    private String pageLocation;

    @Schema(description = "页面标题")
    @TableField("page_title")
    private String pageTitle;

    @Schema(description = "页面引用来源")
    @TableField("page_referrer")
    private String pageReferrer;

    @Schema(description = "营销活动来源")
    @TableField("campaign_source")
    private String campaignSource;

    @Schema(description = "营销活动媒介")
    @TableField("campaign_medium")
    private String campaignMedium;

    @Schema(description = "营销活动名称")
    @TableField("campaign_name")
    private String campaignName;

    @Schema(description = "事件值")
    @TableField("event_value")
    private Double eventValue;

    @Schema(description = "是否已处理")
    @TableField("processed")
    private Boolean processed;

    @Schema(description = "处理时间")
    @TableField("processed_time")
    private LocalDateTime processedTime;

    @Schema(description = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}