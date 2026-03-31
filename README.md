# 雄脱健康管理系统 (Androgenetic Alopecia Health Management System)

## 项目简介

本系统聚焦于雄激素性脱发（雄脱）患者的全周期健康管理，围绕患者自我管理、用药跟踪、影像记录与效果分析四大核心场景构建。

## 技术栈

- **后端框架**: Spring Boot 3.2.0
- **权限认证**: Sa-Token 1.37.0
- **ORM 框架**: MyBatis-Plus 3.5.5
- **数据库**: MySQL 8.0
- **连接池**: Druid 1.2.20
- **对象存储**: MinIO 8.5.7
- **缓存**: Redis
- **构建工具**: Maven

## 项目结构

```
androgenetic-alopecia-system/
├── aha-common/          # 公共模块（统一返回、异常处理、配置类）
├── aha-dao/             # 数据访问层（Entity、Mapper）
├── aha-service/         # 业务逻辑层（Service、DTO、VO）
├── aha-controller/      # 控制器层（RESTful API）
└── aha-admin/           # 管理端启动模块
```

## 核心功能模块

### 1. 患者管理领域
- 患者注册
- 个人信息维护
- 健康档案建立（脱发史、家族史）

### 2. 用药管理领域
- 用药方案制定（管理员配置）
- 用药提醒
- 用药打卡
- 用药记录查询

### 3. 图片记录管理领域
- 图片上传（按部位/时间分类）
- 图片存储（MinIO）
- 图片对比查看

### 4. 分析功能领域
- 用药依从性分析
- 脱发等级趋势分析
- 效果对比报告生成

## 快速开始

### 环境要求
- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Redis 6.0+
- MinIO

### 配置说明

1. 复制配置文件模板：
```bash
cp aha-admin/src/main/resources/application.yml aha-admin/src/main/resources/application-local.yml
```

2. 修改 `application-local.yml` 中的数据库、Redis、MinIO 配置

3. 初始化数据库：
```bash
mysql -u root -p < docs/schema.sql
```

4. 启动项目：
```bash
mvn clean install
cd aha-admin
mvn spring-boot:run
```

## 数据库设计

详细数据库设计请参考 [数据库设计文档](docs/database-design.md)

### 核心表结构
- `User`: 用户表
- `Treatment_Plan`: 用药方案表
- `User_Plan_Relation`: 患者方案关联表
- `Medication_Record`: 用药打卡记录表
- `Image_Resource`: 图片资源表
- `Hair_Loss_Record`: 脱发记录表
- `Record_Image_Relation`: 记录图片关联表

## API 文档

项目启动后访问：http://localhost:8080/doc.html

## 许可证

MIT License
