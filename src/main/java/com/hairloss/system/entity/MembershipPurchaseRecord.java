package com.hairloss.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 会员购买记录表
 */
@Data
@TableName("membership_purchase_record")
public class MembershipPurchaseRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 购买记录唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 操作类型（1：新购，2：续费，3：升级）
     */
    private Integer actionType;

    /**
     * 原会员等级
     */
    private Integer fromLevel;

    /**
     * 目标会员等级
     */
    private Integer toLevel;

    /**
     * 原价
     */
    private BigDecimal originalPrice;

    /**
     * 优惠金额
     */
    private BigDecimal discountAmount;

    /**
     * 实付金额
     */
    private BigDecimal actualPayment;

    /**
     * 会员天数
     */
    private Integer membershipDays;

    /**
     * 原到期时间
     */
    private LocalDateTime oldEndTime;

    /**
     * 新到期时间
     */
    private LocalDateTime newEndTime;

    /**
     * 支付状态（0：未支付，1：已支付）
     */
    private Integer paymentStatus;

    /**
     * 支付时间
     */
    private LocalDateTime paymentTime;

    /**
     * 备注
     */
    private String remark;

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
