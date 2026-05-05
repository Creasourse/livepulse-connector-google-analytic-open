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

/**
 * GA4 事件订阅配置
 * 配置需要同步的事件类型
 *
 * @author LivePulse
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("ga4_event_subscription")
@Schema(name = "Ga4EventSubscription", description = "GA4 事件订阅配置表")
public class Ga4EventSubscription extends Model<Ga4EventSubscription> {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键 ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "关联的 GA4 属性 ID")
    @TableField("ga4_property_id")
    private Long ga4PropertyId;

    @Schema(description = "事件名称（支持通配符 * 表示所有事件）")
    @TableField("event_name")
    private String eventName;

    @Schema(description = "事件类型: standard(标准事件)/custom(自定义事件)")
    @TableField("event_type")
    private String eventType;

    @Schema(description = "是否启用")
    @TableField("enabled")
    private Boolean enabled;

    @Schema(description = "过滤条件（JSON 格式）")
    @TableField("filter_condition")
    private String filterCondition;

    @Schema(description = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;

    @Schema(description = "创建人")
    @TableField("create_by")
    private String createBy;

    @Schema(description = "更新人")
    @TableField("update_by")
    private String updateBy;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}