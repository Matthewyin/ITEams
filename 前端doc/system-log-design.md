# 系统操作日志设计文档

## 1. 功能概述

系统操作日志模块用于记录用户在系统中的所有增删改操作，便于系统管理员追踪用户行为、审计操作历史以及问题排查。

## 2. 记录内容设计

系统操作日志应记录以下核心信息：

- **操作类型**：增加(CREATE)、修改(UPDATE)、删除(DELETE)、查询(QUERY)、导入(IMPORT)、导出(EXPORT)等
- **操作模块**：资产管理、部门管理、用户管理等
- **操作对象ID**：被操作记录的唯一标识
- **操作前数据**：JSON格式存储操作前的数据状态（适用于修改和删除）
- **操作后数据**：JSON格式存储操作后的数据状态（适用于新增和修改）
- **操作人信息**：用户ID、用户名、角色
- **操作时间**：精确到毫秒
- **IP地址**：操作者的IP地址
- **操作结果**：成功/失败
- **失败原因**：如果操作失败，记录失败原因

## 3. 需要记录的关键操作

### 资产管理模块
- 新增资产
- 修改资产信息
- 删除资产
- 批量删除资产
- 导入资产
- 导出资产

### 部门管理模块
- 新增部门
- 修改部门信息
- 删除部门

### 用户管理模块
- 用户登录/登出
- 新增用户
- 修改用户信息
- 删除用户
- 修改密码
- 重置密码

### 系统设置模块
- 修改系统配置
- 备份/恢复数据

## 4. 后端API需求

### 4.1 API概述

系统操作日志模块需要提供以下API接口：

| 接口名称 | 请求方法 | 接口路径 | 描述 |
|---------|--------|---------|------|
| 获取日志列表 | GET | /api/logs | 分页获取系统操作日志列表，支持多条件筛选 |
| 获取日志详情 | GET | /api/logs/:id | 获取指定ID的日志详细信息 |
| 导出日志 | POST | /api/logs/export | 导出符合条件的日志为Excel文件 |
| 获取日志统计 | GET | /api/logs/stats | 获取日志统计数据 |
| 批量删除日志 | DELETE | /api/logs | 批量删除指定的日志记录（可选，需要权限控制） |
| 清空日志 | DELETE | /api/logs/clear | 清空所有日志记录（可选，需要权限控制） |

### 4.2 API详细规范

#### 4.2.1 获取日志列表

- **接口URL**: `/api/logs`
- **请求方法**: GET
- **接口描述**: 分页获取系统操作日志列表，支持多条件筛选

**请求参数**:

| 参数名 | 类型 | 必填 | 描述 | 示例值 |
|-------|-----|-----|------|-------|
| page | Integer | 是 | 当前页码，从1开始 | 1 |
| pageSize | Integer | 是 | 每页记录数 | 10 |
| startTime | String | 否 | 开始时间，ISO8601格式 | 2025-01-01T00:00:00 |
| endTime | String | 否 | 结束时间，ISO8601格式 | 2025-03-19T00:00:00 |
| operationType | String | 否 | 操作类型，支持多选，逗号分隔 | CREATE,UPDATE |
| module | String | 否 | 操作模块，支持多选，逗号分隔 | ASSET,USER |
| operatorId | String | 否 | 操作人ID | 1 |
| status | String | 否 | 操作结果，SUCCESS或FAILED | SUCCESS |
| keyword | String | 否 | 搜索关键词，匹配描述字段 | 资产 |

**响应参数**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 100,
    "pages": 10,
    "list": [
      {
        "id": "1",
        "operationType": "CREATE",
        "module": "ASSET",
        "objectId": "123",
        "objectType": "ASSET",
        "operatorId": "1",
        "operatorName": "管理员",
        "operationTime": "2025-03-19T10:00:00",
        "status": "SUCCESS",
        "description": "创建了资产 'ThinkPad X1'"
      }
    ]
  }
}
```

**状态码**:

| 状态码 | 描述 |
|-------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 权限不足 |
| 500 | 服务器内部错误 |

#### 4.2.2 获取日志详情

- **接口URL**: `/api/logs/:id`
- **请求方法**: GET
- **接口描述**: 获取指定ID的日志详细信息

**路径参数**:

| 参数名 | 类型 | 必填 | 描述 | 示例值 |
|-------|-----|-----|------|-------|
| id | String | 是 | 日志ID | 1 |

**响应参数**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": "1",
    "operationType": "UPDATE",
    "module": "ASSET",
    "objectId": "123",
    "objectType": "ASSET",
    "beforeData": { "name": "旧名称", "status": "IN_USE" },
    "afterData": { "name": "新名称", "status": "IDLE" },
    "operatorId": "1",
    "operatorName": "管理员",
    "operationTime": "2025-03-19T10:00:00",
    "status": "SUCCESS",
    "errorMessage": null,
    "description": "修改了资产 'ThinkPad X1' 的信息"
  }
}
```

