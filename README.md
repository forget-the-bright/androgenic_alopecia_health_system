# 雄激素性脱发患者管理与分析系统

## 项目简介

本系统针对男性雄激素性脱发患者，提供**毛发数据记录、AI 智能分析、用药管理、用户权限管控**一体化服务，实现患者脱发情况可视化追踪、规范化管理。

## 技术栈

| 层级 | 技术选型 |
| ---- | ---- |
| 后端框架 | Spring Boot 2.7.18 |
| 权限安全 | Sa-Token 1.37.0 + Redis |
| ORM 框架 | MyBatis-Plus 3.5.3.1 |
| 前端架构 | Thymeleaf + Bootstrap + jQuery |
| 接口文档 | Knife4j 3.0.3 (Swagger) |
| 文件存储 | MinIO 8.4.3 |
| 数据库 | MySQL 5.7+ (utf8mb4) |
| 工具类 | Hutool 5.8.16 |
| 配置加密 | Jasypt 3.0.4 |

## 功能模块

### 普通用户功能
- **头发数据管理**：照片上传（支持 JPG/PNG，最大 10MB）、数据展示、数据管理
- **AI 对比分析**：选择两张历史照片发起 AI 对比分析，生成分析报告（评分、趋势、详情）
- **用药管理**：用药方案自定义（药物名称、剂量、服用时间、周期）、每日用药打卡
- **个人资料**：个人信息管理、密码修改、语言偏好设置（中文/英文）

### 管理员功能
- **用户管理**：用户列表、用户详情、禁用/启用账号、重置密码
- **数据统计**：全局统计数据查看
- **系统配置**：MinIO 配置、AI 接口配置、系统参数配置
- **操作日志**：系统操作日志查看（支持按用户、操作类型、时间筛选）

## 快速开始

### 环境要求
- JDK 1.8+
- MySQL 5.7+（需支持 utf8mb4 字符集）
- Maven 3.6+
- Redis（用于 Sa-Token 会话管理）
- MinIO（可选，用于文件存储）

### 数据库初始化

1. 创建数据库并执行初始化脚本：
```bash
mysql -u root -p < src/main/resources/db/init.sql
```

2. 或手动执行 `src/main/resources/db/init.sql` 中的 SQL 语句

初始化后会创建以下 7 张表：
- `sys_user` - 系统用户表
- `user_hair_image` - 毛发照片表
- `ai_analysis` - AI 分析记录表
- `user_medicine` - 用药方案表
- `medicine_clock` - 用药打卡表
- `sys_config` - 系统配置表
- `sys_operation_log` - 系统操作日志表

### 配置修改

修改 `src/main/resources/application.yml` 中的配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/hair_loss_system?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: your_password
  
  redis:
    host: localhost
    port: 6379
    password: ''  # 如有密码请填写
    database: 0
    timeout: 10s

minio:
  endpoint: http://localhost:9000
  access-key: minioadmin
  secret-key: minioadmin
  bucket-name: hairloss

ai:
  analysis:
    url: https://api.example.com/ai/hair-analysis
    key: your-api-key
```

### 启动项目
jasypt.encryptor.password=your_password
your_password 是需要替换的 配置文件加密密码。
**方式一：Maven 启动**
```bash
mvn clean install
mvn spring-boot:run "-Dspring-boot.run.arguments=--jasypt.encryptor.password=your_password"
```

**方式二：IDE 直接运行**
运行 `src/main/java/com/hairloss/system/HairLossSystemApplication.java` 中的 main 方法 但是要追加启动参数  --jasypt.encryptor.password=your_password

**方式三：JAR 包启动**
```bash
mvn clean package
java -jar target/androgenic-alopecia-health-system-1.0.0.jar --jasypt.encryptor.password=your_password
```

启动成功后控制台输出：
```
========================================
雄激素性脱发患者管理与分析系统启动成功！
接口文档地址：http://localhost:8080/doc.html
========================================
```

### Jasypt 配置加密（可选）

如需要对敏感配置（如数据库密码、MinIO 密钥）进行加密：

```bash
# 加密配置
mvn jasypt:encrypt -Djasypt.encryptor.password=your_password

# 解密配置
mvn jasypt:decrypt -Djasypt.encryptor.password=your_password

# 启动时指定密码
 mvn spring-boot:run "-Dspring-boot.run.arguments=--jasypt.encryptor.password=your_password"
