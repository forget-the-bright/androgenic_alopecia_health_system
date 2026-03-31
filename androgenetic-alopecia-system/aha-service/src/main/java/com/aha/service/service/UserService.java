package com.aha.service.service;

import com.aha.dao.entity.User;
import com.aha.service.dto.LoginDTO;
import com.aha.service.dto.RegisterDTO;
import com.aha.service.vo.UserVO;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 用户登录
     */
    String login(LoginDTO loginDTO);

    /**
     * 用户注册
     */
    Long register(RegisterDTO registerDTO);

    /**
     * 获取当前登录用户信息
     */
    UserVO getCurrentUser();

    /**
     * 根据 ID 获取用户
     */
    UserVO getUserById(Long userId);

    /**
     * 更新用户信息
     */
    void updateUserInfo(User user);

    /**
     * 根据手机号获取用户
     */
    User getByPhone(String phone);
}
