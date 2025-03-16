

# ITEams - IT设备资产管理系统

## 项目介绍

ITEams是一个面向企业的IT设备资产全生命周期管理系统，旨在帮助IT部门高效管理硬件资产、软件许可、维保合同等，实现资产追踪、维护及优化配置，提高IT资产的使用效率，降低总体拥有成本。

## 技术栈

### 后端技术

- **核心框架**：Spring Boot 3.4.3
- **ORM框架**：Spring Data JPA
- **数据库**：MySQL 8.0
- **服务层**：Spring MVC
- **Java版本**：JDK 17
- **项目构建**：Maven
- **数据验证**：Spring Validation
- **Excel处理**：Apache POI 5.2.5
- **日志框架**：SLF4J + Logback
- **代码简化**：Lombok

### 前端技术

- **框架**：Vue.js 3
- **UI组件**：Element Plus
- **状态管理**：Pinia
- **路由**：Vue Router
- **HTTP客户端**：Axios
- **图表库**：ECharts

## 项目目录结构

```
ITEams/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── io/nssa/iteams/
│   │   │       ├── config/                 # 配置类
│   │   │       │   ├── CorsConfig.java     # CORS配置
│   │   │       │   └── WebConfig.java      # Web配置
│   │   │       ├── controller/             # 控制器层
│   │   │       │   ├── AssetController.java       # 资产控制器
│   │   │       │   ├── CategoryController.java    # 分类控制器
│   │   │       │   ├── LocationController.java    # 位置控制器
│   │   │       │   ├── ReportController.java      # 报表控制器
│   │   │       │   ├── UserController.java        # 用户控制器
│   │   │       │   └── WarrantyController.java    # 保修控制器
│   │   │       ├── entity/                # 实体类
│   │   │       │   ├── AssetMaster.java         # 资产主表
│   │   │       │   ├── ChangeTrace.java         # 变更记录
│   │   │       │   ├── CategoryMetadata.java    # 分类元数据
│   │   │       │   ├── SpaceTimeline.java       # 空间时间线
│   │   │       │   ├── User.java                # 用户
│   │   │       │   └── WarrantyContract.java    # 保修合同
│   │   │       ├── exception/             # 异常处理
│   │   │       │   ├── GlobalExceptionHandler.java   # 全局异常处理器
│   │   │       │   ├── ResourceNotFoundException.java # 资源不存在异常
│   │   │       │   └── ValidationException.java      # 验证异常
│   │   │       ├── repository/            # 数据访问层
│   │   │       │   ├── AssetMasterRepository.java      # 资产库
│   │   │       │   ├── CategoryMetadataRepository.java # 分类元数据库
│   │   │       │   ├── ChangeTraceRepository.java      # 变更记录库
│   │   │       │   ├── SpaceTimelineRepository.java    # 空间时间线库
│   │   │       │   ├── UserRepository.java             # 用户库
│   │   │       │   └── WarrantyContractRepository.java # 保修合同库
│   │   │       ├── service/               # 服务层
│   │   │       │   ├── AssetService.java         # 资产服务接口
│   │   │       │   ├── CategoryService.java      # 分类服务接口
│   │   │       │   ├── ReportService.java        # 报表服务接口
│   │   │       │   ├── UserService.java          # 用户服务接口
│   │   │       │   ├── WarrantyService.java      # 保修服务接口
│   │   │       │   └── impl/                     # 服务实现
│   │   │       │       ├── AssetServiceImpl.java
│   │   │       │       ├── CategoryServiceImpl.java
│   │   │       │       ├── ReportServiceImpl.java
│   │   │       │       ├── UserServiceImpl.java
│   │   │       │       └── WarrantyServiceImpl.java
│   │   │       ├── dto/                   # 数据传输对象
│   │   │       │   ├── AssetDTO.java      # 资产DTO
│   │   │       │   └── ReportDTO.java     # 报表DTO
│   │   │       ├── util/                  # 工具类
│   │   │       │   ├── ExcelHelper.java   # Excel工具
│   │   │       │   └── DateUtils.java     # 日期工具
│   │   │       └── ITEamsApplication.java # 应用主类
│   │   └── resources/
│   │       ├── static/                    # 静态资源
│   │       ├── templates/                 # 模板
│   │       ├── application.yaml           # 应用配置
│   │       └── i18n/                      # 国际化资源
│   │           └── messages.properties    # 消息资源
│   └── test/                              # 测试代码
│       └── java/
│           └── io/nssa/iteams/
│               └── ITEamsApplicationTests.java
├── pom.xml                                # Maven配置
├── .gitignore                             # Git忽略文件
├── LICENSE                                # 许可证
└── README.md                              # 项目说明
```

## 核心功能

1. **资产生命周期管理**
    - 新设备注册、配置与部署
    - 设备状态变更追踪（使用中、维修中、报废等）
    - 报废与处置流程管理

2. **资产信息管理**
    - 硬件清单（计算机、服务器、网络设备等）
    - 软件许可证与合规管理
    - 资产分类与标签管理

3. **位置与空间追踪**
    - 资产地理位置与分配记录
    - 空间使用优化建议

4. **维护与保修管理**
    - 维保合同跟踪
    - 维护计划与提醒
    - 故障报告与处理

5. **报表与分析**
    - 资产使用率分析
    - 资产成本分析
    - 定制化报表导出

## 环境要求

- JDK 17+
- MySQL 8.0+
- Maven 3.8+

## 安装与部署

1. **克隆项目**
```
git clone https://github.com/nssa-io/ITEams.git
   cd ITEams
```

2. **配置数据库**
    - 创建MySQL数据库 `iteams_db`
    - 根据需要修改 `application.yaml` 中的数据库连接信息

3. **构建与运行**
```
mvn clean install
   java -jar target/ITEams-0.0.1-SNAPSHOT.jar
```

4. **访问应用**
```
http://localhost:8080/api
```

## API文档

启动应用后，可通过以下地址访问Swagger UI API文档：
```
http://localhost:8080/api/swagger-ui.html
```

## 开发指南

1. **添加新实体**
    - 创建实体类并添加JPA注解
    - 创建对应的Repository接口
    - 定义Service接口与实现
    - 添加Controller以提供REST API

2. **代码规范**
    - 遵循Java代码规范
    - 确保所有公开API有适当的JavaDoc文档
    - 使用适当的异常处理
    - 遵循RESTful API设计原则

## 贡献指南

1. Fork 本仓库
2. 创建特性分支：`git checkout -b feature/amazing-feature`
3. 提交更改：`git commit -m 'Add some amazing feature'`
4. 推送到分支：`git push origin feature/amazing-feature`
5. 提交Pull Request

## 许可证

本项目采用 MIT 许可证 

---

© 2025 netc2c. All Rights Reserved.