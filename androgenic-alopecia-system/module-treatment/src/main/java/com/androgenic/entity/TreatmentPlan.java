package com.androgenic.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用药方案实体类
 */
@Data
@TableName("Treatment_Plan")
public class TreatmentPlan {

    /**
     * 方案 ID
     */
    @TableId(value = "plan_id", type = IdType.AUTO)
    private Long planId;

    /**
     * 方案名称
     */
    private String planName;

    /**
     * 适用脱发等级
     */
    private String applicableGrade;

    /**
     * 药品列表 (JSON 格式)
     */
    private String drugList;

    /**
     * 疗程总天数
     */
    private Integer durationDays;

    /**
     * 方案描述
     */
    private String description;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
