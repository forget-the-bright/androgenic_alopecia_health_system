package com.hairloss.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hairloss.system.entity.SysOperationLog;

import java.util.List;

/**
 * 系统操作日志服务接口
 */
public interface SysOperationLogService extends IService<SysOperationLog> {

    /**
     * 记录操作日志
     * @param userId 用户 ID
     * @param operationType 操作类型
     * @param operationDesc 操作描述
     * @param requestUrl 请求 URL
     * @param requestParam 请求参数
     * @param ipAddress IP 地址
     * @param status 状态
     * @param errorMsg 错误信息
     */
    void logOperation(Long userId, String operationType, String operationDesc,
                      String requestUrl, String requestParam, String ipAddress,
                      Integer status, String errorMsg);

    /**
     * 分页查询日志
     * @param page 分页参数
     * @param userId 用户 ID（可选）
     * @param operationType 操作类型（可选）
     * @param status 状态（可选）
     * @return 分页结果
     */
    Page<SysOperationLog> getLogPage(Page<SysOperationLog> page, Long userId, String operationType, Integer status);

    /**
     * 获取用户操作日志
     * @param userId 用户 ID
     * @param limit 数量限制
     * @return 日志列表
     */
    List<SysOperationLog> getUserLogs(Long userId, Integer limit);
}
