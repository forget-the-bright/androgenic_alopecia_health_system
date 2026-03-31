package com.androgenic.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
@TableName("User")
public class User {

    /**
     * 用户 ID
     */
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

    /**
     * 手机号（唯一登录凭证）
     */
    private String phone;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 性别 (0:未知，1:男，2:女)
     */
    private Integer gender;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 注册时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime registerTime;

    /**
     * 初始脱发等级
     */
    private String initialGrade;

    /**
     * 家族遗传史 (0:无，1:有)
     */
    private Integer familyHistory;

    /**
     * 账号状态 (0:禁用，1:正常)
     */
    private Integer status;
}
