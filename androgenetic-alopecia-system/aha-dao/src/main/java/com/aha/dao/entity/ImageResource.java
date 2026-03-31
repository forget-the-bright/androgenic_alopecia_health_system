package com.aha.dao.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 图片资源表实体类
 */
@Data
@TableName("Image_Resource")
public class ImageResource implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 图片 ID
     */
    @TableId(value = "image_id", type = IdType.AUTO)
    private Long imageId;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 图片 URL
     */
    private String imageUrl;

    /**
     * 文件大小 (KB)
     */
    private Integer fileSize;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 上传时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime uploadTime;

    /**
     * 状态 (0:删除，1:正常)
     */
    private Integer status;
}
