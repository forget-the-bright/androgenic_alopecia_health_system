package com.hairloss.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hairloss.system.common.Result;
import com.hairloss.system.entity.MembershipPurchaseRecord;
import com.hairloss.system.entity.SysConfig;
import com.hairloss.system.entity.UserMembership;
import com.hairloss.system.mapper.MembershipPurchaseRecordMapper;
import com.hairloss.system.mapper.UserMembershipMapper;
import com.hairloss.system.service.SysConfigService;
import com.hairloss.system.service.UserMembershipService;
import com.hairloss.system.utils.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户会员服务实现类
 */
@Slf4j
@Service
public class UserMembershipServiceImpl extends ServiceImpl<UserMembershipMapper, UserMembership> implements UserMembershipService {

    @Autowired
    private SysConfigService sysConfigService;

    @Autowired
    private MembershipPurchaseRecordMapper membershipPurchaseRecordMapper;

    // 会员等级常量
    private static final int LEVEL_FREE = 0;
    private static final int LEVEL_MONTHLY = 1;
    private static final int LEVEL_YEARLY = 2;

    // 会员价格配置键
    private static final String CONFIG_MONTHLY_PRICE = "membership.monthly.price";
    private static final String CONFIG_YEARLY_PRICE = "membership.yearly.price";
    private static final String CONFIG_MONTHLY_QUOTA = "membership.monthly.quota";
    private static final String CONFIG_YEARLY_QUOTA = "membership.yearly.quota";
    private static final String CONFIG_FREE_QUOTA = "membership.free.quota";

