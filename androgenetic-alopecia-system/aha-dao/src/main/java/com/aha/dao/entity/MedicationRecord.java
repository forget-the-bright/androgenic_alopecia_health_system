package com.aha.dao.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用药记录表实体类
 */
@Data
@TableName("Medication_Record")
public class MedicationRecord implements Serializable {

    private static final long serialVersionUID = 1L;

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
     * 记录日期
     */
    private LocalDate recordDate;

    /**
     * 是否已用药 (0:否，1:是)
     */
    private Integer isTaken;

    /**
     * 实际剂量
     */
    private String actualDose;

    /**
     * 反馈
     */
    private String feedback;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
