-- 雄激素性脱发患者管理与分析系统 - 数据库初始化脚本
-- 数据库版本：MySQL 5.7+
-- 字符集：UTF8mb4

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `hair_loss_system` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `hair_loss_system`;

-- =========================== 系统用户表 ===========================
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户唯一标识',
  `username` VARCHAR(50) NOT NULL COMMENT '登录账号（唯一）',
  `password` VARCHAR(100) NOT NULL COMMENT '加密密码',
  `real_name` VARCHAR(20) DEFAULT NULL COMMENT '真实姓名',
  `phone` VARCHAR(11) DEFAULT NULL COMMENT '手机号码',
  `email` VARCHAR(50) DEFAULT NULL COMMENT '电子邮箱',
  `role` TINYINT NOT NULL DEFAULT 0 COMMENT '角色（0：普通用户，1：系统管理员）',
  `language` VARCHAR(10) NOT NULL DEFAULT 'zh-CN' COMMENT '语言偏好（zh-CN：中文，en-US：英文）',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '账号状态（0：禁用，1：启用）',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_role` (`role`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- =========================== 毛发照片表 ===========================
DROP TABLE IF EXISTS `user_hair_image`;
CREATE TABLE `user_hair_image` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '照片唯一标识',
  `user_id` BIGINT NOT NULL COMMENT '关联用户 ID',
  `part` VARCHAR(20) NOT NULL COMMENT '拍摄部位（头顶/前额/两侧等）',
  `file_url` VARCHAR(255) NOT NULL COMMENT 'MinIO 文件访问 URL',
  `minio_path` VARCHAR(255) NOT NULL COMMENT 'MinIO 文件存储路径',
  `file_name` VARCHAR(100) NOT NULL COMMENT '文件名（含后缀）',
  `file_size` BIGINT NOT NULL COMMENT '文件大小（字节）',
  `file_type` VARCHAR(10) NOT NULL COMMENT '文件类型（jpg/png）',
  `upload_date` DATE NOT NULL DEFAULT CURRENT_DATE COMMENT '上传日期',
  `upload_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  `remark` VARCHAR(200) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_upload_part` (`user_id`,`upload_date`,`part`),
  KEY `idx_upload_date` (`upload_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='毛发照片表';

-- =========================== AI 分析记录表 ===========================
DROP TABLE IF EXISTS `ai_analysis`;
CREATE TABLE `ai_analysis` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分析记录唯一标识',
  `user_id` BIGINT NOT NULL COMMENT '关联用户 ID',
  `image_id1` BIGINT NOT NULL COMMENT '对比照片 1 ID',
  `image_id2` BIGINT NOT NULL COMMENT '对比照片 2 ID',
  `score` DECIMAL(5,2) NOT NULL COMMENT 'AI 分析评分（0-100）',
  `trend` VARCHAR(20) NOT NULL COMMENT '变化趋势（改善/稳定/恶化）',
  `report_content` TEXT NOT NULL COMMENT 'AI 分析报告详情',
  `analysis_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '分析完成时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_analysis_time` (`user_id`,`analysis_time`),
  KEY `idx_image1` (`image_id1`),
  KEY `idx_image2` (`image_id2`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI 分析记录表';

-- =========================== 用药方案表 ===========================
DROP TABLE IF EXISTS `user_medicine`;
CREATE TABLE `user_medicine` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用药方案唯一标识',
  `user_id` BIGINT NOT NULL COMMENT '关联用户 ID',
  `medicine_name` VARCHAR(50) NOT NULL COMMENT '药物名称',
  `dosage` VARCHAR(20) NOT NULL COMMENT '用药剂量',
  `take_time` VARCHAR(50) NOT NULL COMMENT '服用时间',
  `cycle` INT NOT NULL COMMENT '用药周期（天）',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '方案状态（0：停用，1：启用）',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` VARCHAR(200) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_user_status` (`user_id`,`status`),
  KEY `idx_medicine_name` (`medicine_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用药方案表';

-- =========================== 用药打卡表 ===========================
DROP TABLE IF EXISTS `medicine_clock`;
CREATE TABLE `medicine_clock` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '打卡记录唯一标识',
  `user_id` BIGINT NOT NULL COMMENT '关联用户 ID',
  `medicine_id` BIGINT NOT NULL COMMENT '关联用药方案 ID',
  `clock_date` DATE NOT NULL DEFAULT CURRENT_DATE COMMENT '打卡日期',
  `clock_status` TINYINT NOT NULL COMMENT '打卡状态（0：漏服，1：已服，2：补卡）',
  `clock_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '打卡时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_medicine_date` (`user_id`,`medicine_id`,`clock_date`),
  KEY `idx_clock_status` (`clock_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用药打卡表';

-- =========================== 系统配置表 ===========================
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '配置项唯一标识',
  `config_key` VARCHAR(50) NOT NULL COMMENT '配置键（唯一）',
  `config_value` TEXT NOT NULL COMMENT '配置值',
  `config_type` VARCHAR(20) NOT NULL COMMENT '配置类型（minio/ai/system）',
  `remark` VARCHAR(200) DEFAULT NULL COMMENT '配置说明',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`),
  KEY `idx_config_type` (`config_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- =========================== 系统操作日志表 ===========================
DROP TABLE IF EXISTS `sys_operation_log`;
CREATE TABLE `sys_operation_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志唯一标识',
  `user_id` BIGINT NOT NULL COMMENT '操作人 ID（0：系统）',
  `operation_type` VARCHAR(20) NOT NULL COMMENT '操作类型（登录/上传/分析/配置等）',
  `operation_desc` VARCHAR(200) NOT NULL COMMENT '操作描述',
  `request_url` VARCHAR(255) NOT NULL COMMENT '请求接口地址',
  `request_param` TEXT DEFAULT NULL COMMENT '请求参数',
  `ip_address` VARCHAR(50) NOT NULL COMMENT '操作 IP 地址',
  `operation_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '操作状态（0：失败，1：成功）',
  `error_msg` TEXT DEFAULT NULL COMMENT '错误信息（失败时记录）',
  PRIMARY KEY (`id`),
  KEY `idx_user_operation_time` (`user_id`,`operation_time`),
  KEY `idx_operation_type` (`operation_type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统操作日志表';

-- =========================== 初始化数据 ===========================

-- 初始化系统管理员账号（密码：admin123，BCrypt 加密）
INSERT INTO `sys_user` (`username`, `password`, `real_name`, `role`, `status`, `language`)
VALUES ('admin', '$2a$10$7JBqZpJNl7W5q5q5q5q5q5q5q5q5q5q5q5q5q5q5q5q5q5q5q5q', '系统管理员', 1, 1, 'zh-CN');

-- 初始化系统基础配置
INSERT INTO `sys_config` (`config_key`, `config_value`, `config_type`, `remark`) VALUES
('minio.bucket.name', 'hair-loss-images', 'minio', 'MinIO 存储照片的 Bucket 名称'),
('ai.analysis.url', 'https://api.example.com/ai/hair-analysis', 'ai', 'AI 分析接口地址'),
('system.login.timeout', '7200', 'system', '登录会话超时时间（秒）');
