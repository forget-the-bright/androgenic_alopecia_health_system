package com.hairloss.system.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hairloss.system.dto.MedicineDetailStats;
import com.hairloss.system.entity.MedicineClock;
import com.hairloss.system.entity.UserMedicine;
import com.hairloss.system.mapper.MedicineClockMapper;
import com.hairloss.system.service.MedicineClockService;
import com.hairloss.system.service.SysOperationLogService;
import com.hairloss.system.service.UserMedicineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用药打卡服务实现类
 */
@Service
public class MedicineClockServiceImpl extends ServiceImpl<MedicineClockMapper, MedicineClock> implements MedicineClockService {

    @Autowired
    private SysOperationLogService sysOperationLogService;

    @Autowired
    private UserMedicineService userMedicineService;

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
    public boolean clockIn(Long userId, Long medicineId, LocalDate date, Integer status) {
        // 检查是否已打卡
        MedicineClock existClock = this.getOne(new LambdaQueryWrapper<MedicineClock>()
                .eq(MedicineClock::getUserId, userId)
                .eq(MedicineClock::getMedicineId, medicineId)
                .eq(MedicineClock::getClockDate, date));

        if (existClock != null) {
            throw new RuntimeException("该日期已打卡");
        }

        MedicineClock clock = new MedicineClock();
        clock.setUserId(userId);
        clock.setMedicineId(medicineId);
        clock.setClockDate(date);
        clock.setClockStatus(status);

        boolean result = this.save(clock);

        if (result) {
            sysOperationLogService.logOperation(userId, "CLOCK_IN", "用药打卡",
                    "/api/clock/in", "medicineId=" + medicineId + ",date=" + date,
                    getIpAddress(), 1, null);
        }

        return result;
    }

    @Override
    public List<MedicineClock> getClockRecords(Long userId, Long medicineId, String month) {
        LambdaQueryWrapper<MedicineClock> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicineClock::getUserId, userId);

        if (medicineId != null) {
            wrapper.eq(MedicineClock::getMedicineId, medicineId);
        }

        if (month != null && !month.isEmpty()) {
            YearMonth yearMonth = YearMonth.parse(month, DateTimeFormatter.ofPattern("yyyy-MM"));
            LocalDate startOfMonth = yearMonth.atDay(1);
            LocalDate endOfMonth = yearMonth.atEndOfMonth();
            wrapper.between(MedicineClock::getClockDate, startOfMonth, endOfMonth);
        }

