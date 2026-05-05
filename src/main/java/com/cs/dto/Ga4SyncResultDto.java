package com.cs.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * GA4 同步结果 DTO
 *
 * @author LivePulse
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ga4SyncResultDto {

    /**
     * GA4 属性 ID
     */
    private Long ga4PropertyId;

    /**
     * 同步状态
     */
    private String syncStatus;

    /**
     * 开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    /**
     * 起始日期
     */
    private String startDate;

    /**
     * 结束日期
     */
    private String endDate;

    /**
     * 成功记录数
     */
    private Long successCount;

    /**
     * 失败记录数
     */
    private Long failureCount;

    /**
     * 总记录数
     */
    private Long totalCount;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 执行耗时（毫秒）
     */
    private Long duration;

    /**
     * 消息
     */
    private String message;
}