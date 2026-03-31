package com.aha.service.vo;

import lombok.Data;

/**
 * 用户信息 VO
 */
@Data
public class UserVO {

    /**
     * 用户 ID
     */
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
}
