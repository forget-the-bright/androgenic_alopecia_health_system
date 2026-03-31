package com.hairloss.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hairloss.system.entity.UserHairImage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 毛发照片服务接口
 */
public interface UserHairImageService extends IService<UserHairImage> {

    /**
     * 上传毛发照片
     * @param userId 用户 ID
     * @param file 文件
     * @param part 部位
     * @param remark 备注
     * @return 上传结果
     */
    boolean uploadImage(Long userId, MultipartFile file, String part, String remark);

    /**
     * 获取用户照片列表
     * @param userId 用户 ID
     * @param part 部位（可选）
     * @param startDate 起始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 照片列表
     */
    List<UserHairImage> getImageList(Long userId, String part, String startDate, String endDate);

    /**
     * 分页获取用户照片列表
     * @param page 分页参数
     * @param userId 用户 ID
     * @param part 部位（可选）
     * @return 分页结果
     */
    Page<UserHairImage> getImagePage(Page<UserHairImage> page, Long userId, String part);

    /**
     * 删除照片
     * @param imageId 照片 ID
     * @param userId 用户 ID
     * @return 删除结果
     */
    boolean deleteImage(Long imageId, Long userId);

    /**
     * 批量删除照片
     * @param imageIds 照片 ID 列表
     * @param userId 用户 ID
     * @return 删除结果
     */
    boolean batchDeleteImages(List<Long> imageIds, Long userId);

    /**
     * 获取所有部位
     * @param userId 用户 ID
     * @return 部位列表
     */
    List<String> getParts(Long userId);

    /**
     * 获取照片详情
     * @param imageId 照片 ID
     * @param userId 用户 ID
     * @return 照片详情
     */
    UserHairImage getImageDetail(Long imageId, Long userId);

    /**
     * 编辑照片信息
     * @param imageId 照片 ID
     * @param userId 用户 ID
     * @param part 部位
     * @param remark 备注
     * @return 更新结果
     */
    boolean updateImage(Long imageId, Long userId, String part, String remark);
}
