package com.hairloss.system.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.hairloss.system.common.Result;
import com.hairloss.system.dto.MedicineDetailStats;
import com.hairloss.system.entity.MedicineClock;
import com.hairloss.system.service.MedicineClockService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 用药打卡控制器
 */
@RestController
@RequestMapping("/api/clock")
@Api(tags = "用药打卡")
public class MedicineClockController {

    @Autowired
    private MedicineClockService medicineClockService;

    @PostMapping("/in")
    @ApiOperation("打卡")
    public Result<Void> clockIn(@RequestBody Map<String, Object> params) {
        Long medicineId = Long.parseLong(params.get("medicineId").toString());
        String dateStr = params.get("date").toString();
        Integer status = (Integer) params.get("status");

        LocalDate date = LocalDate.parse(dateStr);
        Long userId = StpUtil.getLoginIdAsLong();

        medicineClockService.clockIn(userId, medicineId, date, status);
        return Result.success();
    }

    @GetMapping("/records")
    @ApiOperation("获取打卡记录")
    public Result<List<MedicineClock>> getClockRecords(
            @RequestParam(required = false) Long medicineId,
            @RequestParam(required = false) String month) {
        Long userId = StpUtil.getLoginIdAsLong();
        return Result.success(medicineClockService.getClockRecords(userId, medicineId, month));
    }

    @GetMapping("/statistics")
    @ApiOperation("获取打卡统计")
    public Result<Map<String, Object>> getClockStatistics(
            @RequestParam Long medicineId) {
        Long userId = StpUtil.getLoginIdAsLong();
        return Result.success(medicineClockService.getClockStatistics(userId, medicineId));
    }

    @GetMapping("/all-statistics")
    @ApiOperation("获取所有用药方案的打卡统计")
    public Result<List<Map<String, Object>>> getAllClockStatistics() {
        Long userId = StpUtil.getLoginIdAsLong();
        return Result.success(medicineClockService.getAllClockStatistics(userId));
    }

    @PostMapping("/makeup")
    @ApiOperation("补打卡")
    public Result<Void> makeUpClock(@RequestBody Map<String, Object> params) {
        Long medicineId = Long.parseLong(params.get("medicineId").toString());
        String dateStr = params.get("date").toString();
        Integer status = (Integer) params.get("status");

        LocalDate date = LocalDate.parse(dateStr);
        Long userId = StpUtil.getLoginIdAsLong();

        medicineClockService.makeUpClock(userId, medicineId, date, status);
        return Result.success();
    }

    @DeleteMapping("/{clockId}")
    @ApiOperation("删除打卡记录")
    public Result<Void> deleteClock(@PathVariable Long clockId) {
        Long userId = StpUtil.getLoginIdAsLong();
        medicineClockService.deleteClock(clockId, userId);
        return Result.success();
    }

    @GetMapping("/record/{clockId}")
    @ApiOperation("获取打卡记录详情")
    public Result<MedicineClock> getClockDetail(@PathVariable Long clockId) {
        Long userId = StpUtil.getLoginIdAsLong();
        return Result.success(medicineClockService.getClockDetail(clockId, userId));
    }

    @GetMapping("/detail-stats/{medicineId}")
    @ApiOperation("获取用药详情统计")
    public Result<MedicineDetailStats> getMedicineDetailStats(@PathVariable Long medicineId) {
        Long userId = StpUtil.getLoginIdAsLong();
        return Result.success(medicineClockService.getMedicineDetailStats(userId, medicineId));
    }

    @GetMapping("/all-detail-stats")
    @ApiOperation("获取所有用药方案详情统计")
    public Result<List<MedicineDetailStats>> getAllMedicineDetailStats() {
        Long userId = StpUtil.getLoginIdAsLong();
        return Result.success(medicineClockService.getAllMedicineDetailStats(userId));
    }
}
