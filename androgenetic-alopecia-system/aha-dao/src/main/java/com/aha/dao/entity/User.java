package com.aha.dao.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户表实体类
 */
@Data
@TableName("User")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户 ID
     */
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

    /**
     * 手机号
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
     * 家族史 (0:无，1:有)
     */
    private Integer familyHistory;

    /**
     * 状态 (0:禁用，1:正常)
     */
    private Integer status;

    /**
     * 密码（非数据库字段，用于登录注册）
     */
    @TableField(exist = false)
    private String password;
}
