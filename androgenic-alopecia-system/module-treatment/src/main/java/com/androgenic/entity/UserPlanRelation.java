package com.androgenic.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;

/**
 * 患者方案关联实体类
 */
@Data
@TableName("User_Plan_Relation")
public class UserPlanRelation {

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
     * 疗程开始日期
     */
    private LocalDate startDate;

    /**
     * 预计/实际结束日期
     */
    private LocalDate endDate;

    /**
     * 状态 (active:进行中，completed:完成，paused:暂停)
     */
    private String currentStatus;
}
