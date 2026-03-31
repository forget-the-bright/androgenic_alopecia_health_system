package com.hairloss.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hairloss.system.entity.MedicineClock;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 用药打卡服务接口
 */
public interface MedicineClockService extends IService<MedicineClock> {

    /**
     * 打卡
     * @param userId 用户 ID
     * @param medicineId 用药方案 ID
     * @param date 打卡日期
     * @param status 打卡状态
     * @return 打卡结果
     */
    boolean clockIn(Long userId, Long medicineId, LocalDate date, Integer status);

    /**
     * 获取用户打卡记录
     * @param userId 用户 ID
     * @param medicineId 用药方案 ID（可选）
     * @param month 月份（可选，格式：yyyy-MM）
     * @return 打卡记录列表
     */
    List<MedicineClock> getClockRecords(Long userId, Long medicineId, String month);

    /**
     * 获取打卡统计
     * @param userId 用户 ID
     * @param medicineId 用药方案 ID
     * @return 统计数据
     */
    Map<String, Object> getClockStatistics(Long userId, Long medicineId);

    /**
     * 获取用户所有用药方案的打卡统计
     * @param userId 用户 ID
     * @return 统计数据列表
     */
    List<Map<String, Object>> getAllClockStatistics(Long userId);

    /**
     * 补打卡
     * @param userId 用户 ID
     * @param medicineId 用药方案 ID
     * @param date 补卡日期
     * @param status 打卡状态
     * @return 补卡结果
     */
    boolean makeUpClock(Long userId, Long medicineId, LocalDate date, Integer status);

    /**
     * 删除打卡记录
     * @param clockId 打卡记录 ID
     * @param userId 用户 ID
     * @return 删除结果
     */
    boolean deleteClock(Long clockId, Long userId);

    /**
     * 获取打卡记录详情
     * @param clockId 打卡记录 ID
     * @param userId 用户 ID
     * @return 打卡记录详情
     */
    MedicineClock getClockDetail(Long clockId, Long userId);
}
