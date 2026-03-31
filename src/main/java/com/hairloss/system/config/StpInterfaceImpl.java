package com.hairloss.system.config;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpInterface;
import com.hairloss.system.entity.SysUser;
import com.hairloss.system.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Sa-Token 角色权限接口实现
 */
@Component
public class StpInterfaceImpl implements StpInterface {

    @Autowired
    private SysUserService sysUserService;

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // 本系统暂不实现权限列表
        return new ArrayList<>();
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        List<String> roles = new ArrayList<>();
        
        // 根据用户 ID 获取用户信息
        SysUser user = sysUserService.getById(Long.parseLong(loginId.toString()));
        if (user != null) {
            // role=1 为管理员
            if (user.getRole() == 1) {
                roles.add("admin");
            }
        }
        
        return roles;
    }
}
