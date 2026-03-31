package com.aha.dao.mapper;

import com.aha.dao.entity.MedicationRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用药记录 Mapper 接口
 */
@Mapper
public interface MedicationRecordMapper extends BaseMapper<MedicationRecord> {
}
