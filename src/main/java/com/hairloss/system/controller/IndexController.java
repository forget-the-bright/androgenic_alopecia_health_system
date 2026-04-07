package com.hairloss.system.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.hairloss.system.common.Result;
import com.hairloss.system.entity.AiAnalysis;
import com.hairloss.system.entity.UserHairImage;
import com.hairloss.system.entity.UserMedicine;
import com.hairloss.system.service.MedicineClockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 首页控制器
 */
@Controller
public class IndexController {
    @Autowired
    private MedicineClockService medicineClockService;

    @GetMapping("/")
    public String index() {
        // 已登录跳转到首页，未登录跳转到登录页
        if (StpUtil.isLogin()) {
            return "index";
        }
        return "login";
    }

    @ResponseBody
    @GetMapping("/api/index/loadStatistics")
    public Result<Map<String, Object>> loadStatistics() {
        LambdaQueryWrapper<UserHairImage> wrapper = Wrappers.lambdaQuery(UserHairImage.class);
        wrapper.eq(UserHairImage::getUserId, StpUtil.getLoginIdAsLong());
        long imageCount = Db.count(wrapper);

        LambdaQueryWrapper<AiAnalysis> analysisLambdaQueryWrapper = Wrappers.lambdaQuery(AiAnalysis.class);
        analysisLambdaQueryWrapper.eq(AiAnalysis::getUserId, StpUtil.getLoginIdAsLong());
        long analysisCount = Db.count(analysisLambdaQueryWrapper);


        LambdaQueryWrapper<UserMedicine> medicineLambdaQueryWrapper = Wrappers.lambdaQuery(UserMedicine.class);
        medicineLambdaQueryWrapper.eq(UserMedicine::getUserId, StpUtil.getLoginIdAsLong());
        long medicineCount = Db.count(medicineLambdaQueryWrapper);


        List<Map<String, Object>> allClockStatistics = medicineClockService.getAllClockStatistics(StpUtil.getLoginIdAsLong());

        HashMap<String, Object> statistics = new HashMap<>();
        statistics.put("imageCount", imageCount);
        statistics.put("analysisCount", analysisCount);
        statistics.put("medicineCount", medicineCount);
        statistics.put("clockStatistics", allClockStatistics);
        return Result.success(statistics);
    }
}
