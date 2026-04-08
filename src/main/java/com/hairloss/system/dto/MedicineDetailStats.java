
package com.hairloss.system.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 用药详情统计 DTO
 *
 * @author wanghao(helloworlwh@163.com)
 * @since 2026/4/8 10:36
 */
@Data
@ApiModel(value = "MedicineDetailStats", description = "用药详情统计信息")
public class MedicineDetailStats {

    @ApiModelProperty(value = "用药方案 ID")
    private Long medicineId;

    @ApiModelProperty(value = "药物名称")
    private String medicineName;

    @ApiModelProperty(value = "用药剂量")
    private String dosage;

    @ApiModelProperty(value = "服用时间")
    private String takeTime;

    @ApiModelProperty(value = "用药周期（天）")
    private Integer cycle;

    @ApiModelProperty(value = "方案状态（0：停用，1：启用）")
    private Integer status;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "开始用药日期")
    private LocalDate firstClockDate;

    @ApiModelProperty(value = "最后一次打卡日期")
    private LocalDate lastClockDate;

    @ApiModelProperty(value = "从开始用药到现在的总天数")
    private Integer totalDays;

    @ApiModelProperty(value = "实际打卡天数（包括已服和补卡）")
    private Integer actualClockDays;

    @ApiModelProperty(value = "漏服天数")
    private Integer missedDays;

    @ApiModelProperty(value = "是否有中断")
    private Boolean hasBreak;

    @ApiModelProperty(value = "中断次数")
    private Integer breakCount;

    @ApiModelProperty(value = "总中断天数")
    private Integer totalBreakDays;

    @ApiModelProperty(value = "中断详情列表")
    private List<BreakPeriod> breakPeriods;

    @ApiModelProperty(value = "完成率（%）")
    private Double completionRate;

    @ApiModelProperty(value = "当前连续打卡天数")
    private Integer currentConsecutiveDays;

    @Data
    @ApiModel(value = "BreakPeriod", description = "中断时段详情")
    public static class BreakPeriod {
        
        @ApiModelProperty(value = "中断开始日期")
        private LocalDate startDate;

        @ApiModelProperty(value = "中断结束日期")
        private LocalDate endDate;

        @ApiModelProperty(value = "中断天数")
        private Integer days;
    }
}
