package com.aha.dao.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 治疗方案表实体类
 */
@Data
@TableName("Treatment_Plan")
public class TreatmentPlan implements Serializable {

    private static final long serialVersionUID = 1L;

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
     * 适用等级
     */
    private String applicableGrade;

    /**
     * 药品列表 JSON
     */
    private String drugList;

    /**
     * 疗程天数
     */
    private Integer durationDays;

    /**
     * 描述
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
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}