# 或
java -jar target/androgenic-alopecia-health-system-1.0.0.jar --jasypt.encryptor.password=your_password
```

### 访问系统

- 系统首页：http://localhost:8080/
- 登录页面：http://localhost:8080/login.html
- 接口文档：http://localhost:8080/doc.html

### 默认账号

| 用户名 | 密码 | 角色 |
| ------ | ---- | ---- |
| admin | admin123 | 管理员 |

> 注意：默认管理员密码为 BCrypt 加密存储，首次登录后建议修改密码。

## 项目结构

```
src/main/java/com/hairloss/system/
├── HairLossSystemApplication.java    # 启动类
├── common/                           # 公共类
│   ├── Result.java                   # 统一返回结果
│   └── GlobalExceptionHandler.java   # 全局异常处理
├── config/                           # 配置类
│   ├── WebConfig.java                # Web 配置
│   ├── MybatisPlusConfig.java        # MyBatis-Plus 配置
│   ├── MinioConfig.java              # MinIO 配置
│   ├── SwaggerConfig.java            # Swagger 配置
│   └── I18nConfig.java               # 国际化配置
├── controller/                       # 控制器
│   ├── SysUserController.java        # 用户管理
│   ├── UserHairImageController.java  # 毛发照片管理
│   ├── AiAnalysisController.java     # AI 分析管理
│   ├── UserMedicineController.java   # 用药方案管理
│   ├── MedicineClockController.java  # 用药打卡管理
│   ├── AdminController.java          # 管理员功能
│   └── IndexController.java          # 首页
├── entity/                           # 实体类
│   ├── SysUser.java                  # 系统用户
│   ├── UserHairImage.java            # 毛发照片
│   ├── AiAnalysis.java               # AI 分析记录
│   ├── UserMedicine.java             # 用药方案
│   ├── MedicineClock.java            # 用药打卡
│   ├── SysConfig.java                # 系统配置
│   └── SysOperationLog.java          # 操作日志
├── mapper/                           # Mapper 接口
├── service/                          # Service 接口
│   └── impl/                         # Service 实现类
└── interceptor/                      # 拦截器
```

## 数据库表结构

| 表名 | 说明 |
| ---- | ---- |
| sys_user | 系统用户表（含登录账号、角色、状态等） |
| user_hair_image | 毛发照片表（存储照片 URL、拍摄部位、上传时间等） |
| ai_analysis | AI 分析记录表（存储对比分析结果、评分、趋势等） |
| user_medicine | 用药方案表（存储药物名称、剂量、周期等） |
| medicine_clock | 用药打卡表（存储每日打卡记录） |
| sys_config | 系统配置表（存储 MinIO、AI 接口等配置） |
| sys_operation_log | 系统操作日志表（存储用户操作记录） |

## API 接口

### 用户相关
- `POST /api/login` - 用户登录
- `POST /api/logout` - 退出登录
- `POST /api/register` - 用户注册
- `GET /api/user/info` - 获取当前用户信息
- `POST /api/user/change-password` - 修改密码

### 毛发照片相关
- `POST /api/image/upload` - 上传照片
- `GET /api/image/list` - 获取照片列表
- `DELETE /api/image/{id}` - 删除照片
- `POST /api/image/batch-delete` - 批量删除

### AI 分析相关
- `POST /api/analysis/analyze` - 发起分析
- `GET /api/analysis/list` - 获取分析记录
- `GET /api/analysis/{id}` - 获取分析详情

### 用药管理相关
- `POST /api/medicine/add` - 添加用药方案
- `GET /api/medicine/list` - 获取用药方案列表
- `DELETE /api/medicine/{id}` - 删除用药方案
- `POST /api/clock/in` - 打卡
- `GET /api/clock/records` - 获取打卡记录

### 管理员接口
- `GET /api/admin/user/list` - 用户列表
- `POST /api/admin/user/status` - 更新用户状态
- `POST /api/admin/user/reset-password` - 重置密码
- `GET /api/admin/config/list` - 系统配置列表
- `GET /api/admin/log/list` - 操作日志列表

## 注意事项

1. **MinIO 配置**：如不使用 MinIO，可修改 `UserHairImageServiceImpl` 使用本地文件存储
2. **AI 接口**：当前 AI 分析为模拟实现，需替换为真实 AI 接口
3. **密码加密**：使用 BCrypt 加密，默认管理员密码为 `admin123`
4. **会话超时**：默认会话超时时间为 7200 秒（2 小时）
5. **Redis 配置**：Sa-Token 需要 Redis 存储会话，请确保 Redis 服务正常运行
6. **文件上传限制**：默认最大上传文件大小为 10MB，可在 `application.yml` 中修改

## 开发计划

- [ ] 集成真实 AI 分析接口
- [ ] 增加数据导出功能（Excel/PDF）
- [ ] 增加消息通知功能（邮件/短信）
- [ ] 移动端适配优化
- [ ] 增加数据报表功能
- [ ] 支持多语言国际化

## 许可证

MIT License

## 联系方式

如有问题，请提交 Issue 或联系开发团队。
