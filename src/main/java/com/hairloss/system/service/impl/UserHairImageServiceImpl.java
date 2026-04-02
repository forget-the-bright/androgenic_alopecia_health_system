package com.hairloss.system.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hairloss.system.entity.UserHairImage;
import com.hairloss.system.mapper.UserHairImageMapper;
import com.hairloss.system.service.SysOperationLogService;
import com.hairloss.system.service.UserHairImageService;
import com.hairloss.system.utils.TimeUtil;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 毛发照片服务实现类
 */
@Service
public class UserHairImageServiceImpl extends ServiceImpl<UserHairImageMapper, UserHairImage> implements UserHairImageService {

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucket-name:hair-loss-images}")
    private String bucketName;

    @Autowired
    private SysOperationLogService sysOperationLogService;

    /**
     * 获取请求 IP 地址
     */
    private String getIpAddress() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String ip = request.getHeader("X-Forwarded-For");
                if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getHeader("X-Real-IP");
                }
                if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getRemoteAddr();
                }
                return ip;
            }
        } catch (Exception e) {
            // 忽略异常
        }
        return "0.0.0.0";
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean uploadImage(Long userId, MultipartFile file, String part, String remark) {
        try {
            // 检查 bucket 是否存在，不存在则创建
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }

            // 生成文件路径
            String originalFilename = file.getOriginalFilename();
            String suffix = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : ".jpg";
            String fileName = UUID.randomUUID().toString().replace("-", "") + suffix;
            String minioPath = userId + "/" + part + "/" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + "-" + fileName;

            // 上传到 MinIO
            InputStream inputStream = file.getInputStream();
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(minioPath)
                    .stream(inputStream, inputStream.available(), -1)
                    .contentType(file.getContentType())
                    .build());
            inputStream.close();

            // 生成访问 URL（7 天有效期）
            String fileUrl = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucketName)
                    .object(minioPath)
                    .build());

            // 保存记录
            UserHairImage hairImage = new UserHairImage();
            hairImage.setUserId(userId);
            hairImage.setPart(part);
            hairImage.setFileUrl(fileUrl);
            hairImage.setMinioPath(minioPath);
            hairImage.setFileName(fileName);
            hairImage.setFileSize(file.getSize());
            hairImage.setFileType(suffix.replace(".", "").toLowerCase());
            hairImage.setUploadDate(TimeUtil.today());
            hairImage.setUploadTime(TimeUtil.now());
            hairImage.setRemark(remark);

            boolean result = this.save(hairImage);

            if (result) {
                sysOperationLogService.logOperation(userId, "UPLOAD_IMAGE", "上传毛发照片",
                        "/api/image/upload", "part=" + part, getIpAddress(), 1, null);
            }

            return result;
        } catch (Exception e) {
            sysOperationLogService.logOperation(userId, "UPLOAD_IMAGE", "上传毛发照片失败",
                    "/api/image/upload", "part=" + part, getIpAddress(), 0, e.getMessage());
            throw new RuntimeException("上传失败：" + e.getMessage());
        }
    }

    @Override
    public List<UserHairImage> getImageList(Long userId, String part, String startDate, String endDate) {
        LambdaQueryWrapper<UserHairImage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserHairImage::getUserId, userId);
        
        if (part != null && !part.isEmpty()) {
            wrapper.eq(UserHairImage::getPart, part);
        }
        
        if (startDate != null && !startDate.isEmpty()) {
            wrapper.ge(UserHairImage::getUploadDate, startDate);
        }
        
        if (endDate != null && !endDate.isEmpty()) {
            wrapper.le(UserHairImage::getUploadDate, endDate);
        }
        
        wrapper.orderByDesc(UserHairImage::getUploadDate)
                .orderByDesc(UserHairImage::getUploadTime);
        return this.list(wrapper);
    }

    @Override
    public Page<UserHairImage> getImagePage(Page<UserHairImage> page, Long userId, String part) {
        LambdaQueryWrapper<UserHairImage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserHairImage::getUserId, userId);
        if (part != null && !part.isEmpty()) {
            wrapper.eq(UserHairImage::getPart, part);
        }
        wrapper.orderByDesc(UserHairImage::getUploadDate)
                .orderByDesc(UserHairImage::getUploadTime);
        return this.page(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteImage(Long imageId, Long userId) {
        UserHairImage image = this.getById(imageId);
        if (image == null) {
            throw new RuntimeException("照片不存在");
        }

        if (!image.getUserId().equals(userId)) {
            throw new RuntimeException("无权限删除该照片");
        }

        // 删除 MinIO 文件
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(image.getMinioPath())
                    .build());
        } catch (Exception e) {
            // 文件删除失败不影响数据库删除
        }

        boolean result = this.removeById(imageId);

        if (result) {
            sysOperationLogService.logOperation(userId, "DELETE_IMAGE", "删除毛发照片",
                    "/api/image/delete", "imageId=" + imageId, getIpAddress(), 1, null);
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDeleteImages(List<Long> imageIds, Long userId) {
        for (Long imageId : imageIds) {
            this.deleteImage(imageId, userId);
        }
        return true;
    }

    @Override
    public List<String> getParts(Long userId) {
        LambdaQueryWrapper<UserHairImage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserHairImage::getUserId, userId)
                .select(UserHairImage::getPart)
                .groupBy(UserHairImage::getPart);
        return this.list(wrapper).stream()
                .map(UserHairImage::getPart)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public UserHairImage getImageDetail(Long imageId, Long userId) {
        UserHairImage image = this.getById(imageId);
        if (image == null || !image.getUserId().equals(userId)) {
            throw new RuntimeException("照片不存在");
        }
        return image;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateImage(Long imageId, Long userId, String part, String remark, String uploadTime) {
        UserHairImage image = this.getById(imageId);
        if (image == null || !image.getUserId().equals(userId)) {
            throw new RuntimeException("照片不存在或无权限修改");
        }

        image.setPart(part);
        image.setRemark(remark);
        
        // 解析时间字符串为 LocalDateTime（假设输入格式为 yyyy-MM-dd'T'HH:mm）
        if (uploadTime != null && !uploadTime.isEmpty()) {
            try {
                // 前端传递的格式是 yyyy-MM-dd'T'HH:mm (datetime-local)
                LocalDateTime localDateTime = LocalDateTime.parse(uploadTime);
                image.setUploadTime(localDateTime);
                // 同时更新 uploadDate 以保持一致
                image.setUploadDate(localDateTime.toLocalDate());
            } catch (Exception e) {
                throw new RuntimeException("时间格式错误：" + uploadTime);
            }
        }
        
        boolean result = this.updateById(image);

        if (result) {
            sysOperationLogService.logOperation(userId, "UPDATE_IMAGE", "编辑毛发照片",
                    "/api/image/" + imageId, "part=" + part, getIpAddress(), 1, null);
        }

        return result;
    }
}