**状态码**:

| 状态码 | 描述 |
|-------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 权限不足 |
| 404 | 日志不存在 |
| 500 | 服务器内部错误 |

#### 4.2.3 导出日志

- **接口URL**: `/api/logs/export`
- **请求方法**: POST
- **接口描述**: 导出符合条件的日志为Excel文件

**请求参数**:

同获取日志列表，但通过POST请求体传递

```json
{
  "startTime": "2025-01-01T00:00:00",
  "endTime": "2025-03-19T00:00:00",
  "operationType": "CREATE,UPDATE",
  "module": "ASSET",
  "operatorId": "1",
  "status": "SUCCESS",
  "keyword": "资产"
}
```

**响应**:

- Content-Type: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet
- Content-Disposition: attachment; filename=operation_logs_20250319.xlsx

**状态码**:

| 状态码 | 描述 |
|-------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 权限不足 |
| 500 | 服务器内部错误 |

#### 4.2.4 获取日志统计

- **接口URL**: `/api/logs/stats`
- **请求方法**: GET
- **接口描述**: 获取日志统计数据

**请求参数**:

| 参数名 | 类型 | 必填 | 描述 | 示例值 |
|-------|-----|-----|------|-------|
| startTime | String | 否 | 开始时间，ISO8601格式 | 2025-01-01T00:00:00 |
| endTime | String | 否 | 结束时间，ISO8601格式 | 2025-03-19T00:00:00 |
| module | String | 否 | 操作模块，支持多选，逗号分隔 | ASSET,USER |

