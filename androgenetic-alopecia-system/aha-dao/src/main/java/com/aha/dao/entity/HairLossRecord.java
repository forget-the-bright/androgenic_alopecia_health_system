package com.aha.dao.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 脱发记录表实体类
 */
@Data
@TableName("Hair_Loss_Record")
public class HairLossRecord implements Serializable {

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
     * 记录日期
     */
    private LocalDate recordDate;

    /**
     * 脱发等级
     */
    private String hairGrade;

    /**
     * 备注
     */
    private String notes;

    /**
     * 图片数量
     */
    private Integer imageCount;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
