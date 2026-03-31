package com.aha.dao.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;

/**
 * 记录图片关联表实体类
 */
@Data
@TableName("Record_Image_Relation")
public class RecordImageRelation implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 脱发记录 ID
     */
    private Long recordId;

    /**
     * 图片 ID
     */
    private Long imageId;

    /**
     * 拍摄部位 (top/front/side)
     */
    private String shotType;
}
