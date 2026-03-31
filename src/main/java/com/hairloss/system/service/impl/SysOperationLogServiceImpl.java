package com.hairloss.system.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hairloss.system.entity.SysOperationLog;
import com.hairloss.system.mapper.SysOperationLogMapper;
import com.hairloss.system.service.SysOperationLogService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 系统操作日志服务实现类
 */
@Service
public class SysOperationLogServiceImpl extends ServiceImpl<SysOperationLogMapper, SysOperationLog> implements SysOperationLogService {

    @Override
    public void logOperation(Long userId, String operationType, String operationDesc,
                             String requestUrl, String requestParam, String ipAddress,
                             Integer status, String errorMsg) {
        SysOperationLog log = new SysOperationLog();
        log.setUserId(userId);
        log.setOperationType(operationType);
        log.setOperationDesc(operationDesc);
        log.setRequestUrl(requestUrl);
        log.setRequestParam(requestParam != null && requestParam.length() > 500
                ? requestParam.substring(0, 500) : requestParam);
        log.setIpAddress(ipAddress);
        log.setOperationTime(LocalDateTime.now());
        log.setStatus(status);
        log.setErrorMsg(errorMsg);

        this.save(log);
    }

    @Override
    public Page<SysOperationLog> getLogPage(Page<SysOperationLog> page, Long userId, String operationType, Integer status) {
        LambdaQueryWrapper<SysOperationLog> wrapper = new LambdaQueryWrapper<>();

        if (userId != null && userId > 0) {
            wrapper.eq(SysOperationLog::getUserId, userId);
        }

        if (StringUtils.hasText(operationType)) {
            wrapper.eq(SysOperationLog::getOperationType, operationType);
        }

        if (status != null) {
            wrapper.eq(SysOperationLog::getStatus, status);
        }

        wrapper.orderByDesc(SysOperationLog::getOperationTime);
        return this.page(page, wrapper);
    }

    @Override
    public List<SysOperationLog> getUserLogs(Long userId, Integer limit) {
        LambdaQueryWrapper<SysOperationLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysOperationLog::getUserId, userId)
                .orderByDesc(SysOperationLog::getOperationTime);

        if (limit != null) {
            wrapper.last("LIMIT " + limit);
        }

        return this.list(wrapper);
    }
}
