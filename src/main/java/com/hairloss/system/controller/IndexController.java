package com.hairloss.system.controller;

import cn.dev33.satoken.stp.StpUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

/**
 * 首页控制器
 */
@Controller
public class IndexController {

    @GetMapping("/")
    public Object index() {
        // 已登录跳转到首页，未登录跳转到登录页
        if (StpUtil.isLogin()) {
            return new RedirectView("/index.html");
        } else {
            return new RedirectView("/login.html");
        }
    }
}
