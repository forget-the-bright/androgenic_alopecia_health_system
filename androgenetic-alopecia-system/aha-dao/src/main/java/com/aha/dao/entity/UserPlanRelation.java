package com.aha.dao.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 用户方案关联表实体类
 */
@Data
@TableName("User_Plan_Relation")
public class UserPlanRelation implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关系 ID
     */
    @TableId(value = "relation_id", type = IdType.AUTO)
    private Long relationId;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 方案 ID
     */
    private Long planId;

    /**
     * 开始日期
     */
    private LocalDate startDate;

    /**
     * 结束日期
     */
    private LocalDate endDate;

    /**
     * 当前状态 (active, completed, stopped)
     */
    private String currentStatus;
}
