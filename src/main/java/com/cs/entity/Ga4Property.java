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
 * GA4 属性实体
 * 对应 Google Analytics 4 的属性（Property）
 *
 * @author LivePulse
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("ga4_property")
@Schema(name = "Ga4Property", description = "GA4 属性配置表")
public class Ga4Property extends Model<Ga4Property> {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键 ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "GA4 Property ID (如: 123456789)")
    @TableField("property_id")
    private String propertyId;

    @Schema(description = "属性名称")
    @TableField("property_name")
    private String propertyName;

    @Schema(description = "BigQuery 项目 ID")
    @TableField("bigquery_project_id")
    private String bigqueryProjectId;

    @Schema(description = "BigQuery 数据集 ID")
    @TableField("bigquery_dataset_id")
    private String bigqueryDatasetId;

    @Schema(description = "数据流 ID")
    @TableField("data_stream_id")
    private String dataStreamId;

    @Schema(description = "是否启用")
    @TableField("enabled")
    private Boolean enabled;

    @Schema(description = "同步状态: pending/syncing/success/failed")
    @TableField("sync_status")
    private String syncStatus;

    @Schema(description = "最后同步时间")
    @TableField("last_sync_time")
    private LocalDateTime lastSyncTime;

    @Schema(description = "最后错误信息")
    @TableField("last_error_message")
    private String lastErrorMessage;

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