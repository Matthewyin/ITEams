# ITEams - IT资产管理系统

<div align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.3-brightgreen" alt="Spring Boot">
  <img src="https://img.shields.io/badge/JDK-17-orange" alt="JDK">
  <img src="https://img.shields.io/badge/MySQL-8.0-blue" alt="MySQL">
  <img src="https://img.shields.io/badge/Vue.js-3-green" alt="Vue.js">
  <img src="https://img.shields.io/badge/License-MIT-yellow" alt="License">
</div>

## 📝 项目介绍

ITEams是一个面向企业的IT资产全生命周期管理系统，专注于帮助IT部门高效管理硬件资产、软件许可和维保合同。系统支持大规模Excel数据导入、资产状态追踪、位置管理和维保提醒等功能，旨在提高IT资产使用效率，降低总体拥有成本。

### ✨ 主要特点

- **多级分类管理**：支持灵活的三级分类体系，方便资产归类与查询
- **空间位置跟踪**：记录资产位置变更历史，支持机房、机柜定位
- **状态变更追溯**：完整记录资产状态变更过程，支持审计追溯
- **批量Excel导入**：支持大规模Excel数据导入，自动处理数据关联
- **维保合同管理**：自动关联资产与维保信息，提供到期提醒
- **分离的日志系统**：不同级别日志分文件存储，便于问题排查
- **安全的用户认证**：使用JWT实现的无状态用户认证机制，提供安全可靠的访问控制
- **稳定的分页序列化**：使用自定义PagedResponse包装分页结果，确保API返回一致的JSON结构

## 🚀 最新更新

- **分页序列化优化**：使用自定义PagedResponse包装Spring Data分页结果，解决PageImpl序列化警告
- **增强Spring Data Web支持**：添加@EnableSpringDataWebSupport配置，改进分页参数处理
- **优化Excel导入功能**：支持多种日期格式，自动处理分类数据
- **维保合同去重**：自动检测并合并重复的维保合同记录
- **分离日志系统**：将DEBUG/INFO/WARN/ERROR日志分开存储，提高可维护性
- **升级Spring Boot**：从2.7.8升级到3.4.3，提升性能和安全性
- **增强用户认证**：完善了JWT认证实现，修复了登录验证问题

## 🧰 技术栈

### 后端技术

- **核心框架**：Spring Boot 3.4.3
- **ORM框架**：Spring Data JPA
- **数据库**：MySQL 8.0
- **服务层**：Spring MVC
- **Java版本**：JDK 17
- **项目构建**：Maven
- **数据验证**：Spring Validation
- **Excel处理**：Apache POI 5.3.0
- **日志框架**：SLF4J + Logback
- **安全框架**：Spring Security
- **身份认证**：JWT (JSON Web Token)
- **代码简化**：Lombok

### 前端技术

- **框架**：Vue.js 3
- **UI组件**：Element Plus
- **状态管理**：Pinia
- **路由**：Vue Router
- **HTTP客户端**：Axios
- **图表库**：ECharts

## 📂 项目目录结构