**响应参数**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalCount": 1000,
    "successCount": 980,
    "failedCount": 20,
    "typeStats": {
      "CREATE": 300,
      "UPDATE": 500,
      "DELETE": 200
    },
    "moduleStats": {
      "ASSET": 600,
      "USER": 300,
      "DEPARTMENT": 100
    },
    "dailyStats": [
      {
        "date": "2025-03-18",
        "count": 50,
        "successCount": 48,
        "failedCount": 2
      },
      {
        "date": "2025-03-19",
        "count": 45,
        "successCount": 44,
        "failedCount": 1
      }
    ],
    "operatorStats": [
      {
        "operatorId": "1",
        "operatorName": "管理员",
        "count": 500
      },
      {
        "operatorId": "2",
        "operatorName": "操作员",
        "count": 300
      }
    ]
  }
}
```

**状态码**:

| 状态码 | 描述 |
|-------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 权限不足 |
| 500 | 服务器内部错误 |

## 5. 数据库设计

### 5.1 数据库表设计

#### 5.1.1 操作日志表 (sys_operation_log)

```sql
CREATE TABLE sys_operation_log (
    id VARCHAR(36) PRIMARY KEY COMMENT '主键，UUID格式',
    operation_type VARCHAR(20) NOT NULL COMMENT '操作类型：CREATE、UPDATE、DELETE、QUERY、IMPORT、EXPORT等',
    module VARCHAR(50) NOT NULL COMMENT '操作模块：ASSET、USER、DEPARTMENT等',
    object_id VARCHAR(36) COMMENT '操作对象ID',
    object_type VARCHAR(50) COMMENT '操作对象类型',
    before_data LONGTEXT COMMENT '操作前数据(JSON格式)',
    after_data LONGTEXT COMMENT '操作后数据(JSON格式)',
    operator_id VARCHAR(36) NOT NULL COMMENT '操作人ID',
    operator_name VARCHAR(50) NOT NULL COMMENT '操作人姓名',
    operation_time DATETIME(3) NOT NULL COMMENT '操作时间，精确到毫秒',
    ip_address VARCHAR(50) COMMENT '操作者IP地址',
    user_agent VARCHAR(500) COMMENT '用户代理字符串',
    status VARCHAR(20) NOT NULL COMMENT '操作结果：SUCCESS或FAILED',
    error_message TEXT COMMENT '错误信息',
    description VARCHAR(500) COMMENT '操作描述',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
    INDEX idx_operation_time (operation_time),
    INDEX idx_operator_id (operator_id),
    INDEX idx_module (module),
    INDEX idx_operation_type (operation_type),
    INDEX idx_object_id (object_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统操作日志表';
```

#### 5.1.2 日志分区表（可选）

对于大型系统，可以考虑按时间分区的设计，例如按月分区：

```sql
CREATE TABLE sys_operation_log_202503 LIKE sys_operation_log;
```

### 5.2 数据字典

#### 5.2.1 操作类型 (operation_type)

| 编码 | 名称 | 描述 |
|-------|------|------|
| CREATE | 新增 | 新增数据记录 |
| UPDATE | 修改 | 修改数据记录 |
| DELETE | 删除 | 删除数据记录 |
| QUERY | 查询 | 查询数据记录（可选记录） |
| IMPORT | 导入 | 批量导入数据 |
| EXPORT | 导出 | 导出数据 |
| LOGIN | 登录 | 用户登录系统 |
| LOGOUT | 登出 | 用户登出系统 |
| UPLOAD | 上传 | 上传文件 |
| DOWNLOAD | 下载 | 下载文件 |

#### 5.2.2 操作模块 (module)

| 编码 | 名称 | 描述 |
|-------|------|------|
| ASSET | 资产管理 | 资产相关操作 |
| USER | 用户管理 | 用户相关操作 |
| DEPARTMENT | 部门管理 | 部门相关操作 |
| SYSTEM | 系统设置 | 系统设置相关操作 |
| AUTH | 权限管理 | 权限相关操作 |
| DASHBOARD | 仪表盘 | 仪表盘相关操作 |

#### 5.2.3 操作结果 (status)

| 编码 | 名称 | 描述 |
|-------|------|------|
| SUCCESS | 成功 | 操作成功完成 |
| FAILED | 失败 | 操作执行失败 |

### 5.3 索引设计说明

| 索引名 | 索引字段 | 索引类型 | 用途 |
|---------|---------|---------|-----|
| PRIMARY KEY | id | 主键 | 唐一标识日志记录 |
| idx_operation_time | operation_time | 普通索引 | 加速时间范围查询 |
| idx_operator_id | operator_id | 普通索引 | 加速按操作人查询 |
| idx_module | module | 普通索引 | 加速按模块查询 |
| idx_operation_type | operation_type | 普通索引 | 加速按操作类型查询 |
| idx_object_id | object_id | 普通索引 | 加速按操作对象查询 |
| idx_status | status | 普通索引 | 加速按操作结果查询 |
| idx_created_at | created_at | 普通索引 | 加速按创建时间查询 |

### 5.4 数据存储注意事项

1. **JSON数据存储**：
   - before_data 和 after_data 字段使用 LONGTEXT 类型存储JSON格式数据
   - 存储前应进行敏感数据脱敏处理，如密码、证件号等
   - 对于大对象，可以只存储关键字段或差异字段，减少存储开销

2. **分表存储策略**：
   - 对于大量日志数据，建议按时间分表，如按月分表
   - 可以使用数据库分区表功能或应用程序实现分表访问
   - 建议实现日志数据归档功能，将旧数据移动到归档表

3. **性能考虑**：
   - 对于高并发系统，可以考虑异步日志记录，使用消息队列
   - 对于查询频率低的日志数据，可以考虑压缩存储或使用分布式文件系统

## 6. 实现建议与最佳实践

### 6.1 日志记录实现方案

#### 6.1.1 AOP方式实现

建议使用面向切面编程(AOP)方式实现日志记录，可以减少业务代码与日志记录代码的耦合。

```java
// 自定义注解
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {
    String module() default "";  // 模块
    String operationType() default "";  // 操作类型
    String description() default "";  // 描述
}

// AOP实现
@Aspect
@Component
public class OperationLogAspect {
    @Pointcut("@annotation(com.example.annotation.OperationLog)")
    public void operationLogPointCut() {}
    
    @Around("operationLogPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        // 记录操作前数据
        Object beforeData = getRequestData(point);
        
        // 执行目标方法
        Object result = null;
        try {
            result = point.proceed();
            // 记录成功日志
            saveLog(point, beforeData, result, true, null);
        } catch (Exception e) {
            // 记录失败日志
            saveLog(point, beforeData, null, false, e.getMessage());
            throw e;
        }
        
        return result;
    }
}
```

#### 6.1.2 事件驱动方式

对于复杂系统，可以采用事件驱动方式，通过发布/订阅模式实现日志记录。

```java
// 操作日志事件
@Data
public class OperationLogEvent {
    private String operationType;
    private String module;
    private String objectId;
    private String objectType;
    private Object beforeData;
    private Object afterData;
    private String operatorId;
    private String description;
    // 其他必要字段
}

// 事件监听器
@Component
public class OperationLogListener {
    @Async
    @EventListener
    public void handleOperationLogEvent(OperationLogEvent event) {
        // 异步处理日志记录
        saveOperationLog(event);
    }
}
```

### 6.2 数据对比实现

对于修改操作，需要记录操作前后的数据差异。建议使用差异对比工具，如javers或自定义实现。

```java
// 使用javers进行对象差异对比
@Service
public class DiffService {
    private final Javers javers;
    
    public DiffService() {
        this.javers = JaversBuilder.javers().build();
    }
    
    public JsonNode compareObjects(Object oldObj, Object newObj) {
        Diff diff = javers.compare(oldObj, newObj);
        return javers.getJsonConverter().toJson(diff.getChanges());
    }
}
```

### 6.3 安全性考虑

#### 6.3.1 数据脱敏

在记录日志时，必须对敏感信息进行脱敏处理：

```java
// 数据脱敏工具类
@Component
public class DataMaskingUtil {
    // 密码脱敏
    public static String maskPassword(String data) {
        return "******";
    }
    
    // 手机号脱敏
    public static String maskPhone(String phone) {
        if (phone == null || phone.length() < 11) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }
    
    // 身份证号脱敏
    public static String maskIdCard(String idCard) {
        if (idCard == null || idCard.length() < 15) {
            return idCard;
        }
        return idCard.substring(0, 6) + "********" + idCard.substring(14);
    }
}
```

#### 6.3.2 访问控制

日志查询应设置严格的权限控制，只允许授权用户访问。

```java
@PreAuthorize("hasRole('ADMIN') or hasAuthority('LOG_VIEW')")
@GetMapping("/api/logs")
public ResponseEntity<Page<OperationLogVO>> getLogs(OperationLogQuery query) {
    // 实现日志查询
}
```

### 6.4 性能优化建议

#### 6.4.1 异步日志记录

对于高并发系统，建议采用异步方式记录日志，可以使用消息队列如Kafka或RabbitMQ。

```java
// 日志生产者
@Service
public class OperationLogProducer {
    private final KafkaTemplate<String, OperationLogEvent> kafkaTemplate;
    
    public void sendLog(OperationLogEvent event) {
        kafkaTemplate.send("operation-logs", event);
    }
}

// 日志消费者
@Service
public class OperationLogConsumer {
    private final OperationLogRepository repository;
    
    @KafkaListener(topics = "operation-logs")
    public void consume(OperationLogEvent event) {
        OperationLog log = convertToEntity(event);
        repository.save(log);
    }
}
```

#### 6.4.2 数据库优化

1. **分表存储**：按时间分表，提高查询效率
2. **索引优化**：根据实际查询需求优化索引
3. **定期归档**：实现旧数据定期归档机制

```java
// 定时归档任务
@Component
public class LogArchiveTask {
    private final OperationLogService logService;
    
    @Scheduled(cron = "0 0 1 1 * ?") // 每月全1日凌晨1点执行
    public void archiveOldLogs() {
        // 归档三个月前的日志
        LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);
        logService.archiveLogs(threeMonthsAgo);
    }
}
```

## 7. 前端页面设计

### 7.1 日志列表页面
- **搜索区域**：
  - 时间范围选择器
  - 操作类型下拉选择
  - 操作模块下拉选择
  - 操作人输入框
  - 关键词搜索
  - 搜索/重置按钮

- **操作区域**：
  - 导出按钮

- **列表区域**：
  - 序号
  - 操作类型（带图标和颜色区分）
  - 操作模块
  - 操作描述
  - 操作人
  - 操作时间
  - IP地址
  - 操作结果（成功/失败标签）
  - 操作列（查看详情）

- **分页区域**

### 日志详情弹窗
- 基本信息区域
- 操作前后数据对比（类似Git差异对比）
- 失败原因（如果操作失败）

## 7. 实现计划

### 前端实现
1. 在路由配置中添加日志页面路由
2. 创建日志列表页面组件
3. 创建日志详情弹窗组件
4. 创建日志API服务
5. 添加到系统菜单

### 后端实现
1. 设计日志表结构
2. 实现AOP切面或拦截器记录操作日志
3. 实现日志查询API
4. 实现日志导出功能
