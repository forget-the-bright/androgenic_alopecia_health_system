package com.hairloss.system.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hairloss.system.common.Result;
import com.hairloss.system.entity.SysConfig;
import com.hairloss.system.entity.SysOperationLog;
import com.hairloss.system.entity.SysUser;
import com.hairloss.system.entity.UserHairImage;
import com.hairloss.system.entity.AiAnalysis;
import com.hairloss.system.service.SysConfigService;
import com.hairloss.system.service.SysOperationLogService;
import com.hairloss.system.service.SysUserService;
import com.hairloss.system.service.UserHairImageService;
import com.hairloss.system.service.AiAnalysisService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统管理控制器
 */
@RestController
@RequestMapping("/api/admin")
@Api(tags = "系统管理")
public class AdminController {

    @Autowired
    private SysConfigService sysConfigService;

    @Autowired
    private SysOperationLogService sysOperationLogService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private UserHairImageService userHairImageService;

    @Autowired
    private AiAnalysisService aiAnalysisService;

    @GetMapping("/statistics")
    @SaCheckRole("admin")
    @ApiOperation("获取系统统计数据")
    public Result<Map<String, Object>> getStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // 用户总数
        stats.put("userCount", sysUserService.count());

        // 照片总数
        stats.put("photoCount", userHairImageService.count());

        // AI 分析次数
        stats.put("analysisCount", aiAnalysisService.count());

        return Result.success(stats);
    }

    @GetMapping("/config/list")
    @SaCheckRole("admin")
    @ApiOperation("获取系统配置列表")
    public Result<Page<SysConfig>> getConfigList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String type) {
        Page<SysConfig> page = new Page<>(pageNum, pageSize);
        return Result.success(sysConfigService.getConfigPage(page, type));
    }

    @PostMapping("/config")
    @SaCheckRole("admin")
    @ApiOperation("新增系统配置")
    public Result<Void> addConfig(@RequestBody SysConfig config) {
        sysConfigService.addConfig(config);
        return Result.success();
    }

    @PutMapping("/config/{id}")
    @SaCheckRole("admin")
    @ApiOperation("更新系统配置")
    public Result<Void> updateConfig(
            @PathVariable Long id,
            @RequestBody SysConfig config) {
        config.setId(id);
        sysConfigService.updateConfigById(config);
        return Result.success();
    }

    @DeleteMapping("/config/{id}")
    @SaCheckRole("admin")
    @ApiOperation("删除系统配置")
    public Result<Void> deleteConfig(@PathVariable Long id) {
        sysConfigService.removeById(id);
        return Result.success();
    }

    @GetMapping("/user/{userId}/images")
    @SaCheckRole("admin")
    @ApiOperation("获取指定用户的照片列表")
    public Result<List<UserHairImage>> getUserImages(
            @PathVariable Long userId,
            @RequestParam(required = false) String part,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        List<UserHairImage> list = userHairImageService.getImageList(userId, part, startDate, endDate);
        return Result.success(list);
    }

    @GetMapping("/user/{userId}/analyses")
    @SaCheckRole("admin")
    @ApiOperation("获取指定用户的分析记录")
    public Result<List<AiAnalysis>> getUserAnalyses(@PathVariable Long userId) {
        List<AiAnalysis> list = aiAnalysisService.getAnalysisList(userId);
        return Result.success(list);
    }

    @GetMapping("/user/{userId}")
    @SaCheckRole("admin")
    @ApiOperation("获取用户详情（包含统计）")
    public Result<Map<String, Object>> getUserDetail(@PathVariable Long userId) {
        Map<String, Object> detail = new HashMap<>();
        
        // 用户信息
        SysUser user = sysUserService.getById(userId);
        detail.put("user", user);
        
        // 统计信息
        detail.put("photoCount", userHairImageService.count(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<UserHairImage>()
                .eq(UserHairImage::getUserId, userId)));
        detail.put("analysisCount", aiAnalysisService.count(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AiAnalysis>()
                .eq(AiAnalysis::getUserId, userId)));
        
        return Result.success(detail);
    }

    @GetMapping("/log/list")
    @SaCheckRole("admin")
    @ApiOperation("获取操作日志列表")
    public Result<Page<SysOperationLog>> getLogList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String operationType,
            @RequestParam(required = false) Integer status) {
        Page<SysOperationLog> page = new Page<>(pageNum, pageSize);
        Page<SysOperationLog> result = sysOperationLogService.getLogPage(page, userId, operationType, status);
        return Result.success(result);
    }
}