```
ITEAMS/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── iteams/
│       │           ├── ITAssetApplication.java      # 应用程序入口
│       │           ├── annotation/                  # 自定义注解
│       │           │   └── OperationLog.java        # 操作日志注解
│       │           ├── aspect/                      # AOP切面
│       │           │   └── OperationLogAspect.java  # 操作日志切面
│       │           ├── config/                      # 配置类
│       │           │   ├── AuthProviderConfig.java  # 认证提供者配置
│       │           │   ├── DataInitializer.java     # 数据初始化
│       │           │   ├── JwtAuthenticationEntryPoint.java # JWT认证入口点
│       │           │   ├── JwtAuthenticationFilter.java # JWT认证过滤器
│       │           │   ├── SchedulingConfig.java    # 定时任务配置
│       │           │   ├── SecurityConfig.java      # 安全配置
│       │           │   └── WebConfig.java           # CORS和Web配置
│       │           ├── controller/                  # 控制器
│       │           │   ├── AuthController.java      # 认证控制器
│       │           │   ├── ImportController.java    # Excel导入处理控制器
│       │           │   ├── OperationLogController.java # 操作日志控制器
│       │           │   └── PasswordResetController.java # 密码重置控制器
│       │           ├── model/                       # 数据模型
│       │           │   ├── dto/                     # 数据传输对象
│       │           │   │   ├── ApiResponse.java     # 统一API响应
│       │           │   │   ├── ImportProgressDTO.java # 导入进度
│       │           │   │   ├── ImportResultDTO.java # 导入结果
│       │           │   │   ├── LoginRequestDTO.java # 登录请求
│       │           │   │   ├── LoginResponseDTO.java # 登录响应
│       │           │   │   ├── OperationLogQuery.java # 操作日志查询
│       │           │   │   ├── pagination/          # 分页相关DTO
│       │           │   │   │   └── PagedResponse.java # 分页响应包装类
│       │           │   │   └── UserInfoDTO.java    # 用户信息
│       │           │   ├── entity/                  # 实体类
│       │           │   │   ├── AssetMaster.java     # 资产主表实体
│       │           │   │   ├── CategoryMetadata.java # 分类元数据
│       │           │   │   ├── ChangeTrace.java     # 变更记录
│       │           │   │   ├── OperationLog.java    # 操作日志
│       │           │   │   ├── SpaceTimeline.java   # 空间位置时间线
│       │           │   │   ├── User.java            # 用户实体
│       │           │   │   └── WarrantyContract.java # 维保合同
│       │           │   ├── enums/                   # 枚举类型
│       │           │   │   ├── ModuleType.java      # 模块类型
│       │           │   │   ├── OperationType.java   # 操作类型
│       │           │   │   └── StatusType.java      # 状态类型
│       │           │   └── vo/                      # 视图对象
│       │           │       ├── ApiResponse.java     # 统一API响应视图
│       │           │       ├── OperationLogDetailVO.java # 操作日志详情
│       │           │       ├── OperationLogStatsVO.java # 操作日志统计
│       │           │       └── OperationLogVO.java  # 操作日志视图
│       │           ├── repository/                  # 数据访问层
│       │           │   ├── AssetRepository.java     # 资产数据访问
│       │           │   ├── CategoryRepository.java  # 分类数据访问
│       │           │   ├── ChangeTraceRepository.java # 变更记录数据访问
│       │           │   ├── OperationLogRepository.java # 操作日志数据访问
│       │           │   ├── SpaceRepository.java     # 空间位置数据访问
│       │           │   ├── UserRepository.java      # 用户数据访问
│       │           │   └── WarrantyRepository.java  # 维保合同数据访问
│       │           ├── service/                     # 服务层
│       │           │   ├── AuthService.java         # 认证服务接口
│       │           │   ├── CategoryService.java     # 分类管理服务接口
│       │           │   ├── ChangeTraceService.java  # 变更追踪服务
│       │           │   ├── ImportService.java       # Excel导入服务
│       │           │   ├── OperationLogService.java # 操作日志服务
│       │           │   ├── SpaceService.java        # 空间位置管理
│       │           │   └── WarrantyService.java     # 维保管理
│       │           ├── service/impl/                # 服务实现
│       │           │   ├── AuthServiceImpl.java     # 认证服务实现
│       │           │   ├── CategoryServiceImpl.java # 分类服务实现
│       │           │   ├── ChangeTraceServiceImpl.java # 变更追踪实现
│       │           │   ├── ImportServiceImpl.java   # 导入服务实现
│       │           │   ├── OperationLogServiceImpl.java # 日志服务实现
│       │           │   ├── SpaceServiceImpl.java    # 空间服务实现
│       │           │   ├── UserDetailsServiceImpl.java # 用户详情服务
│       │           │   └── WarrantyServiceImpl.java # 维保服务实现
│       │           ├── task/                        # 定时任务
│       │           │   └── LogArchiveTask.java      # 日志归档任务
│       │           └── util/                        # 工具类
│       │               ├── excel/
│       │               │   ├── ExcelParser.java     # Excel解析器
│       │               │   └── RowProcessor.java    # 行处理接口
│       │               ├── Constants.java           # 常量定义
│       │               └── UuidGenerator.java       # UUID生成工具
│       └── resources/
│           ├── application.yml                     # 应用配置文件
│           ├── logback-spring.xml                  # 日志配置
│           ├── static/                             # 静态资源目录
│           ├── db/
│           │   └── data.sql  
│           │   └── schama.sql      # 基础表创建
│           └── templates/
│               └── import_template.xlsx            # 导入模板
├── logs/                                          # 日志目录
│   ├── iteams.log                                 # 主日志文件
│   ├── iteams-error.log                           # 错误日志
│   ├── iteams-warn.log                            # 警告日志
│   ├── iteams-debug.log                           # 调试日志
│   └── iteams-sql.log                             # SQL日志
├── pom.xml                                         # Maven配置
└── readme.md                                       # 项目说明文档
```

## 🔑 核心功能

### 1. Excel批量导入处理

系统支持批量导入Excel文件来创建和更新资产记录：

- **异步处理**：通过异步任务处理大文件导入，不阻塞用户操作
- **进度跟踪**：实时显示导入进度，支持查看成功和失败条目
- **智能解析**：支持多种日期格式，自动处理分类数据
- **数据验证**：全面检查数据完整性，确保导入高质量数据
- **重复检测**：通过指纹算法避免重复导入相同数据

### 2. 资产生命周期管理

- 新设备注册、配置与部署
- 设备状态变更追踪（使用中、维修中、报废等）
- 报废与处置流程管理
- 变更历史追溯

### 3. 资产信息管理

- 硬件清单（计算机、服务器、网络设备等）
- 软件许可证与合规管理
- 资产分类与标签管理

### 4. 位置与空间追踪

- 资产地理位置与分配记录
- 数据中心、机房、机柜定位
- 空间使用优化建议
- 位置变更历史

### 5. 维护与保修管理

