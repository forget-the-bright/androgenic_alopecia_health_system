package com.hairloss.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hairloss.system.entity.MedicineClock;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用药打卡 Mapper 接口
 */
@Mapper
public interface MedicineClockMapper extends BaseMapper<MedicineClock> {
}
