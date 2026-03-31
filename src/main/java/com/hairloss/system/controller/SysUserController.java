package com.hairloss.system.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hairloss.system.common.Result;
import com.hairloss.system.entity.SysUser;
import com.hairloss.system.service.SysOperationLogService;
import com.hairloss.system.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 系统用户控制器
 */
@RestController
@RequestMapping("/api")
@Api(tags = "用户管理")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysOperationLogService sysOperationLogService;

    @PostMapping("/login")
    @ApiOperation("用户登录")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");

        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            return Result.error("用户名和密码不能为空");
        }

        String token = sysUserService.login(username, password);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userInfo", sysUserService.getCurrentUser());

        return Result.success(result);
    }

    @PostMapping("/logout")
    @ApiOperation("用户退出")
    public Result<Void> logout() {
        sysUserService.logout();
        return Result.success();
    }

    @PostMapping("/register")
    @ApiOperation("用户注册")
    public Result<Void> register(@RequestBody SysUser user) {
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            return Result.error("用户名不能为空");
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            return Result.error("密码不能为空");
        }

        sysUserService.register(user);
        return Result.success();
    }

    @GetMapping("/user/info")
    @ApiOperation("获取当前用户信息")
    public Result<SysUser> getUserInfo() {
        return Result.success(sysUserService.getCurrentUser());
    }

    @PostMapping("/user/change-password")
    @ApiOperation("修改密码")
    public Result<Void> changePassword(@RequestBody Map<String, String> params) {
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        String confirmPassword = params.get("confirmPassword");

        if (oldPassword == null || oldPassword.isEmpty() || newPassword == null || newPassword.isEmpty()) {
            return Result.error("密码不能为空");
        }

        if (!newPassword.equals(confirmPassword)) {
            return Result.error("两次输入的密码不一致");
        }

        Long userId = StpUtil.getLoginIdAsLong();
        sysUserService.changePassword(userId, oldPassword, newPassword);
        return Result.success();
    }

    @PostMapping("/user/update-language")
    @ApiOperation("更新语言偏好")
    public Result<Void> updateLanguage(@RequestBody Map<String, String> params) {
        String language = params.get("language");
        Long userId = StpUtil.getLoginIdAsLong();

        SysUser user = sysUserService.getById(userId);
        user.setLanguage(language);
        sysUserService.updateById(user);

        return Result.success();
    }

    // ==================== 管理员功能 ====================

    @GetMapping("/admin/user/list")
    @SaCheckRole("admin")
    @ApiOperation("用户列表（管理员）")
    public Result<Page<SysUser>> getUserList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Integer role) {
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        Page<SysUser> result = sysUserService.getUserPage(page, username, role);
        return Result.success(result);
    }

    @PostMapping("/admin/user/status")
    @SaCheckRole("admin")
    @ApiOperation("更新用户状态（管理员）")
    public Result<Void> updateUserStatus(@RequestBody Map<String, Object> params) {
        Long userId = Long.parseLong(params.get("userId").toString());
        Integer status = (Integer) params.get("status");

        sysUserService.updateUserStatus(userId, status);
        return Result.success();
    }

    @PostMapping("/admin/user/reset-password")
    @SaCheckRole("admin")
    @ApiOperation("重置用户密码（管理员）")
    public Result<Void> resetPassword(@RequestBody Map<String, Object> params) {
        Long userId = Long.parseLong(params.get("userId").toString());
        String newPassword = params.get("newPassword").toString();

        sysUserService.resetPassword(userId, newPassword);
        return Result.success();
    }

/*    @GetMapping("/admin/user/{userId}")
    @SaCheckRole("admin")
    @ApiOperation("获取用户详情（管理员）")
    public Result<SysUser> getUserDetail(@PathVariable Long userId) {
        SysUser user = sysUserService.getById(userId);
        return Result.success(user);
    }*/
}
