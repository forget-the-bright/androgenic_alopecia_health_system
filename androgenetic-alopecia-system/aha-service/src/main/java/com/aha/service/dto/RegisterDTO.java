package com.aha.service.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 用户注册 DTO
 */
@Data
public class RegisterDTO {

    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在 6-20 位之间")
    private String password;

    /**
     * 昵称
     */
    @NotBlank(message = "昵称不能为空")
    @Size(max = 50, message = "昵称长度不能超过 50 个字符")
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
}
