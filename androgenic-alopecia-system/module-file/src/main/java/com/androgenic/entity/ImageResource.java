package com.androgenic.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 图片资源实体类
 */
@Data
@TableName("Image_Resource")
public class ImageResource {

    /**
     * 图片 ID
     */
    @TableId(value = "image_id", type = IdType.AUTO)
    private Long imageId;

    /**
     * 所属用户 ID
     */
    private Long userId;

    /**
     * 图片存储路径/URL
     */
    private String imageUrl;

    /**
     * 文件大小 (字节)
     */
    private Integer fileSize;

    /**
     * MIME 类型 (如 image/jpeg)
     */
    private String fileType;

    /**
     * 上传时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime uploadTime;

    /**
     * 状态 (0:删除，1:有效)
     */
    private Integer status;
}
