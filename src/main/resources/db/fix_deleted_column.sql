-- 修复打卡表结构 - 移除逻辑删除字段
-- 执行此脚本将 medicine_clock 表的 deleted 字段移除

USE hair_loss_system;

-- 检查并删除 deleted 字段
ALTER TABLE medicine_clock DROP COLUMN IF EXISTS `deleted`;

-- 同样修复其他表的逻辑删除字段（如果需要）
-- ALTER TABLE sys_user DROP COLUMN IF EXISTS `deleted`;
-- ALTER TABLE user_hair_image DROP COLUMN IF EXISTS `deleted`;
-- ALTER TABLE ai_analysis DROP COLUMN IF EXISTS `deleted`;
-- ALTER TABLE user_medicine DROP COLUMN IF EXISTS `deleted`;
-- ALTER TABLE sys_config DROP COLUMN IF EXISTS `deleted`;
-- ALTER TABLE sys_operation_log DROP COLUMN IF EXISTS `deleted`;
