package com.hairloss.system.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hairloss.system.entity.SysUser;
import com.hairloss.system.mapper.SysUserMapper;
import com.hairloss.system.service.SysOperationLogService;
import com.hairloss.system.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * 系统用户服务实现类
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Autowired
    private SysOperationLogService sysOperationLogService;

    /**
     * 获取请求 IP 地址
     */
    private String getIpAddress() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String ip = request.getHeader("X-Forwarded-For");
                if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getHeader("X-Real-IP");
                }
                if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getRemoteAddr();
                }
                return ip;
            }
        } catch (Exception e) {
            // 忽略异常
        }
        return "0.0.0.0";
    }

    @Override
    public String login(String username, String password) {
        SysUser user = this.getOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username));

        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        if (!BCrypt.checkpw(password, user.getPassword())) {
            sysOperationLogService.logOperation(user.getId(), "LOGIN", "用户登录",
                    "/api/login", null, getIpAddress(), 0, "密码错误");
            throw new RuntimeException("用户名或密码错误");
        }

        if (user.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }

        // 登录
        StpUtil.login(user.getId());

        // 更新最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        this.updateById(user);

        // 记录日志
        sysOperationLogService.logOperation(user.getId(), "LOGIN", "用户登录",
                "/api/login", null, getIpAddress(), 1, null);

        return StpUtil.getTokenValue();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean register(SysUser user) {
        // 检查用户名是否存在
        Long count = this.count(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, user.getUsername()));
        if (count > 0) {
            throw new RuntimeException("用户名已存在");
        }

        // 加密密码
        user.setPassword(BCrypt.hashpw(user.getPassword()));
        user.setRole(0); // 默认普通用户
        user.setStatus(1); // 默认启用
        user.setLanguage("zh-CN");

        boolean result = this.save(user);

        if (result) {
            sysOperationLogService.logOperation(user.getId(), "REGISTER", "用户注册",
                    "/api/register", null, getIpAddress(), 1, null);
        }

        return result;
    }

    @Override
    public void logout() {
        Long userId = StpUtil.getLoginIdAsLong();
        StpUtil.logout();
        sysOperationLogService.logOperation(userId, "LOGOUT", "用户退出",
                "/api/logout", null, getIpAddress(), 1, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        SysUser user = this.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (!BCrypt.checkpw(oldPassword, user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }

        user.setPassword(BCrypt.hashpw(newPassword));
        boolean result = this.updateById(user);

        if (result) {
            sysOperationLogService.logOperation(userId, "CHANGE_PASSWORD", "修改密码",
                    "/api/change-password", null, getIpAddress(), 1, null);
        }

        return result;
    }

    @Override
    public SysUser getCurrentUser() {
        Long userId = StpUtil.getLoginIdAsLong();
        return this.getById(userId);
    }

    @Override
    public Page<SysUser> getUserPage(Page<SysUser> page, String username, Integer role) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(username)) {
            wrapper.like(SysUser::getUsername, username);
        }
        if (role != null) {
            wrapper.eq(SysUser::getRole, role);
        }
        wrapper.orderByDesc(SysUser::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUserStatus(Long userId, Integer status) {
        SysUser user = this.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        user.setStatus(status);
        boolean result = this.updateById(user);

        if (result) {
            Long currentUserId = StpUtil.getLoginIdAsLong();
            sysOperationLogService.logOperation(currentUserId, "UPDATE_USER_STATUS",
                    "更新用户状态：" + (status == 1 ? "启用" : "禁用"),
                    "/api/admin/user/status", "userId=" + userId + ",status=" + status,
                    getIpAddress(), 1, null);
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean resetPassword(Long userId, String newPassword) {
        SysUser user = this.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        user.setPassword(BCrypt.hashpw(newPassword));
        boolean result = this.updateById(user);

        if (result) {
            Long currentUserId = StpUtil.getLoginIdAsLong();
            sysOperationLogService.logOperation(currentUserId, "RESET_PASSWORD",
                    "重置用户密码", "/api/admin/user/reset-password",
                    "userId=" + userId, getIpAddress(), 1, null);
        }

        return result;
    }
}
