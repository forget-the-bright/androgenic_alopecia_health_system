# 数据库设计文档

## 表结构设计

### 1. 用户表 (User)

| 字段名 | 类型 | 长度 | 允许空 | 默认值 | 注释 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| user_id | BIGINT | | 否 | | 主键，自增 ID |
| phone | VARCHAR | 20 | 否 | | 手机号（唯一登录凭证） |
| nickname | VARCHAR | 50 | 是 | | 昵称 |
| gender | TINYINT | 1 | 是 | | 性别 (0:未知，1:男，2:女) |
| age | TINYINT | 3 | 是 | | 年龄 |
| register_time | DATETIME | | 否 | CURRENT_TIMESTAMP | 注册时间 |
| initial_grade | VARCHAR | 10 | 是 | | 初始脱发等级 |
| family_history | TINYINT | 1 | 是 | 0 | 家族遗传史 (0:无，1:有) |
| status | TINYINT | 1 | 否 | 1 | 账号状态 (0:禁用，1:正常) |

### 2. 用药方案表 (Treatment_Plan)

| 字段名 | 类型 | 长度 | 允许空 | 默认值 | 注释 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| plan_id | BIGINT | | 否 | | 主键，方案 ID |
| plan_name | VARCHAR | 100 | 否 | | 方案名称 |
| applicable_grade | VARCHAR | 50 | 是 | | 适用脱发等级 |
| drug_list | JSON | | 是 | | 药品列表 (JSON 格式) |
| duration_days | INT | 11 | 是 | | 疗程总天数 |
| description | TEXT | | 是 | | 方案描述 |
| create_time | DATETIME | | 否 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | | 是 | | 更新时间 |

### 3. 患者方案关联表 (User_Plan_Relation)

| 字段名 | 类型 | 长度 | 允许空 | 默认值 | 注释 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| relation_id | BIGINT | | 否 | | 主键 |
| user_id | BIGINT | | 否 | | 外键，关联用户表 |
| plan_id | BIGINT | | 否 | | 外键，关联方案表 |
| start_date | DATE | | 否 | | 疗程开始日期 |
| end_date | DATE | | 是 | | 预计/实际结束日期 |
| current_status | VARCHAR | 20 | 否 | 'active' | 状态 (active/completed/paused) |

### 4. 用药打卡记录表 (Medication_Record)

| 字段名 | 类型 | 长度 | 允许空 | 默认值 | 注释 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| record_id | BIGINT | | 否 | | 主键 |
| user_id | BIGINT | | 否 | | 外键，用户 ID |
| relation_id | BIGINT | | 否 | | 外键，关联的方案关系 ID |
| record_date | DATE | | 否 | | 打卡日期 |
| is_taken | TINYINT | 1 | 否 | 0 | 是否用药 (0:否，1:是) |
| actual_dose | VARCHAR | 50 | 是 | | 实际用药剂量 |
| feedback | VARCHAR | 200 | 是 | | 反馈（如副作用描述） |
| create_time | DATETIME | | 否 | CURRENT_TIMESTAMP | 打卡时间 |

### 5. 图片资源表 (Image_Resource)

| 字段名 | 类型 | 长度 | 允许空 | 默认值 | 注释 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| image_id | BIGINT | | 否 | | 主键 |
| user_id | BIGINT | | 否 | | 外键，所属用户 |
| image_url | VARCHAR | 500 | 否 | | 图片存储路径/URL |
| file_size | INT | 11 | 是 | | 文件大小 (字节) |
| file_type | VARCHAR | 20 | 是 | | MIME 类型 |
| upload_time | DATETIME | | 否 | CURRENT_TIMESTAMP | 上传时间 |
| status | TINYINT | 1 | 否 | 1 | 状态 (0:删除，1:有效) |

### 6. 脱发记录表 (Hair_Loss_Record)

| 字段名 | 类型 | 长度 | 允许空 | 默认值 | 注释 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| record_id | BIGINT | | 否 | | 主键 |
| user_id | BIGINT | | 否 | | 外键，用户 ID |
| record_date | DATE | | 否 | | 记录日期 |
| hair_grade | VARCHAR | 20 | 是 | | 当前脱发等级 |
| notes | TEXT | | 是 | | 备注/医生评语 |
| image_count | INT | 11 | 是 | 0 | 关联图片数量 |
| create_time | DATETIME | | 否 | CURRENT_TIMESTAMP | 创建时间 |

### 7. 记录图片关联表 (Record_Image_Relation)

| 字段名 | 类型 | 长度 | 允许空 | 默认值 | 注释 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| id | BIGINT | | 否 | | 主键 |
| record_id | BIGINT | | 否 | | 外键，脱发记录 ID |
| image_id | BIGINT | | 否 | | 外键，图片 ID |
| shot_type | VARCHAR | 20 | 是 | | 拍摄部位 (top/front/side) |

## ER 关系图

```
User (1) ←→ (N) User_Plan_Relation
Treatment_Plan (1) ←→ (N) User_Plan_Relation
User_Plan_Relation (1) ←→ (N) Medication_Record
User (1) ←→ (N) Hair_Loss_Record
User (1) ←→ (N) Image_Resource
Hair_Loss_Record (1) ←→ (N) Record_Image_Relation
Image_Resource (1) ←→ (N) Record_Image_Relation
```
