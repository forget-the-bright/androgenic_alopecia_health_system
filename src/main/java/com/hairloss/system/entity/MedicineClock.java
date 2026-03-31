package com.hairloss.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用药打卡表
 */
@Data
@TableName("medicine_clock")
public class MedicineClock implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 打卡记录唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关联用户 ID
     */
    private Long userId;

    /**
     * 关联用药方案 ID
     */
    private Long medicineId;

    /**
     * 打卡日期
     */
    private LocalDate clockDate;

    /**
     * 打卡状态（0：漏服，1：已服，2：补卡）
     */
    private Integer clockStatus;

    /**
     * 打卡时间
     */
    private LocalDateTime clockTime;

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
