package com.hairloss.system.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hairloss.system.common.Result;
import com.hairloss.system.entity.UserHairImage;
import com.hairloss.system.service.UserHairImageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 毛发照片控制器
 */
@RestController
@RequestMapping("/api/image")
@Api(tags = "毛发照片管理")
public class UserHairImageController {

    @Autowired
    private UserHairImageService userHairImageService;

    @PostMapping("/upload")
    @ApiOperation("上传毛发照片")
    public Result<Void> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("part") String part,
            @RequestParam(value = "remark", required = false) String remark) {

        if (file.isEmpty()) {
            return Result.error("请选择文件");
        }

        // 验证文件大小（10MB）
        if (file.getSize() > 10 * 1024 * 1024) {
            return Result.error("文件大小不能超过 10MB");
        }

        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/png"))) {
            return Result.error("只支持 JPG 和 PNG 格式的图片");
        }

        Long userId = StpUtil.getLoginIdAsLong();
        userHairImageService.uploadImage(userId, file, part, remark);
        return Result.success();
    }

    @GetMapping("/list")
    @ApiOperation("获取照片列表")
    public Result<List<UserHairImage>> getImageList(
            @RequestParam(required = false) String part,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        Long userId = StpUtil.getLoginIdAsLong();
        List<UserHairImage> list = userHairImageService.getImageList(userId, part, startDate, endDate);
        return Result.success(list);
    }

    @GetMapping("/page")
    @ApiOperation("分页获取照片列表")
    public Result<Page<UserHairImage>> getImagePage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String part) {
        Long userId = StpUtil.getLoginIdAsLong();
        Page<UserHairImage> page = new Page<>(pageNum, pageSize);
        Page<UserHairImage> result = userHairImageService.getImagePage(page, userId, part);
        return Result.success(result);
    }

    @GetMapping("/parts")
    @ApiOperation("获取所有部位")
    public Result<List<String>> getParts() {
        Long userId = StpUtil.getLoginIdAsLong();
        return Result.success(userHairImageService.getParts(userId));
    }

    @GetMapping("/{imageId}")
    @ApiOperation("获取照片详情")
    public Result<UserHairImage> getImageDetail(@PathVariable Long imageId) {
        Long userId = StpUtil.getLoginIdAsLong();
        return Result.success(userHairImageService.getImageDetail(imageId, userId));
    }

    @DeleteMapping("/{imageId}")
    @ApiOperation("删除照片")
    public Result<Void> deleteImage(@PathVariable Long imageId) {
        Long userId = StpUtil.getLoginIdAsLong();
        userHairImageService.deleteImage(imageId, userId);
        return Result.success();
    }

    @PostMapping("/batch-delete")
    @ApiOperation("批量删除照片")
    public Result<Void> batchDeleteImages(@RequestBody List<Long> imageIds) {
        Long userId = StpUtil.getLoginIdAsLong();
        userHairImageService.batchDeleteImages(imageIds, userId);
        return Result.success();
    }

    @PutMapping("/{imageId}")
    @ApiOperation("编辑照片信息")
    public Result<Void> updateImage(
            @PathVariable Long imageId,
            @RequestBody Map<String, String> params) {
        Long userId = StpUtil.getLoginIdAsLong();
        String part = params.get("part");
        String remark = params.get("remark");
        
        userHairImageService.updateImage(imageId, userId, part, remark);
        return Result.success();
    }
}
