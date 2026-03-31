package com.hairloss.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 毛发照片表
 */
@Data
@TableName("user_hair_image")
public class UserHairImage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 照片唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关联用户 ID
     */
    private Long userId;

    /**
     * 拍摄部位（头顶/前额/两侧等）
     */
    private String part;

    /**
     * MinIO 文件访问 URL
     */
    private String fileUrl;

    /**
     * MinIO 文件存储路径
     */
    private String minioPath;

    /**
     * 文件名（含后缀）
     */
    private String fileName;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 文件类型（jpg/png）
     */
    private String fileType;

    /**
     * 上传日期
     */
    private LocalDate uploadDate;

    /**
     * 上传时间
     */
    private LocalDateTime uploadTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
