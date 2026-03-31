-- 雄脱健康管理系统数据库初始化脚本
-- MySQL 8.0+

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 1. 用户表
CREATE TABLE IF NOT EXISTS `User` (
  `user_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户 ID',
  `phone` VARCHAR(20) NOT NULL COMMENT '手机号',
  `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
  `gender` TINYINT DEFAULT NULL COMMENT '性别 (0:未知，1:男，2:女)',
  `age` TINYINT DEFAULT NULL COMMENT '年龄',
  `register_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  `initial_grade` VARCHAR(10) DEFAULT NULL COMMENT '初始脱发等级',
  `family_history` TINYINT DEFAULT '0' COMMENT '家族史 (0:无，1:有)',
  `status` TINYINT NOT NULL DEFAULT '1' COMMENT '状态 (0:禁用，1:正常)',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `IDX_PHONE` (`phone`),
  KEY `IDX_REGISTER` (`register_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 2. 用药方案表
CREATE TABLE IF NOT EXISTS `Treatment_Plan` (
  `plan_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '方案 ID',
  `plan_name` VARCHAR(100) NOT NULL COMMENT '方案名称',
  `applicable_grade` VARCHAR(50) DEFAULT NULL COMMENT '适用等级',
  `drug_list` JSON DEFAULT NULL COMMENT '药品列表 JSON',
  `duration_days` INT DEFAULT NULL COMMENT '疗程天数',
  `description` TEXT COMMENT '描述',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`plan_id`),
  KEY `IDX_GRADE` (`applicable_grade`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='治疗方案表';

-- 3. 患者方案关联表
CREATE TABLE IF NOT EXISTS `User_Plan_Relation` (
  `relation_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关系 ID',
  `user_id` BIGINT NOT NULL,
  `plan_id` BIGINT NOT NULL,
  `start_date` DATE NOT NULL,
  `end_date` DATE DEFAULT NULL,
  `current_status` VARCHAR(20) NOT NULL DEFAULT 'active',
  PRIMARY KEY (`relation_id`),
  KEY `IDX_USER_STATUS` (`user_id`,`current_status`),
  CONSTRAINT `FK_USER_PLAN_USER` FOREIGN KEY (`user_id`) REFERENCES `User` (`user_id`),
  CONSTRAINT `FK_USER_PLAN_PLAN` FOREIGN KEY (`plan_id`) REFERENCES `Treatment_Plan` (`plan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户方案关联表';

-- 4. 用药打卡记录表
CREATE TABLE IF NOT EXISTS `Medication_Record` (
  `record_id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `relation_id` BIGINT NOT NULL,
  `record_date` DATE NOT NULL,
  `is_taken` TINYINT NOT NULL DEFAULT '0',
  `actual_dose` VARCHAR(50) DEFAULT NULL,
  `feedback` VARCHAR(200) DEFAULT NULL,
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`record_id`),
  KEY `IDX_USER_DATE` (`user_id`,`record_date`),
  KEY `IDX_RELATION` (`relation_id`),
  CONSTRAINT `FK_MED_USER` FOREIGN KEY (`user_id`) REFERENCES `User` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用药记录表';

-- 5. 图片资源表
CREATE TABLE IF NOT EXISTS `Image_Resource` (
  `image_id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `image_url` VARCHAR(500) NOT NULL,
  `file_size` INT DEFAULT NULL,
  `file_type` VARCHAR(20) DEFAULT NULL,
  `upload_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` TINYINT NOT NULL DEFAULT '1',
  PRIMARY KEY (`image_id`),
  KEY `IDX_USER_UPLOAD` (`user_id`,`upload_time`),
  CONSTRAINT `FK_IMG_USER` FOREIGN KEY (`user_id`) REFERENCES `User` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图片资源表';

-- 6. 脱发记录表
CREATE TABLE IF NOT EXISTS `Hair_Loss_Record` (
  `record_id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `record_date` DATE NOT NULL,
  `hair_grade` VARCHAR(20) DEFAULT NULL COMMENT '脱发等级',
  `notes` TEXT,
  `image_count` INT DEFAULT '0',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`record_id`),
  KEY `IDX_USER_RECORD` (`user_id`,`record_date`),
  CONSTRAINT `FK_HAIR_USER` FOREIGN KEY (`user_id`) REFERENCES `User` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='脱发记录表';

-- 7. 记录图片关联表
CREATE TABLE IF NOT EXISTS `Record_Image_Relation` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `record_id` BIGINT NOT NULL,
  `image_id` BIGINT NOT NULL,
  `shot_type` VARCHAR(20) DEFAULT NULL COMMENT '拍摄部位 (top/front/side)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_RECORD_IMG` (`record_id`,`image_id`),
  KEY `IDX_IMAGE` (`image_id`),
  CONSTRAINT `FK_REL_RECORD` FOREIGN KEY (`record_id`) REFERENCES `Hair_Loss_Record` (`record_id`),
  CONSTRAINT `FK_REL_IMAGE` FOREIGN KEY (`image_id`) REFERENCES `Image_Resource` (`image_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='记录图片关联表';

SET FOREIGN_KEY_CHECKS = 1;
