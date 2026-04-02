-- 雄激素性脱发患者管理与分析系统 - 数据库迁移脚本
-- 迁移版本：v1.1.0
-- 迁移内容：优化 AI 分析记录表，增加结构化字段和图片信息
-- 执行时间：2026-04-02

USE `hair_loss_system`;

-- =========================== 修改 AI 分析记录表 ===========================
-- 增加结构化字段和图片信息字段

ALTER TABLE `ai_analysis` 
    ADD COLUMN `hair_density_score` INT DEFAULT NULL COMMENT '毛发密度评分（0-100）' AFTER `score`,
    ADD COLUMN `hair_loss_improve_score` INT DEFAULT NULL COMMENT '脱发改善评分（0-100）' AFTER `hair_density_score`,
    ADD COLUMN `trend_description` TEXT DEFAULT NULL COMMENT '趋势详细描述' AFTER `trend`,
    ADD COLUMN `compare_result` TEXT DEFAULT NULL COMMENT '对比分析结果' AFTER `trend_description`,
    ADD COLUMN `key_changes` TEXT DEFAULT NULL COMMENT '关键变化点' AFTER `compare_result`,
    ADD COLUMN `treatment_suggestion` TEXT DEFAULT NULL COMMENT '治疗建议' AFTER `key_changes`,
    ADD COLUMN `daily_care` TEXT DEFAULT NULL COMMENT '日常护理建议' AFTER `treatment_suggestion`,
    ADD COLUMN `next_step` TEXT DEFAULT NULL COMMENT '下一步建议' AFTER `daily_care`,
    ADD COLUMN `conclusion` VARCHAR(500) DEFAULT NULL COMMENT '总体结论' AFTER `next_step`,
    ADD COLUMN `analysis_part` VARCHAR(20) DEFAULT NULL COMMENT '分析部位' AFTER `conclusion`,
    ADD COLUMN `time_interval` VARCHAR(50) DEFAULT NULL COMMENT '时间间隔描述' AFTER `analysis_part`,
    ADD COLUMN `image_url_1` VARCHAR(500) DEFAULT NULL COMMENT '图片 1 URL' AFTER `time_interval`,
    ADD COLUMN `image_url_2` VARCHAR(500) DEFAULT NULL COMMENT '图片 2 URL' AFTER `image_url_1`,
    ADD COLUMN `image_part_1` VARCHAR(20) DEFAULT NULL COMMENT '图片 1 部位' AFTER `image_url_2`,
    ADD COLUMN `image_part_2` VARCHAR(20) DEFAULT NULL COMMENT '图片 2 部位' AFTER `image_part_1`,
    ADD COLUMN `image_time_1` DATETIME DEFAULT NULL COMMENT '图片 1 时间' AFTER `image_part_2`,
    ADD COLUMN `image_time_2` DATETIME DEFAULT NULL COMMENT '图片 2 时间' AFTER `image_time_1`;

-- 添加索引优化查询性能
ALTER TABLE `ai_analysis` 
    ADD INDEX `idx_trend` (`trend`),
    ADD INDEX `idx_score` (`score`),
    ADD INDEX `idx_analysis_time` (`analysis_time`);

-- 修改 report_content 字段为 MEDIUMTEXT，支持更长的 JSON 内容
ALTER TABLE `ai_analysis` 
    MODIFY COLUMN `report_content` MEDIUMTEXT NOT NULL COMMENT 'AI 分析报告完整 JSON（原始数据）';

-- 添加说明
SELECT '数据库迁移完成！AI 分析记录表已优化，增加以下字段：' AS '迁移状态';
SELECT '- hair_density_score: 毛发密度评分' AS '新增字段 1';
SELECT '- hair_loss_improve_score: 脱发改善评分' AS '新增字段 2';
SELECT '- trend_description: 趋势详细描述' AS '新增字段 3';
SELECT '- compare_result: 对比分析结果' AS '新增字段 4';
SELECT '- key_changes: 关键变化点' AS '新增字段 5';
SELECT '- treatment_suggestion: 治疗建议' AS '新增字段 6';
SELECT '- daily_care: 日常护理建议' AS '新增字段 7';
SELECT '- next_step: 下一步建议' AS '新增字段 8';
SELECT '- conclusion: 总体结论' AS '新增字段 9';
SELECT '- analysis_part: 分析部位' AS '新增字段 10';
SELECT '- time_interval: 时间间隔描述' AS '新增字段 11';
SELECT '- image_url_1: 图片 1 URL' AS '新增字段 12';
SELECT '- image_url_2: 图片 2 URL' AS '新增字段 13';
SELECT '- image_part_1: 图片 1 部位' AS '新增字段 14';
SELECT '- image_part_2: 图片 2 部位' AS '新增字段 15';
SELECT '- image_time_1: 图片 1 时间' AS '新增字段 16';
SELECT '- image_time_2: 图片 2 时间' AS '新增字段 17';
