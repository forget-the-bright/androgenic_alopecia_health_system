-- 雄激素性脱发患者管理与分析系统 - 会员体系数据库迁移脚本
-- 迁移版本：v1.4.0
-- 迁移内容：会员表、AI 请求日志表、会员购买记录表
-- 执行时间：2026-04-02

USE `hair_loss_system`;

-- =========================== 用户会员表 ===========================
DROP TABLE IF EXISTS `user_membership`;
CREATE TABLE `user_membership` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '会员记录唯一标识',
  `user_id` BIGINT NOT NULL COMMENT '关联用户 ID',
  `membership_level` TINYINT NOT NULL DEFAULT 0 COMMENT '会员等级（0：免费用户，1：月度会员，2：年度会员）',
  `membership_start_time` DATETIME DEFAULT NULL COMMENT '会员开始时间',
  `membership_end_time` DATETIME DEFAULT NULL COMMENT '会员结束时间',
  `monthly_quota` INT NOT NULL DEFAULT 2 COMMENT '每月 AI 分析次数配额',
  `used_count_current_month` INT NOT NULL DEFAULT 0 COMMENT '当月已使用次数',
  `current_month` INT NOT NULL COMMENT '当前月份（用于重置判断）',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态（0：已过期，1：有效期内）',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  KEY `idx_end_time` (`membership_end_time`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户会员表';

-- =========================== AI 分析请求日志表 ===========================
DROP TABLE IF EXISTS `ai_analysis_request_log`;
CREATE TABLE `ai_analysis_request_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '请求日志唯一标识',
  `user_id` BIGINT NOT NULL COMMENT '用户 ID',
  `request_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '请求时间',
  `image_id1` BIGINT NOT NULL COMMENT '对比照片 1 ID',
  `image_id2` BIGINT NOT NULL COMMENT '对比照片 2 ID',
  `analysis_id` BIGINT DEFAULT NULL COMMENT '关联分析记录 ID',
  `token_used` INT DEFAULT 0 COMMENT '消耗 token 数量',
  `request_status` TINYINT NOT NULL DEFAULT 1 COMMENT '请求状态（0：失败，1：成功）',
  `error_message` TEXT DEFAULT NULL COMMENT '错误信息',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_time` (`user_id`, `request_time`),
  KEY `idx_analysis_id` (`analysis_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI 分析请求日志表';

-- =========================== 会员购买记录表 ===========================
DROP TABLE IF EXISTS `membership_purchase_record`;
CREATE TABLE `membership_purchase_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '购买记录唯一标识',
  `user_id` BIGINT NOT NULL COMMENT '用户 ID',
  `order_no` VARCHAR(50) NOT NULL COMMENT '订单号',
  `action_type` TINYINT NOT NULL COMMENT '操作类型（1：新购，2：续费，3：升级）',
  `from_level` TINYINT DEFAULT NULL COMMENT '原会员等级',
  `to_level` TINYINT NOT NULL COMMENT '目标会员等级',
  `original_price` DECIMAL(10,2) NOT NULL COMMENT '原价',
  `discount_amount` DECIMAL(10,2) DEFAULT 0 COMMENT '优惠金额',
  `actual_payment` DECIMAL(10,2) NOT NULL COMMENT '实付金额',
  `membership_days` INT NOT NULL COMMENT '会员天数',
  `old_end_time` DATETIME DEFAULT NULL COMMENT '原到期时间',
  `new_end_time` DATETIME NOT NULL COMMENT '新到期时间',
  `payment_status` TINYINT NOT NULL DEFAULT 1 COMMENT '支付状态（0：未支付，1：已支付）',
  `payment_time` DATETIME DEFAULT NULL COMMENT '支付时间',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_time` (`user_id`, `create_time`),
  KEY `idx_payment_status` (`payment_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员购买记录表';

-- =========================== 初始化数据 ===========================

-- 初始化会员价格配置
INSERT INTO `sys_config` (`config_key`, `config_value`, `config_type`, `remark`) VALUES
('membership.monthly.price', '29.90', 'membership', '月度会员价格（元）'),
('membership.yearly.price', '299.00', 'membership', '年度会员价格（元）'),
('membership.monthly.quota', '30', 'membership', '月度会员每月 AI 分析次数'),
('membership.yearly.quota', '30', 'membership', '年度会员每月 AI 分析次数'),
('membership.free.quota', '2', 'membership', '免费用户每月 AI 分析次数');

-- 添加说明
SELECT '会员体系数据库迁移完成！' AS '迁移状态';
SELECT '已创建表：user_membership（会员表）' AS '表 1';
SELECT '已创建表：ai_analysis_request_log（AI 请求日志表）' AS '表 2';
SELECT '已创建表：membership_purchase_record（会员购买记录表）' AS '表 3';