    @Override
    public UserMembership getMembershipInfo(Long userId) {
        UserMembership membership = this.getOrCreateMembership(userId);

        // 检查是否过期
        checkMembershipExpired(membership);

        // 检查并重置月度次数
        checkAndResetMonthlyCount(membership);

        // 计算剩余次数 (确保不为负数)
        int quota = membership.getMonthlyQuota() != null ? membership.getMonthlyQuota() : 0;
        int used = membership.getUsedCountCurrentMonth() != null ? membership.getUsedCountCurrentMonth() : 0;
        int remaining = quota - used;
        membership.setRemainingCount(Math.max(0, remaining));

        // 设置等级名称
        membership.setLevelName(getLevelName(membership.getMembershipLevel()));

        // 检查是否过期
        membership.setExpired(membership.getStatus() == 0);

        // 确保配额字段有值
        if (membership.getMonthlyQuota() == null) {
            if (membership.getMembershipLevel() == LEVEL_FREE) {
                membership.setMonthlyQuota(getMembershipQuota(CONFIG_FREE_QUOTA));
            } else if (membership.getMembershipLevel() == LEVEL_MONTHLY) {
                membership.setMonthlyQuota(getMembershipQuota(CONFIG_MONTHLY_QUOTA));
            } else if (membership.getMembershipLevel() == LEVEL_YEARLY) {
                membership.setMonthlyQuota(getMembershipQuota(CONFIG_YEARLY_QUOTA));
            } else {
                membership.setMonthlyQuota(0);
            }
        }

        // 确保已使用次数有值
        if (membership.getUsedCountCurrentMonth() == null) {
            membership.setUsedCountCurrentMonth(0);
        }

        log.debug("用户 {} 会员信息: level={}, levelName={}, quota={}, used={}, remaining={}, status={}", 
            userId, membership.getMembershipLevel(), membership.getLevelName(), 
            membership.getMonthlyQuota(), membership.getUsedCountCurrentMonth(), 
            membership.getRemainingCount(), membership.getStatus());

        return membership;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> purchaseMembership(Long userId, Integer membershipLevel) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 获取当前会员信息
            UserMembership membership = this.getOrCreateMembership(userId);
            
            // 检查是否已经是该等级
            if (membership.getMembershipLevel().equals(membershipLevel)) {
                result.put("success", false);
                result.put("message", "您已经是该等级会员");
                return result;
            }
            
            // 获取价格配置
            BigDecimal price;
            int days;
            if (membershipLevel == LEVEL_MONTHLY) {
                price = getMembershipPrice(CONFIG_MONTHLY_PRICE);
                days = 30;
            } else if (membershipLevel == LEVEL_YEARLY) {
                price = getMembershipPrice(CONFIG_YEARLY_PRICE);
                days = 365;
            } else {
                result.put("success", false);
                result.put("message", "无效的会员等级");
                return result;
            }
            
            // 生成订单号
            String orderNo = generateOrderNo();
            
            // 获取配额
            int quota = membershipLevel == LEVEL_MONTHLY ?
                getMembershipQuota(CONFIG_MONTHLY_QUOTA) :
                getMembershipQuota(CONFIG_YEARLY_QUOTA);

            // ✅ 修复：有效期从购买日开始计算
            LocalDateTime now = TimeUtil.now();
            LocalDateTime newEndTime = now.plusDays(days);
            
            // 更新会员信息
            membership.setMembershipLevel(membershipLevel);
            membership.setMembershipStartTime(now);
            membership.setMembershipEndTime(newEndTime);
            membership.setMonthlyQuota(quota);
            membership.setUsedCountCurrentMonth(0); // 新购重置次数
            membership.setCurrentMonth(LocalDate.now().getMonthValue());
            membership.setStatus(1);
            
            this.updateById(membership);
            
            // 记录购买记录
            MembershipPurchaseRecord record = new MembershipPurchaseRecord();
            record.setUserId(userId);
            record.setOrderNo(orderNo);
            record.setActionType(1); // 新购
            record.setFromLevel(null);
            record.setToLevel(membershipLevel);
            record.setOriginalPrice(price);
            record.setDiscountAmount(BigDecimal.ZERO);
            record.setActualPayment(price);
            record.setMembershipDays(days);
            record.setOldEndTime(null);
            record.setNewEndTime(newEndTime);
            record.setPaymentStatus(1);
            record.setPaymentTime(now);
            record.setRemark("新购会员");
            
            membershipPurchaseRecordMapper.insert(record);
            
            result.put("success", true);
            result.put("message", "会员开通成功");
            result.put("orderNo", orderNo);
            result.put("price", price);
            result.put("level", membershipLevel);
            result.put("levelName", getLevelName(membershipLevel));
            result.put("endTime", newEndTime);
            
            log.info("用户 {} 购买会员成功，等级：{}", userId, membershipLevel);
            
        } catch (Exception e) {
            log.error("购买会员失败", e);
            result.put("success", false);
            result.put("message", "购买失败：" + e.getMessage());
        }
        
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> upgradeMembership(Long userId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 获取当前会员信息
            UserMembership membership = this.getOrCreateMembership(userId);
            
            // 检查是否可以升级（只允许月度→年度）
            if (membership.getMembershipLevel() != LEVEL_MONTHLY) {
                result.put("success", false);
                result.put("message", "只有月度会员可以升级为年度会员");
                return result;
            }
            
            // 获取价格配置
            BigDecimal monthlyPrice = getMembershipPrice(CONFIG_MONTHLY_PRICE);
            BigDecimal yearlyPrice = getMembershipPrice(CONFIG_YEARLY_PRICE);
            
            // 计算剩余天数
            LocalDateTime now = TimeUtil.now();
            LocalDateTime endTime = membership.getMembershipEndTime();
            long remainingDays = ChronoUnit.DAYS.between(now, endTime);
            remainingDays = Math.max(0, remainingDays);
            
            // 计算差价：年度价格 - 月度价格 × (剩余天数/30)
            BigDecimal remainingValue = monthlyPrice.multiply(
                new BigDecimal(remainingDays).divide(new BigDecimal(30), 2, BigDecimal.ROUND_HALF_UP)
            );
            BigDecimal diffPrice = yearlyPrice.subtract(remainingValue);
            diffPrice = diffPrice.max(BigDecimal.ZERO); // 确保不为负
            
            // 生成订单号
            String orderNo = generateOrderNo();
            
            // 获取年度配额
            int quota = getMembershipQuota(CONFIG_YEARLY_QUOTA);
            
            // 计算新的到期时间（从当前时间开始 365 天）
            LocalDateTime newEndTime = now.plusDays(365);
            
            // 更新会员信息
            membership.setMembershipLevel(LEVEL_YEARLY);
            membership.setMembershipStartTime(now);
            membership.setMembershipEndTime(newEndTime);
            membership.setMonthlyQuota(quota);
            // 已使用次数保留，不重置
            membership.setStatus(1);
            
            this.updateById(membership);
            
            // 记录升级记录
            MembershipPurchaseRecord record = new MembershipPurchaseRecord();
            record.setUserId(userId);
            record.setOrderNo(orderNo);
            record.setActionType(3); // 升级
            record.setFromLevel(LEVEL_MONTHLY);
            record.setToLevel(LEVEL_YEARLY);
            record.setOriginalPrice(yearlyPrice);
            record.setDiscountAmount(remainingValue);
            record.setActualPayment(diffPrice);
            record.setMembershipDays(365);
            record.setOldEndTime(endTime);
            record.setNewEndTime(newEndTime);
            record.setPaymentStatus(1);
            record.setPaymentTime(now);
            record.setRemark("月度升级年度，剩余天数：" + remainingDays + "，补差价：" + diffPrice);
            
            membershipPurchaseRecordMapper.insert(record);
            
            result.put("success", true);
            result.put("message", "会员升级成功");
            result.put("orderNo", orderNo);
            result.put("originalPrice", yearlyPrice);
            result.put("discountAmount", remainingValue);
            result.put("diffPrice", diffPrice);
            result.put("remainingDays", remainingDays);
            result.put("level", LEVEL_YEARLY);
            result.put("levelName", getLevelName(LEVEL_YEARLY));
            result.put("endTime", newEndTime);
            
            log.info("用户 {} 升级会员成功，补差价：{}", userId, diffPrice);

        } catch (Exception e) {
            log.error("升级会员失败", e);
            result.put("success", false);
            result.put("message", "升级失败：" + e.getMessage());
        }

