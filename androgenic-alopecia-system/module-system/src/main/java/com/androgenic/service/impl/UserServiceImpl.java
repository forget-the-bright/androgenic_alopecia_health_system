package com.androgenic.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.androgenic.entity.User;
import com.androgenic.exception.BusinessException;
import com.androgenic.result.ResultCode;
import com.androgenic.service.UserService;
import com.androgenic.service.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public User getByPhone(String phone) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, phone);
        return this.getOne(wrapper);
    }

    @Override
    public String login(String phone, String password) {
        User user = getByPhone(phone);
        if (user == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "用户不存在");
        }
        if (user.getStatus() == 0) {
            throw new BusinessException(ResultCode.ACCOUNT_DISABLED, "账号已被禁用");
        }
        // TODO: 这里需要实现密码校验逻辑（实际项目中应使用加密密码）
        // if (!passwordEncoder.matches(password, user.getPassword())) { ... }
        
        // 登录成功，生成 token
        StpUtil.login(user.getUserId());
        return StpUtil.getTokenValue();
    }

    @Override
    public boolean register(User user) {
        // 检查手机号是否已存在
        User existUser = getByPhone(user.getPhone());
        if (existUser != null) {
            throw new BusinessException("手机号已注册");
        }
        // 设置默认状态
        user.setStatus(1);
        // TODO: 这里需要实现密码加密逻辑
        return this.save(user);
    }
}
