package com.hairloss.system.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hairloss.system.entity.UserMedicine;
import com.hairloss.system.mapper.UserMedicineMapper;
import com.hairloss.system.service.SysOperationLogService;
import com.hairloss.system.service.UserMedicineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用药方案服务实现类
 */
@Service
public class UserMedicineServiceImpl extends ServiceImpl<UserMedicineMapper, UserMedicine> implements UserMedicineService {

    @Autowired
    private SysOperationLogService sysOperationLogService;

    /**
     * 获取请求 IP 地址
     */
    private String getIpAddress() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String ip = request.getHeader("X-Forwarded-For");
                if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getHeader("X-Real-IP");
                }
                if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getRemoteAddr();
                }
                return ip;
            }
        } catch (Exception e) {
            // 忽略异常
        }
        return "0.0.0.0";
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addMedicine(Long userId, UserMedicine medicine) {
        medicine.setUserId(userId);
        medicine.setStatus(1);

        boolean result = this.save(medicine);

        if (result) {
            sysOperationLogService.logOperation(userId, "ADD_MEDICINE", "添加用药方案",
                    "/api/medicine/add", "medicineName=" + medicine.getMedicineName(),
                    getIpAddress(), 1, null);
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateMedicine(UserMedicine medicine) {
        Long userId = StpUtil.getLoginIdAsLong();
        UserMedicine existMedicine = this.getById(medicine.getId());

        if (existMedicine == null || !existMedicine.getUserId().equals(userId)) {
            throw new RuntimeException("用药方案不存在或无权限修改");
        }

        boolean result = this.updateById(medicine);

        if (result) {
            sysOperationLogService.logOperation(userId, "UPDATE_MEDICINE", "更新用药方案",
                    "/api/medicine/update", "medicineId=" + medicine.getId(),
                    getIpAddress(), 1, null);
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteMedicine(Long medicineId, Long userId) {
        UserMedicine medicine = this.getById(medicineId);
        if (medicine == null || !medicine.getUserId().equals(userId)) {
            throw new RuntimeException("用药方案不存在或无权限删除");
        }

        boolean result = this.removeById(medicineId);

        if (result) {
            sysOperationLogService.logOperation(userId, "DELETE_MEDICINE", "删除用药方案",
                    "/api/medicine/delete", "medicineId=" + medicineId,
                    getIpAddress(), 1, null);
        }

        return result;
    }

    @Override
    public List<UserMedicine> getMedicineList(Long userId) {
        LambdaQueryWrapper<UserMedicine> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserMedicine::getUserId, userId)
                .orderByDesc(UserMedicine::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public UserMedicine getMedicineDetail(Long medicineId, Long userId) {
        UserMedicine medicine = this.getById(medicineId);
        if (medicine == null || !medicine.getUserId().equals(userId)) {
            throw new RuntimeException("用药方案不存在或无权限访问");
        }
        return medicine;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateMedicineStatus(Long medicineId, Integer status, Long userId) {
        UserMedicine medicine = this.getById(medicineId);
        if (medicine == null || !medicine.getUserId().equals(userId)) {
            throw new RuntimeException("用药方案不存在或无权限修改");
        }

        medicine.setStatus(status);
        boolean result = this.updateById(medicine);

        if (result) {
            sysOperationLogService.logOperation(userId, "UPDATE_MEDICINE_STATUS", "更新用药方案状态",
                    "/api/medicine/status", "medicineId=" + medicineId + ",status=" + status,
                    getIpAddress(), 1, null);
        }

        return result;
    }
}
