package com.androgenic.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * 记录图片关联实体类
 */
@Data
@TableName("Record_Image_Relation")
public class RecordImageRelation {

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
     * 拍摄部位 (top:头顶，front:前额，side:侧面)
     */
    private String shotType;
}
