# 雄脱健康管理系统 - Spring Boot 项目骨架

## 项目结构

```
androgenetic-alopecia-system/          # 项目根目录
├── pom.xml                            # 父 POM（Maven 多模块管理）
├── aha-common/                        # 公共模块
│   ├── pom.xml
│   └── src/main/java/com/aha/common/
│       ├── config/                    # 配置类
│       │   ├── SaTokenConfig.java     # Sa-Token 认证配置
│       │   ├── MybatisPlusConfig.java # MyBatis-Plus 配置
│       │   └── MinioConfig.java       # MinIO 对象存储配置
│       ├── exception/                 # 异常处理
│       │   ├── BusinessException.java # 业务异常类
│       │   └── GlobalExceptionHandler.java # 全局异常处理器
│       ├── result/                    # 统一返回结果
│       │   ├── Result.java            # 统一返回结果类
│       │   └── ResultCode.java        # 状态码枚举
│       └── utils/                     # 工具类
├── aha-dao/                           # 数据访问层
│   ├── pom.xml
│   └── src/main/java/com/aha/dao/
│       ├── entity/                    # 实体类（对应数据库表）
│       └── mapper/                    # Mapper 接口
├── aha-service/                       # 业务逻辑层
│   ├── pom.xml
│   └── src/main/java/com/aha/service/
│       ├── dto/                       # 数据传输对象
│       ├── vo/                        # 视图对象
│       └── impl/                      # 服务实现类
├── aha-controller/                    # 控制器层
│   ├── pom.xml
│   └── src/main/java/com/aha/controller/
│       ├── admin/                     # 管理员接口
│       └── patient/                   # 患者接口
└── aha-admin/                         # 管理端启动模块
    ├── pom.xml
    └── src/main/
        ├── java/com/aha/admin/
        │   └── AhaAdminApplication.java # 启动类
        └── resources/
            └── application.yml        # 配置文件
```

## 技术栈

- **Spring Boot 3.2.0** - 核心框架
- **Sa-Token 1.37.0** - 权限认证框架
- **MyBatis-Plus 3.5.5** - ORM 框架
- **MySQL 8.0** - 关系型数据库
- **MinIO 8.5.7** - 对象存储（图片存储）
- **Druid 1.2.20** - 数据库连接池
- **Hutool 5.8.23** - Java 工具库
- **Redis** - 缓存/Sa-Token Token 存储

## 模块说明

### 1. aha-common（公共模块）
- 提供统一返回结果封装（`Result<T>`）
- 全局异常处理（`GlobalExceptionHandler`）
- 业务异常类（`BusinessException`）
- 通用配置类（Sa-Token、MyBatis-Plus、MinIO）

### 2. aha-dao（数据访问层）
- 数据库实体类（Entity）
- MyBatis Mapper 接口
- MyBatis XML 映射文件

### 3. aha-service（业务逻辑层）
- 业务逻辑实现
- DTO（Data Transfer Object）
- VO（View Object）

### 4. aha-controller（控制器层）
- RESTful API 接口
- 按角色分模块：admin（管理员）、patient（患者）

### 5. aha-admin（管理端启动模块）
- 应用启动入口
- 主配置文件（application.yml）
- 依赖整合（Controller + Service + DAO）

## 快速开始

### 1. 环境要求
- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+
- MinIO（可选，用于图片存储）

### 2. 数据库初始化
执行 `database/schema.sql` 创建数据库和表结构。

### 3. 配置修改
编辑 `aha-admin/src/main/resources/application.yml`：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/aha_system?...
    username: your_username
    password: your_password
  
  data:
    redis:
      host: localhost
      port: 6379

minio:
  endpoint: http://localhost:9000
  access-key: your_access_key
  secret-key: your_secret_key
```

### 4. 启动项目
```bash
cd androgenetic-alopecia-system
mvn clean install
cd aha-admin
mvn spring-boot:run
```

### 5. 访问接口
默认端口：`http://localhost:8080/api`

## API 规范

### 统一返回格式
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "timestamp": 1703123456789
}
```

### 状态码说明
| 状态码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 参数错误 |
| 401 | 未登录或 token 过期 |
| 403 | 无权限访问 |
| 404 | 资源不存在 |
| 500 | 系统错误 |

## 开发规范

1. **分层架构**：严格遵循 Controller → Service → DAO 的调用链
2. **统一返回**：所有接口必须返回 `Result<T>` 格式
3. **异常处理**：业务异常使用 `BusinessException`，禁止在 Controller 中捕获异常
4. **命名规范**：
   - Entity：与数据库表名对应（如 `User`）
   - DTO：`XxxDTO`（如 `UserDTO`）
   - VO：`XxxVO`（如 `UserVO`）
   - Mapper：`XxxMapper`（如 `UserMapper`）
   - Service：`XxxService` + `XxxServiceImpl`
   - Controller：`XxxController`

## 后续开发计划

1. 完成数据库实体类（Entity）和 Mapper 接口
2. 实现用户认证模块（登录/注册）
3. 实现用药管理模块
4. 实现图片记录模块（集成 MinIO）
5. 实现分析功能模块
6. 添加单元测试
