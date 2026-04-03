package com.hairloss.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hairloss.system.entity.UserMembership;
import com.hairloss.system.entity.MembershipPurchaseRecord;

import java.util.Map;

/**
 * 用户会员服务接口
 */
public interface UserMembershipService extends IService<UserMembership> {

    /**
     * 获取用户会员信息（自动检查过期和重置）
     * @param userId 用户 ID
     * @return 会员信息
     */
    UserMembership getMembershipInfo(Long userId);

    /**
     * 购买会员
     * @param userId 用户 ID
     * @param membershipLevel 会员等级（1：月度，2：年度）
     * @return 购买结果
     */
    Map<String, Object> purchaseMembership(Long userId, Integer membershipLevel);

    /**
     * 升级会员（补差价）
     * @param userId 用户 ID
     * @return 升级结果（包含差价信息）
     */
    Map<String, Object> upgradeMembership(Long userId);

    /**
     * 获取升级信息（不执行升级，只计算差价）
     * @param userId 用户 ID
     * @return 升级信息（包含差价、剩余天数等）
     */
    Map<String, Object> getUpgradeInfo(Long userId);

    /**
     * 检查并重置月度次数（自然月重置）
     * @param membership 会员信息
     */
    void checkAndResetMonthlyCount(UserMembership membership);

    /**
     * 检查会员是否过期，过期则降级
     * @param membership 会员信息
     * @return 是否过期
     */
    boolean checkMembershipExpired(UserMembership membership);

    /**
     * 增加已使用次数
     * @param userId 用户 ID
     */
    void incrementUsedCount(Long userId);

    /**
     * 检查是否可以使用 AI 分析
     * @param userId 用户 ID
     * @return 检查结果（包含是否可用、原因等）
     */
    Map<String, Object> checkAnalysisPermission(Long userId);
}
