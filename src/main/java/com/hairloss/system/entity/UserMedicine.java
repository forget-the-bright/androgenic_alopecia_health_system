package com.hairloss.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用药方案表
 */
@Data
@TableName("user_medicine")
public class UserMedicine implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用药方案唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关联用户 ID
     */
    private Long userId;

    /**
     * 药物名称
     */
    private String medicineName;

    /**
     * 用药剂量
     */
    private String dosage;

    /**
     * 服用时间
     */
    private String takeTime;

    /**
     * 用药周期（天）
     */
    private Integer cycle;

    /**
     * 方案状态（0：停用，1：启用）
     */
    private Integer status;

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

    /**
     * 备注
     */
    private String remark;
}
