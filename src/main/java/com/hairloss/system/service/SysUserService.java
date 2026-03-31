package com.hairloss.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hairloss.system.entity.SysUser;

/**
 * 系统用户服务接口
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 登录结果
     */
    String login(String username, String password);

    /**
     * 用户注册
     * @param user 用户信息
     * @return 注册结果
     */
    boolean register(SysUser user);

    /**
     * 退出登录
     */
    void logout();

    /**
     * 修改密码
     * @param userId 用户 ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 修改结果
     */
    boolean changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 获取当前登录用户信息
     * @return 用户信息
     */
    SysUser getCurrentUser();

    /**
     * 分页查询用户列表
     * @param page 分页参数
     * @param username 用户名（可选）
     * @param role 角色（可选）
     * @return 分页结果
     */
    Page<SysUser> getUserPage(Page<SysUser> page, String username, Integer role);

    /**
     * 更新用户状态
     * @param userId 用户 ID
     * @param status 状态
     * @return 更新结果
     */
    boolean updateUserStatus(Long userId, Integer status);

    /**
     * 重置用户密码
     * @param userId 用户 ID
     * @param newPassword 新密码
     * @return 重置结果
     */
    boolean resetPassword(Long userId, String newPassword);
}
