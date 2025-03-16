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

## 🚀 最新更新

- **优化Excel导入功能**：支持多种日期格式，自动处理分类数据
- **维保合同去重**：自动检测并合并重复的维保合同记录
- **分离日志系统**：将DEBUG/INFO/WARN/ERROR日志分开存储，提高可维护性
- **升级Spring Boot**：从2.7.8升级到3.4.3，提升性能和安全性

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
│       │           ├── ITAssetApplication.java
│       │           ├── config/
│       │           │   └── WebConfig.java           # CORS和Web配置
│       │           ├── controller/
│       │           │   ├── AssetController.java
│       │           │   ├── CategoryController.java
│       │           │   ├── ImportController.java    # Excel导入处理控制器
│       │           │   └── WarrantyController.java
│       │           ├── service/
│       │           │   ├── AssetService.java
│       │           │   ├── CategoryService.java     # 分类管理服务接口
│       │           │   ├── ImportService.java       # Excel导入服务
│       │           │   ├── SpaceService.java        # 空间位置管理
│       │           │   ├── WarrantyService.java     # 维保管理
│       │           │   └── ChangeTraceService.java  # 变更追踪
│       │           ├── repository/
│       │           │   ├── AssetRepository.java     # 资产数据访问
│       │           │   ├── CategoryRepository.java  # 分类数据访问
│       │           │   ├── SpaceRepository.java     # 空间位置数据访问
│       │           │   ├── WarrantyRepository.java  # 维保合同数据访问
│       │           │   └── ChangeTraceRepository.java # 变更记录数据访问
│       │           ├── model/
│       │           │   ├── entity/
│       │           │   │   ├── AssetMaster.java     # 资产主表实体
│       │           │   │   ├── CategoryMetadata.java # 分类元数据
│       │           │   │   ├── SpaceTimeline.java   # 空间位置时间线
│       │           │   │   ├── WarrantyContract.java # 维保合同
│       │           │   │   └── ChangeTrace.java     # 变更记录
│       │           │   ├── dto/
│       │           │   │   ├── AssetDTO.java        # 资产数据传输对象
│       │           │   │   ├── ImportResultDTO.java # 导入结果
│       │           │   │   └── ImportProgressDTO.java # 导入进度
│       │           │   └── vo/
│       │           │       ├── ApiResponse.java     # 统一API响应格式
│       │           │       └── ImportStatisticsVO.java # 导入统计视图
│       │           └── util/
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

## 🔧 环境要求

- JDK 17+
- MySQL 8.0+
- Maven 3.8+
- Node.js 16+ (前端开发)

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