        wrapper.orderByDesc(MedicineClock::getClockDate);
        return this.list(wrapper);
    }

    @Override
    public Map<String, Object> getClockStatistics(Long userId, Long medicineId) {
        List<MedicineClock> allRecords = this.list(new LambdaQueryWrapper<MedicineClock>()
                .eq(MedicineClock::getUserId, userId)
                .eq(MedicineClock::getMedicineId, medicineId)
                .orderByDesc(MedicineClock::getClockDate));

        Map<String, Object> stats = new HashMap<>();

        // 总打卡次数
        stats.put("totalCount", allRecords.size());

        // 已服次数
        long takenCount = allRecords.stream()
                .filter(r -> r.getClockStatus() == 1)
                .count();
        stats.put("takenCount", takenCount);

        // 补卡次数
        long makeUpCount = allRecords.stream()
                .filter(r -> r.getClockStatus() == 2)
                .count();
        stats.put("makeUpCount", makeUpCount);

        // 漏服次数
        long missedCount = allRecords.stream()
                .filter(r -> r.getClockStatus() == 0)
                .count();
        stats.put("missedCount", missedCount);

        // 计算连续打卡天数
        int consecutiveDays = calculateConsecutiveDays(allRecords);
        stats.put("consecutiveDays", consecutiveDays);

        // 计算完成率
        if (!allRecords.isEmpty()) {
            double completionRate = (double) (takenCount + makeUpCount) / allRecords.size() * 100;
            stats.put("completionRate", String.format("%.1f", completionRate));
        } else {
            stats.put("completionRate", "0.0");
        }

        return stats;
    }

    @Override
    public List<Map<String, Object>> getAllClockStatistics(Long userId) {
        // 获取用户所有用药方案
        // 这里简化处理，返回所有打卡统计
        List<Map<String, Object>> allStats = new ArrayList<>();

        // 按用药方案分组统计
        List<Long> medicineIds = this.list(new LambdaQueryWrapper<MedicineClock>()
                        .eq(MedicineClock::getUserId, userId)
                        .select(MedicineClock::getMedicineId))
                .stream()
                .map(MedicineClock::getMedicineId)
                .distinct()
                .collect(Collectors.toList());

        for (Long medicineId : medicineIds) {
            Map<String, Object> stats = this.getClockStatistics(userId, medicineId);
            stats.put("medicineId", medicineId);
            allStats.add(stats);
        }

        return allStats;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean makeUpClock(Long userId, Long medicineId, LocalDate date, Integer status) {
        // 检查是否已打卡
        MedicineClock existClock = this.getOne(new LambdaQueryWrapper<MedicineClock>()
                .eq(MedicineClock::getUserId, userId)
                .eq(MedicineClock::getMedicineId, medicineId)
                .eq(MedicineClock::getClockDate, date));

        if (existClock != null) {
            throw new RuntimeException("该日期已打卡");
        }

        MedicineClock clock = new MedicineClock();
        clock.setUserId(userId);
        clock.setMedicineId(medicineId);
        clock.setClockDate(date);
        clock.setClockStatus(status); // 2 表示补卡

        boolean result = this.save(clock);

        if (result) {
            sysOperationLogService.logOperation(userId, "MAKE_UP_CLOCK", "补打卡",
                    "/api/clock/makeup", "medicineId=" + medicineId + ",date=" + date,
                    getIpAddress(), 1, null);
        }

        return result;
    }

    /**
     * 计算连续打卡天数
     */
    private int calculateConsecutiveDays(List<MedicineClock> records) {
        if (records.isEmpty()) {
            return 0;
        }

        // 按日期排序
        records.sort(Comparator.comparing(MedicineClock::getClockDate).reversed());

        int consecutiveDays = 0;
        LocalDate expectedDate = LocalDate.now();

        for (MedicineClock record : records) {
            // 只统计已服和补卡
            if (record.getClockStatus() == 1 || record.getClockStatus() == 2) {
                if (record.getClockDate().equals(expectedDate)) {
                    consecutiveDays++;
                    expectedDate = expectedDate.minusDays(1);
                } else if (record.getClockDate().isBefore(expectedDate)) {
                    // 日期不连续，中断
                    break;
                }
            }
        }

        return consecutiveDays;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteClock(Long clockId, Long userId) {
        MedicineClock clock = this.getById(clockId);
        if (clock == null || !clock.getUserId().equals(userId)) {
            throw new RuntimeException("打卡记录不存在或无权限删除");
        }

        boolean result = this.removeById(clockId);

        if (result) {
            sysOperationLogService.logOperation(userId, "DELETE_CLOCK", "删除打卡记录",
                    "/api/clock/" + clockId, "clockId=" + clockId, getIpAddress(), 1, null);
        }

        return result;
    }

    @Override
    public MedicineClock getClockDetail(Long clockId, Long userId) {
        MedicineClock clock = this.getById(clockId);
        if (clock == null || !clock.getUserId().equals(userId)) {
            throw new RuntimeException("打卡记录不存在或无权限访问");
        }
        return clock;
    }

    @Override
    public MedicineDetailStats getMedicineDetailStats(Long userId, Long medicineId) {
        UserMedicine medicine = userMedicineService.getById(medicineId);
        if (medicine == null || !medicine.getUserId().equals(userId)) {
            throw new RuntimeException("用药方案不存在或无权限访问");
        }

        MedicineDetailStats stats = new MedicineDetailStats();
        
        stats.setMedicineId(medicine.getId());
        stats.setMedicineName(medicine.getMedicineName());
        stats.setDosage(medicine.getDosage());
        stats.setTakeTime(medicine.getTakeTime());
        stats.setCycle(medicine.getCycle());
        stats.setStatus(medicine.getStatus());
        stats.setRemark(medicine.getRemark());

        List<MedicineClock> allRecords = this.list(new LambdaQueryWrapper<MedicineClock>()
                .eq(MedicineClock::getUserId, userId)
                .eq(MedicineClock::getMedicineId, medicineId)
                .orderByAsc(MedicineClock::getClockDate));

        if (allRecords.isEmpty()) {
            stats.setFirstClockDate(null);
            stats.setLastClockDate(null);
            stats.setTotalDays(0);
            stats.setActualClockDays(0);
            stats.setMissedDays(0);
            stats.setHasBreak(false);
            stats.setBreakCount(0);
            stats.setTotalBreakDays(0);
            stats.setBreakPeriods(new ArrayList<>());
            stats.setCompletionRate(0.0);
            stats.setCurrentConsecutiveDays(0);
            return stats;
        }

        LocalDate firstDate = allRecords.get(0).getClockDate();
        LocalDate lastDate = allRecords.get(allRecords.size() - 1).getClockDate();
        LocalDate today = LocalDate.now();

        stats.setFirstClockDate(firstDate);
        stats.setLastClockDate(lastDate);

        int totalDays = (int) ChronoUnit.DAYS.between(firstDate, today) + 1;
        stats.setTotalDays(totalDays);

        long actualClockDays = allRecords.stream()
                .filter(r -> r.getClockStatus() == 1 || r.getClockStatus() == 2)
                .count();
        stats.setActualClockDays((int) actualClockDays);

        long missedDays = allRecords.stream()
                .filter(r -> r.getClockStatus() == 0)
                .count();
        stats.setMissedDays((int) missedDays);

        List<MedicineDetailStats.BreakPeriod> breakPeriods = analyzeBreakPeriods(allRecords, firstDate, lastDate);
        stats.setBreakPeriods(breakPeriods);
        stats.setBreakCount(breakPeriods.size());
        stats.setHasBreak(!breakPeriods.isEmpty());
        stats.setTotalBreakDays(breakPeriods.stream()
                .mapToInt(MedicineDetailStats.BreakPeriod::getDays)
                .sum());

        double completionRate = totalDays > 0 ? (double) actualClockDays / totalDays * 100 : 0;
        stats.setCompletionRate(Math.round(completionRate * 10) / 10.0);

        int consecutiveDays = calculateConsecutiveDaysFromToday(allRecords);
        stats.setCurrentConsecutiveDays(consecutiveDays);

        return stats;
    }

    @Override
    public List<MedicineDetailStats> getAllMedicineDetailStats(Long userId) {
        List<UserMedicine> medicines = userMedicineService.getMedicineList(userId);
        List<MedicineDetailStats> allStats = new ArrayList<>();
        
        for (UserMedicine medicine : medicines) {
            try {
                MedicineDetailStats stats = getMedicineDetailStats(userId, medicine.getId());
                allStats.add(stats);
            } catch (Exception e) {
                // 忽略单个方案的错误，继续处理其他方案
            }
        }
        
        return allStats;
    }

    private List<MedicineDetailStats.BreakPeriod> analyzeBreakPeriods(List<MedicineClock> records, LocalDate firstDate, LocalDate lastDate) {
        List<MedicineDetailStats.BreakPeriod> breakPeriods = new ArrayList<>();
        
        if (records.isEmpty()) {
            return breakPeriods;
        }

        Set<LocalDate> clockDates = records.stream()
                .filter(r -> r.getClockStatus() == 1 || r.getClockStatus() == 2)
                .map(MedicineClock::getClockDate)
                .collect(Collectors.toSet());

        LocalDate currentDate = firstDate;
        MedicineDetailStats.BreakPeriod currentBreak = null;

        while (!currentDate.isAfter(lastDate)) {
            if (!clockDates.contains(currentDate)) {
                if (currentBreak == null) {
                    currentBreak = new MedicineDetailStats.BreakPeriod();
                    currentBreak.setStartDate(currentDate);
                }
                currentBreak.setEndDate(currentDate);
            } else {
                if (currentBreak != null) {
                    int days = (int) ChronoUnit.DAYS.between(currentBreak.getStartDate(), currentBreak.getEndDate()) + 1;
                    currentBreak.setDays(days);
                    breakPeriods.add(currentBreak);
                    currentBreak = null;
                }
            }
            currentDate = currentDate.plusDays(1);
        }

        if (currentBreak != null) {
            int days = (int) ChronoUnit.DAYS.between(currentBreak.getStartDate(), currentBreak.getEndDate()) + 1;
            currentBreak.setDays(days);
            breakPeriods.add(currentBreak);
        }

        return breakPeriods;
    }

    private int calculateConsecutiveDaysFromToday(List<MedicineClock> records) {
        if (records.isEmpty()) {
            return 0;
        }

        Set<LocalDate> clockDates = records.stream()
                .filter(r -> r.getClockStatus() == 1 || r.getClockStatus() == 2)
                .map(MedicineClock::getClockDate)
                .collect(Collectors.toSet());

        int consecutiveDays = 0;
        LocalDate expectedDate = LocalDate.now();

        while (clockDates.contains(expectedDate)) {
            consecutiveDays++;
            expectedDate = expectedDate.minusDays(1);
        }

        return consecutiveDays;
    }
}
