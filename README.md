# 雄激素性脱发患者管理与分析系统

## 项目简介

本系统针对男性雄激素性脱发患者，提供**毛发数据记录、AI 智能分析、用药管理、用户权限管控**一体化服务，实现患者脱发情况可视化追踪、规范化管理。

## 技术栈

| 层级 | 技术选型 |
| ---- | ---- |
| 后端框架 | Spring Boot 2.7.18 |
| 权限安全 | Sa-Token 1.37.0 |
| ORM 框架 | MyBatis-Plus 3.5.3.1 |
| 前端架构 | Thymeleaf + Bootstrap + jQuery |
| 接口文档 | Knife4j 3.0.3 |
| 文件存储 | MinIO 8.4.3 |
| 数据库 | MySQL 5.7+ |

## 功能模块

### 普通用户功能
- **头发数据管理**：照片上传、数据展示、数据管理
- **AI 对比分析**：选择两张历史照片发起 AI 对比分析
- **用药管理**：用药方案自定义、每日用药打卡
- **个人资料**：个人信息管理、密码修改、语言偏好设置

### 管理员功能
- **用户管理**：用户列表、用户详情、禁用/启用账号、重置密码
- **数据统计**：全局统计数据查看
- **系统配置**：MinIO 配置、AI 接口配置、系统参数配置
- **操作日志**：系统操作日志查看

## 快速开始

### 环境要求
- JDK 1.8+
- MySQL 5.7+
- Maven 3.6+
- MinIO（可选，用于文件存储）

### 数据库初始化

1. 创建数据库并执行初始化脚本：
```bash
mysql -u root -p < src/main/resources/db/init.sql
```

2. 或手动执行 `src/main/resources/db/init.sql` 中的 SQL 语句

### 配置修改

修改 `src/main/resources/application.yml` 中的配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/hair_loss_system?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: your_password

minio:
  endpoint: http://localhost:9000
  access-key: minioadmin
  secret-key: minioadmin
```

### 启动项目

```bash
mvn clean install
mvn spring-boot:run
```

或直接运行 `HairLossSystemApplication.java`

### 访问系统

- 系统首页：http://localhost:8080/
- 登录页面：http://localhost:8080/login.html
- 接口文档：http://localhost:8080/doc.html

### 默认账号

| 用户名 | 密码 | 角色 |
| ------ | ---- | ---- |
| admin | admin123 | 管理员 |

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
| sys_user | 系统用户表 |
| user_hair_image | 毛发照片表 |
| ai_analysis | AI 分析记录表 |
| user_medicine | 用药方案表 |
| medicine_clock | 用药打卡表 |
| sys_config | 系统配置表 |
| sys_operation_log | 系统操作日志表 |

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

## 开发计划

- [ ] 集成真实 AI 分析接口
- [ ] 增加数据导出功能
- [ ] 增加消息通知功能
- [ ] 移动端适配优化
- [ ] 增加数据报表功能

## 许可证

MIT License

## 联系方式

如有问题，请提交 Issue 或联系开发团队。
