package com.hairloss.system.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.hairloss.system.common.Result;
import com.hairloss.system.entity.UserMedicine;
import com.hairloss.system.service.UserMedicineService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 用药方案控制器
 */
@RestController
@RequestMapping("/api/medicine")
@Api(tags = "用药管理")
public class UserMedicineController {

    @Autowired
    private UserMedicineService userMedicineService;

    @PostMapping("/add")
    @ApiOperation("添加用药方案")
    public Result<Void> addMedicine(@RequestBody UserMedicine medicine) {
        if (medicine.getMedicineName() == null || medicine.getMedicineName().isEmpty()) {
            return Result.error("药物名称不能为空");
        }

        Long userId = StpUtil.getLoginIdAsLong();
        userMedicineService.addMedicine(userId, medicine);
        return Result.success();
    }

    @PutMapping("/update")
    @ApiOperation("更新用药方案")
    public Result<Void> updateMedicine(@RequestBody UserMedicine medicine) {
        if (medicine.getId() == null) {
            return Result.error("用药方案 ID 不能为空");
        }

        userMedicineService.updateMedicine(medicine);
        return Result.success();
    }

    @DeleteMapping("/{medicineId}")
    @ApiOperation("删除用药方案")
    public Result<Void> deleteMedicine(@PathVariable Long medicineId) {
        Long userId = StpUtil.getLoginIdAsLong();
        userMedicineService.deleteMedicine(medicineId, userId);
        return Result.success();
    }

    @GetMapping("/list")
    @ApiOperation("获取用药方案列表")
    public Result<List<UserMedicine>> getMedicineList() {
        Long userId = StpUtil.getLoginIdAsLong();
        return Result.success(userMedicineService.getMedicineList(userId));
    }

    @GetMapping("/{medicineId}")
    @ApiOperation("获取用药方案详情")
    public Result<UserMedicine> getMedicineDetail(@PathVariable Long medicineId) {
        Long userId = StpUtil.getLoginIdAsLong();
        return Result.success(userMedicineService.getMedicineDetail(medicineId, userId));
    }

    @PostMapping("/status")
    @ApiOperation("更新用药方案状态")
    public Result<Void> updateMedicineStatus(@RequestBody Map<String, Object> params) {
        Long medicineId = Long.parseLong(params.get("medicineId").toString());
        Integer status = (Integer) params.get("status");

        Long userId = StpUtil.getLoginIdAsLong();
        userMedicineService.updateMedicineStatus(medicineId, status, userId);
        return Result.success();
    }
}
