package com.androgenic.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用药打卡记录实体类
 */
@Data
@TableName("Medication_Record")
public class MedicationRecord {

    /**
     * 记录 ID
     */
    @TableId(value = "record_id", type = IdType.AUTO)
    private Long recordId;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 方案关系 ID
     */
    private Long relationId;

    /**
     * 打卡日期
     */
    private LocalDate recordDate;

    /**
     * 是否用药 (0:否，1:是)
     */
    private Integer isTaken;

    /**
     * 实际用药剂量
     */
    private String actualDose;

    /**
     * 反馈（如副作用描述）
     */
    private String feedback;

    /**
     * 打卡时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