        return result;
    }

    /**
     * 获取会员升级信息
     * <p>计算从月度会员升级到年度会员的差价，基于剩余天数进行按比例抵扣</p>
     * <p>计算公式：差价 = 年度价格 - (月度价格 × 剩余天数 / 30)</p>
     *
     * @param userId 用户ID
     * @return 包含升级信息的Map对象，包含以下字段：
     *         <ul>
     *           <li>success (Boolean): 是否成功获取升级信息</li>
     *           <li>message (String): 提示信息（失败时返回）</li>
     *           <li>remainingDays (Long): 当前会员剩余天数</li>
     *           <li>diffPrice (BigDecimal): 升级需要补的差价</li>
     *           <li>monthlyPrice (BigDecimal): 月度会员价格</li>
     *           <li>yearlyPrice (BigDecimal): 年度会员价格</li>
     *         </ul>
     */
    @Override
    public Map<String, Object> getUpgradeInfo(Long userId) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 获取当前会员信息
            UserMembership membership = this.getOrCreateMembership(userId);

            // 检查是否可以升级（只允许月度→年度）
            if (membership.getMembershipLevel() != LEVEL_MONTHLY) {
                result.put("success", false);
                result.put("message", "只有月度会员可以升级为年度会员");
                return result;
            }

            // 获取价格配置
            BigDecimal monthlyPrice = getMembershipPrice(CONFIG_MONTHLY_PRICE);
            BigDecimal yearlyPrice = getMembershipPrice(CONFIG_YEARLY_PRICE);

            // 计算剩余天数
            LocalDateTime now = TimeUtil.now();
            LocalDateTime endTime = membership.getMembershipEndTime();
            long remainingDays = ChronoUnit.DAYS.between(now, endTime);
            remainingDays = Math.max(0, remainingDays);

            // 计算升级差价，根据剩余天数按比例抵扣
            BigDecimal remainingValue = monthlyPrice.multiply(
                new BigDecimal(remainingDays).divide(new BigDecimal(30), 2, BigDecimal.ROUND_HALF_UP)
            );
            BigDecimal diffPrice = yearlyPrice.subtract(remainingValue);
            diffPrice = diffPrice.max(BigDecimal.ZERO);

            result.put("success", true);
            result.put("remainingDays", remainingDays);
            result.put("diffPrice", diffPrice);
            result.put("monthlyPrice", monthlyPrice);
            result.put("yearlyPrice", yearlyPrice);

        } catch (Exception e) {
            log.error("获取升级信息失败", e);
            result.put("success", false);
            result.put("message", "获取升级信息失败：" + e.getMessage());
        }

        return result;
    }

    @Override
    public void checkAndResetMonthlyCount(UserMembership membership) {
        if (membership == null) return;
        
        int currentMonth = LocalDate.now().getMonthValue();
        
        // 如果是新的自然月，重置次数
        if (!membership.getCurrentMonth().equals(currentMonth)) {
            membership.setUsedCountCurrentMonth(0);
            membership.setCurrentMonth(currentMonth);
            this.updateById(membership);
            log.info("用户 {} 月度次数已重置", membership.getUserId());
        }
    }

    @Override
    public boolean checkMembershipExpired(UserMembership membership) {
        if (membership == null || membership.getMembershipEndTime() == null) {
            return true;
        }
        
        // 检查是否过期
        if (membership.getMembershipEndTime().isBefore(TimeUtil.now())) {
            // 已过期，降级为免费用户
            membership.setMembershipLevel(LEVEL_FREE);
            membership.setStatus(0);
            membership.setMonthlyQuota(getMembershipQuota(CONFIG_FREE_QUOTA));
            this.updateById(membership);
            log.info("用户 {} 会员已过期，已降级为免费用户", membership.getUserId());
            return true;
        }
        
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void incrementUsedCount(Long userId) {
        UserMembership membership = this.getById(userId);
        if (membership != null) {
            int usedCount = membership.getUsedCountCurrentMonth() != null ? 
                membership.getUsedCountCurrentMonth() : 0;
            membership.setUsedCountCurrentMonth(usedCount + 1);
            this.updateById(membership);
        }
    }

    @Override
    public Map<String, Object> checkAnalysisPermission(Long userId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            UserMembership membership = this.getMembershipInfo(userId);
            
            // 检查是否过期
            if (membership.getStatus() == 0) {
                result.put("allowed", false);
                result.put("reason", "您的会员已过期，已恢复为免费用户");
                result.put("membership", membership);
                return result;
            }
            
            // 检查剩余次数
            if (membership.getRemainingCount() <= 0) {
                result.put("allowed", false);
                result.put("reason", "您本月 AI 分析次数已用完，开通会员可继续使用");
                result.put("membership", membership);
                return result;
            }
            
            // 允许使用
            result.put("allowed", true);
            result.put("reason", "验证通过");
            result.put("membership", membership);
            
        } catch (Exception e) {
            result.put("allowed", false);
            result.put("reason", "检查失败：" + e.getMessage());
        }
        
        return result;
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 获取或创建会员记录
     */
    private UserMembership getOrCreateMembership(Long userId) {
        LambdaQueryWrapper<UserMembership> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserMembership::getUserId, userId);
        UserMembership membership = this.getOne(wrapper);
        
        if (membership == null) {
            // 创建免费会员记录
            membership = new UserMembership();
            membership.setUserId(userId);
            membership.setMembershipLevel(LEVEL_FREE);
            membership.setMonthlyQuota(getMembershipQuota(CONFIG_FREE_QUOTA));
            membership.setUsedCountCurrentMonth(0);
            membership.setCurrentMonth(LocalDate.now().getMonthValue());
            membership.setStatus(1);
            membership.setMembershipStartTime(TimeUtil.now());
            membership.setMembershipEndTime(null); // 免费用户无到期时间
            this.save(membership);
        }
        
        return membership;
    }

    /**
     * 获取会员价格
     */
    private BigDecimal getMembershipPrice(String configKey) {
        try {
            SysConfig config = sysConfigService.getByKey(configKey);
            if (config != null && StrUtil.isNotBlank(config.getConfigValue())) {
                return new BigDecimal(config.getConfigValue());
            }
        } catch (Exception e) {
            log.warn("获取价格配置失败：{}", configKey);
        }
        // 默认价格
        if (CONFIG_MONTHLY_PRICE.equals(configKey)) return new BigDecimal("29.90");
        if (CONFIG_YEARLY_PRICE.equals(configKey)) return new BigDecimal("299.00");
        return BigDecimal.ZERO;
    }

    /**
     * 获取会员配额
     */
    private int getMembershipQuota(String configKey) {
        try {
            SysConfig config = sysConfigService.getByKey(configKey);
            if (config != null && StrUtil.isNotBlank(config.getConfigValue())) {
                return Integer.parseInt(config.getConfigValue());
            }
        } catch (Exception e) {
            log.warn("获取配额配置失败：{}", configKey);
        }
        // 默认配额
        if (CONFIG_FREE_QUOTA.equals(configKey)) return 2;
        if (CONFIG_MONTHLY_QUOTA.equals(configKey)) return 30;
        if (CONFIG_YEARLY_QUOTA.equals(configKey)) return 30;
        return 0;
    }

    /**
     * 获取等级名称
     */
    private String getLevelName(int level) {
        switch (level) {
            case LEVEL_MONTHLY: return "月度会员";
            case LEVEL_YEARLY: return "年度会员";
            default: return "免费用户";
        }
    }

    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        return "M" + System.currentTimeMillis() + (int)(Math.random() * 10000);
    }
}
