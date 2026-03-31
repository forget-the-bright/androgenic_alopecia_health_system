package com.androgenic.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 脱发记录实体类
 */
@Data
@TableName("Hair_Loss_Record")
public class HairLossRecord {

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
     * 记录日期
     */
    private LocalDate recordDate;

    /**
     * 当前脱发等级
     */
    private String hairGrade;

    /**
     * 备注/医生评语
     */
    private String notes;

    /**
     * 关联图片数量
     */
    private Integer imageCount;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
