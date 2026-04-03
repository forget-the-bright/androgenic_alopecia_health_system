package com.hairloss.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户会员表
 */
@Data
@TableName("user_membership")
public class UserMembership implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 会员记录唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关联用户 ID
     */
    private Long userId;

    /**
     * 会员等级（0：免费用户，1：月度会员，2：年度会员）
     */
    private Integer membershipLevel;

    /**
     * 会员开始时间
     */
    private LocalDateTime membershipStartTime;

    /**
     * 会员结束时间
     */
    private LocalDateTime membershipEndTime;

    /**
     * 每月 AI 分析次数配额
     */
    private Integer monthlyQuota;

    /**
     * 当月已使用次数
     */
    private Integer usedCountCurrentMonth;

    /**
     * 当前月份（用于重置判断）
     */
    private Integer currentMonth;

    /**
     * 状态（0：已过期，1：有效期内）
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
     * 会员等级名称
     */
    @TableField(exist = false)
    private String levelName;

    /**
     * 剩余次数
     */
    @TableField(exist = false)
    private Integer remainingCount;

    /**
     * 是否过期
     */
    @TableField(exist = false)
    private Boolean expired;
}
