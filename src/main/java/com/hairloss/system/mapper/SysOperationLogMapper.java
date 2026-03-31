package com.hairloss.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hairloss.system.entity.SysOperationLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统操作日志 Mapper 接口
 */
@Mapper
public interface SysOperationLogMapper extends BaseMapper<SysOperationLog> {
}
