package com.hairloss.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hairloss.system.entity.UserMedicine;

import java.util.List;

/**
 * 用药方案服务接口
 */
public interface UserMedicineService extends IService<UserMedicine> {

    /**
     * 添加用药方案
     * @param userId 用户 ID
     * @param medicine 用药方案
     * @return 添加结果
     */
    boolean addMedicine(Long userId, UserMedicine medicine);

    /**
     * 更新用药方案
     * @param medicine 用药方案
     * @return 更新结果
     */
    boolean updateMedicine(UserMedicine medicine);

    /**
     * 删除用药方案
     * @param medicineId 方案 ID
     * @param userId 用户 ID
     * @return 删除结果
     */
    boolean deleteMedicine(Long medicineId, Long userId);

    /**
     * 获取用户用药方案列表
     * @param userId 用户 ID
     * @return 用药方案列表
     */
    List<UserMedicine> getMedicineList(Long userId);

    /**
     * 获取用药方案详情
     * @param medicineId 方案 ID
     * @param userId 用户 ID
     * @return 用药方案详情
     */
    UserMedicine getMedicineDetail(Long medicineId, Long userId);

    /**
     * 更新用药方案状态
     * @param medicineId 方案 ID
     * @param status 状态
     * @param userId 用户 ID
     * @return 更新结果
     */
    boolean updateMedicineStatus(Long medicineId, Integer status, Long userId);
}
