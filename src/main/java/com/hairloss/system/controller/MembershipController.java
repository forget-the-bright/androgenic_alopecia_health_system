package com.hairloss.system.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.hairloss.system.common.Result;
import com.hairloss.system.entity.UserMembership;
import com.hairloss.system.service.UserMembershipService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 会员管理控制器
 */
@RestController
@RequestMapping("/api/membership")
@Api(tags = "会员管理")
public class MembershipController {

    @Autowired
    private UserMembershipService userMembershipService;

    @GetMapping("/info")
    @ApiOperation("获取会员信息")
    public Result<UserMembership> getMembershipInfo() {
        Long userId = StpUtil.getLoginIdAsLong();
        UserMembership membership = userMembershipService.getMembershipInfo(userId);
        return Result.success(membership);
    }

    @PostMapping("/purchase")
    @ApiOperation("购买会员")
    public Result<Map<String, Object>> purchaseMembership(@RequestBody Map<String, Integer> params) {
        Long userId = StpUtil.getLoginIdAsLong();
        Integer membershipLevel = params.get("membershipLevel");
        
        if (membershipLevel == null || (membershipLevel != 1 && membershipLevel != 2)) {
            return Result.error("无效的会员等级");
        }
        
        Map<String, Object> result = userMembershipService.purchaseMembership(userId, membershipLevel);
        
        if ((Boolean) result.get("success")) {
            return Result.success(result);
        } else {
            return Result.error((String) result.get("message"));
        }
    }

    @PostMapping("/upgrade")
    @ApiOperation("升级会员")
    public Result<Map<String, Object>> upgradeMembership() {
        Long userId = StpUtil.getLoginIdAsLong();
        Map<String, Object> result = userMembershipService.upgradeMembership(userId);

        if ((Boolean) result.get("success")) {
            return Result.success(result);
        } else {
            return Result.error((String) result.get("message"));
        }
    }

    @GetMapping("/upgrade-info")
    @ApiOperation("获取升级信息（不执行升级）")
    public Result<Map<String, Object>> getUpgradeInfo() {
        Long userId = StpUtil.getLoginIdAsLong();
        Map<String, Object> result = userMembershipService.getUpgradeInfo(userId);

        if ((Boolean) result.get("success")) {
            return Result.success(result);
        } else {
            return Result.error((String) result.get("message"));
        }
    }

    @GetMapping("/check-permission")
    @ApiOperation("检查 AI 分析权限")
    public Result<Map<String, Object>> checkAnalysisPermission() {
        Long userId = StpUtil.getLoginIdAsLong();
        Map<String, Object> result = userMembershipService.checkAnalysisPermission(userId);
        return Result.success(result);
    }
}