- 维保合同跟踪与去重
- 维护计划与到期提醒
- 故障报告与处理
- 供应商管理

### 6. 日志与监控

- 分离的多级日志系统
- SQL查询独立记录
- 错误与警告单独存储
- 便于问题排查和性能优化

### 7. 分页数据优化

- **稳定的JSON结构**：自定义PagedResponse提供一致的分页数据格式
- **避免序列化警告**：解决Spring Data PageImpl序列化不稳定的问题
- **分离内容与元数据**：清晰区分数据内容和分页信息
- **更多分页元信息**：提供完整分页状态（是否有下一页、是否首尾页等）

## 📑 API文档

### 认证接口

#### 1. 用户登录

```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

响应示例：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "user": {
      "id": 1,
      "username": "admin",
      "roles": ["ROLE_ADMIN"]
    }
  }
}
```

#### 2. 获取用户信息

```http
GET /api/auth/user
Authorization: Bearer {token}
```

#### 3. 登出

```http
POST /api/auth/logout
Authorization: Bearer {token}
```

### 资产管理接口

#### 1. 获取资产列表

```http
GET /api/assets?page=0&size=10&sort=createTime,desc
Authorization: Bearer {token}
```

查询参数：
- `page`: 页码（从0开始）
- `size`: 每页条数
- `sort`: 排序字段和方向
- `type`: 资产类型过滤
- `status`: 状态过滤
- `keyword`: 关键字搜索

响应示例（优化后的分页格式）：
```json
{
  "code": 200,
  "message": "获取资产列表成功",
  "data": {
    "content": [
      {
        "id": 1,
        "name": "ThinkPad X1",
        "type": "LAPTOP",
        "status": "IN_USE"
      }
    ],
    "metadata": {
      "page": 0,
      "size": 10,
      "totalElements": 150,
      "totalPages": 15,
      "first": true,
      "last": false,
      "hasNext": true,
      "hasPrevious": false
    }
  }
}
```

#### 2. 创建资产

```http
POST /api/assets
Authorization: Bearer {token}
Content-Type: application/json

{
  "name": "ThinkPad X1",
  "type": "LAPTOP",
  "status": "IN_USE",
  "location": "R&D-001",
  "purchaseDate": "2024-03-19"
}
```

#### 3. 更新资产

```http
PUT /api/assets/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
  "status": "MAINTENANCE",
  "location": "IT-002"
}
```

### 日志查询接口

```http
GET /api/logs
Authorization: Bearer {token}

查询参数：
- startTime: 开始时间（yyyy-MM-dd HH:mm:ss）
- endTime: 结束时间
- type: 日志类型（OPERATION/ERROR/LOGIN）
- module: 模块名称
- username: 操作人
```

## 🔧 环境要求

### 基本要求

- JDK 17+
- MySQL 8.0+
- Maven 3.8+
- Node.js 16+ (前端开发)

### 推荐开发工具

- IntelliJ IDEA 2023.1+ (推荐使用Ultimate版本)
- MySQL Workbench 8.0+ (数据库管理)
- Postman (接口测试)
- Git 2.30+

### 推荐系统配置

- 内存：16GB+
- 处理器：4核+
- 磁盘空间：10GB+
- 网络：带宽要求不高，普通宽带即可

## 🛠️ 安装与部署

### 1. 克隆项目

```bash
git clone https://github.com/yourusername/ITEams.git
cd ITEams
```

### 2. 配置数据库

- 创建MySQL数据库 `iteams_db`
- 修改 `application.yml` 中的数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/iteams_db?useUnicode=true&characterEncoding=utf8
    username: your_username
    password: your_password
```

### 3. 构建与运行

```bash
mvn clean install
java -jar target/asset-management-0.0.1-SNAPSHOT.jar
```

### 4. 访问应用

```
http://localhost:8080
```

## 📚 开发指南

### 添加新实体

1. 创建实体类并添加JPA注解
2. 创建对应的Repository接口
3. 定义Service接口与实现
4. 添加Controller以提供REST API

### 分页数据处理

当需要返回分页数据时，应使用`PagedResponse`进行包装：

```java
@GetMapping
public ResponseEntity<ApiResponse<PagedResponse<AssetDTO>>> getAssets(Pageable pageable) {
    Page<AssetDTO> page = assetService.getAssets(pageable);
    PagedResponse<AssetDTO> response = PagedResponse.of(page);
    return ResponseEntity.ok(ApiResponse.success("获取资产列表成功", response));
}
```

### 代码规范

- 遵循Java代码规范
- 确保所有公开API有适当的JavaDoc文档
- 使用适当的异常处理
- 遵循RESTful API设计原则
- 编写单元测试确保代码质量

## 🤝 贡献指南

1. Fork 本仓库
2. 创建特性分支：`git checkout -b feature/amazing-feature`
3. 提交更改：`git commit -m '添加新功能'`
4. 推送到分支：`git push origin feature/amazing-feature`
5. 提交Pull Request

## 📜 许可证

本项目采用 MIT 许可证 - 详情请参阅 LICENSE 文件