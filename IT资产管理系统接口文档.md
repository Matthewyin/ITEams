# IT资产管理系统接口文档

## 目录

1. [接口基础信息](#接口基础信息)
2. [用户管理接口](#用户管理接口)
3. [资产管理接口](#资产管理接口)
4. [分类管理接口](#分类管理接口)
5. [空间管理接口](#空间管理接口)
6. [维保管理接口](#维保管理接口)
7. [数据导入导出接口](#数据导入导出接口) 
8. [通用响应格式](#通用响应格式)
9. [错误码说明](#错误码说明)

## 接口基础信息

### 基础URL

```
/api
```

### 认证方式

系统采用基于JWT的Token认证机制，所有非公开接口需在请求头中携带Token

```
Authorization: Bearer {token}
```

### 内容类型

```
Content-Type: application/json
```

## 用户管理接口

### 1. 用户登录

- **URL**: `/auth/login`
- **Method**: `POST`
- **描述**: 用户登录并获取Token
- **请求参数**:

```
{
  "username": "admin",
  "password": "123456"
}
```

- **响应示例**:

```
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "expires": 3600,
    "userInfo": {
      "id": 1,
      "username": "admin",
      "realName": "管理员",
      "avatarUrl": "/avatars/admin.jpg",
      "lastLoginTime": "2023-05-20T14:30:25",
      "roles": ["admin"],
      "permissions": ["user:add", "user:edit", "user:delete"]
    }
  }
}
```

### 2. 获取当前用户信息

- **URL**: ``/auth/user``
- **Method**: `GET`
- **描述**: 获取当前登录用户信息
- **响应示例**:

```
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "username": "admin",
    "realName": "管理员",
  	"avatarUrl": "/avatars/admin.jpg",
    "lastLoginTime": "2023-05-20T14:30:25",
    "roles": ["admin"],
    "permissions": ["user:add", "user:edit", "user:delete"]
  }
}
```

### 3. 获取用户列表

- **URL**: `/users`

- **Method**: `GET`

- **描述**: 分页获取用户列表

- 请求参数

  : 

  - query: string (可选，支持用户名、真实姓名、邮箱模糊搜索)
  - role: string (可选，角色筛选)
  - status: int (可选，0-禁用，1-启用)
  - page: int (当前页码，默认1)
  - size: int (每页条数，默认10)
  - sort: string (排序方式，默认id,desc)

- **响应示例**:

```
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "content": [
      {
        "id": 1,
        "username": "admin",
        "realName": "管理员",
        "avatarUrl": "/avatars/admin.jpg",
        "lastLoginTime": "2023-05-20T14:30:25",
        "enabled": true,
        "accountNonExpired": true,
        "accountNonLocked": true,
        "credentialsNonExpired": true,
        "roles": ["admin"]
      }
    ],
    "totalElements": 50,
    "totalPages": 5,
    "size": 10,
    "number": 0
  }
}
```

### 4. 创建用户

- **URL**: `/users`
- **Method**: `POST`
- **描述**: 创建新用户
- **请求参数**:

```
{
  "username": "newuser",
  "password": "password123",
  "realName": "新用户",
  "roles": ["user"]
}
```

- **响应示例**:

```
{
  "code": 200,
  "message": "创建用户成功",
  "data": {
    "id": 51,
    "username": "newuser",
    "realName": "新用户",
    "avatarUrl": null,
    "lastLoginTime": null,
    "enabled": true,
    "roles": ["user"]
  }
}
```

### 5. 更新用户信息

- **URL**: `/users/{id}`
- **Method**: `PUT`
- **描述**: 更新用户信息
- **请求参数**:

```
{
  "realName": "用户已更新",
  "roles": ["user", "manager"]
}
```

- **响应示例**:

```
{
  "code": 200,
  "message": "更新用户成功",
  "data": {
    "id": 51,
    "username": "newuser",
    "realName": "用户已更新",
    "avatarUrl": null,
    "lastLoginTime": null,
    "enabled": true,
    "roles": ["user", "manager"]
  }
}
```

### 6. 删除用户

- **URL**: `/users/{id}`
- **Method**: `DELETE`
- **描述**: 删除指定用户
- **响应示例**:

```
{
  "code": 200,
  "message": "删除用户成功",
  "data": null
}
```

### 7. 重置用户密码

- **URL**: `/users/{id}/reset-password`
- **Method**: `POST`
- **描述**: 重置指定用户密码
- **响应示例**:

```
{
  "code": 200,
  "message": "重置密码成功",
  "data": {
    "password": "tempPass123"
  }
}
```

### 8. 检查用户名是否存在

- **URL**: `/users/check-username`

- **Method**: `GET`

- **描述**: 检查用户名是否已存在

- 请求参数

  : 

  - username: string (必填)

- **响应示例**:

```
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "exists": true
  }
}
```

### 9. 获取所有角色列表

- **URL**: `/users/roles`
- **Method**: `GET`
- **描述**: 获取系统中所有角色列表
- **响应示例**:

```
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "code": "admin",
      "name": "管理员"
    },
    {
      "code": "user",
      "name": "普通用户"
    }
  ]
}
```

## 资产管理接口

### 1. 获取资产列表

- **URL**: `/assets`

- **Method**: `GET`

- **描述**: 分页获取资产列表

- 请求参数

  : 

  - query: string (可选，支持资产名称、编号模糊搜索)
  - categoryId: number (可选，分类ID筛选)
  - status: string (可选，资产状态筛选，如IN_USE等)
  - page: int (当前页码，默认1)
  - size: int (每页条数，默认10)
  - sort: string (排序方式，默认asset_id,desc)

- **响应示例**:

```
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "content": [
      {
        "assetId": 1,
        "assetUuid": "AST20230501-abcdef123456",
        "assetNo": "IT01HR2305001",
        "assetName": "Dell PowerEdge R740服务器",
        "currentStatus": "IN_USE",
        "categoryHierarchy": {
          "l1": {"id": 1, "name": "IT设备"},
          "l2": {"id": 3, "name": "服务器"},
          "l3": {"id": 5, "name": "机架式"}
        },
        "space": {
          "dataCenter": "华东数据中心",
          "roomName": "A机房",
          "cabinetNo": "3排机柜",
          "uPosition": "U12",
          "keeper": "张三"
        },
        "warranty": {
          "warrantyId": 101,
          "provider": "戴尔",
          "endDate": "2025-05-01"
        },
        "createdAt": "2023-05-01T10:30:00"
      }
    ],
    "totalElements": 120,
    "totalPages": 12,
    "size": 10,
    "number": 0
  }
}
```

### 2. 获取资产详情

- **URL**: `/assets/{id}`
- **Method**: `GET`
- **描述**: 获取资产详细信息
- **响应示例**:

```
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "assetId": 1,
    "assetUuid": "AST20230501-abcdef123456",
    "assetNo": "IT01HR2305001",
    "assetName": "Dell PowerEdge R740服务器",
    "currentStatus": "IN_USE",
    "categoryHierarchy": {
      "l1": {"id": 1, "name": "IT设备", "code": "IT"},
      "l2": {"id": 3, "name": "服务器", "code": "SV"},
      "l3": {"id": 5, "name": "机架式", "code": "RK"}
    },
    "spaceHistory": [
      {
        "spaceId": 101,
        "locationPath": "华东数据中心/A机房/3排机柜/U12",
        "dataCenter": "华东数据中心",
        "roomName": "A机房",
        "cabinetNo": "3排机柜",
        "uPosition": "U12",
        "environment": "生产环境",
        "keeper": "张三",
        "isCurrent": true,
        "validFrom": "2023-05-01T10:30:00",
        "validTo": null
      },
      {
        "spaceId": 100,
        "locationPath": "IT设备库房/待分配区",
        "dataCenter": null,
        "roomName": "IT设备库房",
        "cabinetNo": null,
        "uPosition": null,
        "environment": "库存环境",
        "keeper": "李四",
        "isCurrent": false,
        "validFrom": "2023-04-15T09:00:00",
        "validTo": "2023-05-01T10:29:59"
      }
    ],
    "warrantyHistory": [
      {
        "warrantyId": 101,
        "contractNo": "DL-2023-0501",
        "provider": "戴尔",
        "contactPerson": "戴尔客服",
        "contactPhone": "400-123-4567",
        "warrantyLevel": "7x24金牌服务",
        "startDate": "2023-05-01",
        "endDate": "2025-05-01",
        "isCurrent": true
      }
    ],
    "createdAt": "2023-05-01T10:30:00",
    "updatedAt": "2023-05-01T10:30:00"
  }
}
```

### 3. 创建资产

- **URL**: `/assets`
- **Method**: `POST`
- **描述**: 创建新资产
- **请求参数**:

```
{
  "assetName": "Cisco UCS C220 M5服务器",
  "categoryHierarchy": {
    "l1": 1,
    "l2": 3,
    "l3": 5
  },
  "currentStatus": "INVENTORY",
  "spaceInfo": {
    "dataCenter": "华南数据中心",
    "roomName": "IT设备库房",
    "environment": "库存环境",
    "keeper": "王五"
  },
  "warrantyInfo": {
    "contractNo": "CS-2023-0602",
    "provider": "思科",
    "contactPerson": "思科客服",
    "contactPhone": "400-888-9999",
    "warrantyLevel": "标准保修",
    "startDate": "2023-06-02",
    "endDate": "2024-06-02"
  }
}
```

- **响应示例**:

```
{
  "code": 200,
  "message": "创建资产成功",
  "data": {
    "assetId": 121,
    "assetUuid": "AST20230602-xyz789012345",
    "assetNo": "IT01IT2306010",
    "assetName": "Cisco UCS C220 M5服务器",
    "currentStatus": "INVENTORY"
  }
}
```

### 4. 更新资产信息

- **URL**: `/assets/{id}`
- **Method**: `PUT`
- **描述**: 更新资产信息
- **请求参数**:

```
{
  "assetName": "Cisco UCS C220 M5服务器(已升级)",
  "currentStatus": "IN_USE"
}
```

- **响应示例**:

```
{
  "code": 200,
  "message": "更新资产成功",
  "data": {
    "assetId": 121,
    "assetUuid": "AST20230602-xyz789012345",
    "assetNo": "IT01IT2306010",
    "assetName": "Cisco UCS C220 M5服务器(已升级)",
    "currentStatus": "IN_USE"
  }
}
```

### 5. 更新资产状态

- **URL**: `/assets/{id}/status`
- **Method**: `PATCH`
- **描述**: 更新资产状态
- **请求参数**:

```
{
  "status": "MAINTENANCE",
  "reason": "设备故障，送修中"
}
```

- **响应示例**:

```
{
  "code": 200,
  "message": "更新资产状态成功",
  "data": {
    "assetId": 121,
    "assetNo": "IT01IT2306010",
    "currentStatus": "MAINTENANCE",
    "statusChangeTime": "2023-06-10T15:20:30"
  }
}
```

### 6. 删除资产

- **URL**: `/assets/{id}`
- **Method**: `DELETE`
- **描述**: 删除指定资产
- **响应示例**:

```
{
  "code": 200,
  "message": "删除资产成功",
  "data": null
}
```

## 分类管理接口

### 1. 获取分类树结构

- **URL**: `/categories/tree`
- **Method**: `GET`
- **描述**: 获取完整的分类树结构
- **响应示例**:

```
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "name": "IT设备",
      "code": "IT",
      "level": 1,
      "children": [
        {
          "id": 3,
          "name": "服务器",
          "code": "SV",
          "level": 2,
          "children": [
            {
              "id": 5,
              "name": "机架式",
              "code": "RK",
              "level": 3,
              "children": []
            },
            {
              "id": 6,
              "name": "塔式",
              "code": "TW",
              "level": 3,
              "children": []
            }
          ]
        },
        {
          "id": 4,
          "name": "网络设备",
          "code": "NE",
          "level": 2,
          "children": [
            {
              "id": 7,
              "name": "交换机",
              "code": "SW",
              "level": 3,
              "children": []
            },
            {
              "id": 8,
              "name": "路由器",
              "code": "RT",
              "level": 3,
              "children": []
            }
          ]
        }
      ]
    },
    {
      "id": 2,
      "name": "办公设备",
      "code": "OE",
      "level": 1,
      "children": []
    }
  ]
}
```

### 2. 创建分类

- **URL**: `/categories`
- **Method**: `POST`
- **描述**: 创建新分类
- **请求参数**:

```
{
  "name": "云服务器",
  "code": "CS",
  "level": 2,
  "parentId": 1
}
```

- **响应示例**:

```
{
  "code": 200,
  "message": "创建分类成功",
  "data": {
    "id": 9,
    "name": "云服务器",
    "code": "CS",
    "level": 2,
    "parentId": 1
  }
}
```

### 3. 更新分类

- **URL**: `/categories/{id}`
- **Method**: `PUT`
- **描述**: 更新分类信息
- **请求参数**:

```
{
  "name": "云服务器(更新)",
  "code": "CS"
}
```

- **响应示例**:

```
{
  "code": 200,
  "message": "更新分类成功",
  "data": {
    "id": 9,
    "name": "云服务器(更新)",
    "code": "CS",
    "level": 2,
    "parentId": 1
  }
}
```

### 4. 删除分类

- **URL**: `/categories/{id}`
- **Method**: `DELETE`
- **描述**: 删除指定分类
- **响应示例**:

```
{
  "code": 200,
  "message": "删除分类成功",
  "data": null
}
```

## 空间管理接口

### 1. 获取资产空间历史

- **URL**: `/spaces/asset/{assetId}`
- **Method**: `GET`
- **描述**: 获取指定资产的空间变更历史
- **响应示例**:

```
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "spaceId": 101,
      "assetId": 1,
      "locationPath": "华东数据中心/A机房/3排机柜/U12",
      "dataCenter": "华东数据中心",
      "roomName": "A机房",
      "cabinetNo": "3排机柜",
      "uPosition": "U12",
      "environment": "生产环境",
      "keeper": "张三",
      "isCurrent": true,
      "validFrom": "2023-05-01T10:30:00",
      "validTo": null
    },
    {
      "spaceId": 100,
      "assetId": 1,
      "locationPath": "IT设备库房/待分配区",
      "dataCenter": null,
      "roomName": "IT设备库房",
      "cabinetNo": null,
      "uPosition": null,
      "environment": "库存环境",
      "keeper": "李四",
      "isCurrent": false,
      "validFrom": "2023-04-15T09:00:00",
      "validTo": "2023-05-01T10:29:59"
    }
  ]
}
```

### 2. 更新资产空间位置

- **URL**: `/spaces/asset/{assetId}`
- **Method**: `POST`
- **描述**: 更新资产空间位置
- **请求参数**:

```
{
  "dataCenter": "华东数据中心",
  "roomName": "B机房",
  "cabinetNo": "5排机柜",
  "uPosition": "U08",
  "environment": "测试环境",
  "keeper": "赵六"
}
```

- **响应示例**:

```
{
  "code": 200,
  "message": "更新空间位置成功",
  "data": {
    "spaceId": 102,
    "assetId": 1,
    "locationPath": "华东数据中心/B机房/5排机柜/U08",
    "isCurrent": true,
    "validFrom": "2023-06-15T14:20:00"
  }
}
```

### 3. 获取空间统计信息

- **URL**: `/spaces/statistics`
- **Method**: `GET`
- **描述**: 获取空间使用统计信息
- **响应示例**:

```
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "dataCenters": [
      {
        "name": "华东数据中心",
        "assetCount": 45,
        "rooms": [
          {
            "name": "A机房",
            "assetCount": 30
          },
          {
            "name": "B机房",
            "assetCount": 15
          }
        ]
      },
      {
        "name": "华南数据中心",
        "assetCount": 25,
        "rooms": [
          {
            "name": "主机房",
            "assetCount": 20
          },
          {
            "name": "IT设备库房",
            "assetCount": 5
          }
        ]
      }
    ]
  }
}
```

## 维保管理接口

### 1. 获取资产维保历史

- **URL**: `/warranties/asset/{assetId}`
- **Method**: `GET`
- **描述**: 获取指定资产的维保历史记录
- **响应示例**:

```
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "warrantyId": 101,
      "assetId": 1,
      "contractNo": "DL-2023-0501",
      "provider": "戴尔",
      "contactPerson": "戴尔客服",
      "contactPhone": "400-123-4567",
      "warrantyLevel": "7x24金牌服务",
      "startDate": "2023-05-01",
      "endDate": "2025-05-01",
      "isCurrent": true,
      "createdAt": "2023-05-01T10:30:00"
    }
  ]
}
```

### 2. 添加资产维保记录

- **URL**: `/warranties/asset/{assetId}`
- **Method**: `POST`
- **描述**: 添加资产新的维保记录
- **请求参数**:

```
{
  "contractNo": "DL-2023-0601",
  "provider": "戴尔",
  "contactPerson": "戴尔客服",
  "contactPhone": "400-123-4567",
  "warrantyLevel": "7x24白金服务",
  "startDate": "2025-05-02",
  "endDate": "2027-05-01"
}
```

- **响应示例**:

```
{
  "code": 200,
  "message": "添加维保记录成功",
  "data": {
    "warrantyId": 102,
    "assetId": 1,
    "contractNo": "DL-2023-0601",
    "startDate": "2025-05-02",
    "endDate": "2027-05-01",
    "isCurrent": false
  }
}
```

### 3. 获取即将过期维保列表

- **URL**: `/warranties/expiring`

- **Method**: `GET`

- **描述**: 获取即将过期的维保记录列表

- 请求参数

  : 

  - days: int (可选，默认30，表示多少天内过期)
  - page: int (当前页码，默认1)
  - size: int (每页条数，默认10)

- **响应示例**:

```
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "content": [
      {
        "warrantyId": 105,
        "assetId": 5,
        "assetNo": "IT01HR2302005",
        "assetName": "HP ProLiant DL380 Gen10服务器",
        "contractNo": "HP-2022-0210",
        "provider": "惠普",
        "warrantyLevel": "标准保修",
        "startDate": "2022-02-10",
        "endDate": "2023-07-10",
        "daysRemaining": 25
      }
    ],
    "totalElements": 8,
    "totalPages": 1,
    "size": 10,
    "number": 0
  }
}
```

## 数据导入导出接口

### 1. 导入资产数据

- **URL**: `/import/assets`

- **Method**: `POST`

- **Content-Type**: `multipart/form-data`

- **描述**: 通过Excel文件批量导入资产数据

- 请求参数

  : 

  - file: File (必填，Excel文件)
  - updateExisting: boolean (可选，是否更新已存在资产)

- **响应示例**:

```
{
  "code": 200,
  "message": "导入成功",
  "data": {
    "total": 100,
    "success": 95,
    "failed": 5,
    "batchId": "IMP20230615153020",
    "errors": [
      {
        "row": 5,
        "error": "分类编码不存在: XX99"
      },
      {
        "row": 23,
        "error": "资产名称不能为空"
      }
    ]
  }
}
```

### 2. 导出资产数据

- **URL**: `/export/assets`

- **Method**: `GET`

- **描述**: 导出资产数据为Excel文件

- 请求参数

  : 

  - query: string (可选，资产名称、编号筛选)
  - categoryId: number (可选，分类ID筛选)
  - status: string (可选，资产状态筛选)

- 响应

  : 

  - 直接下载Excel文件

### 3. 下载导入模板

- **URL**: `/import/template`

- **Method**: `GET`

- **描述**: 下载资产导入模板

- 响应

  : 

  - 直接下载Excel模板文件

## 通用响应格式

### 成功响应

```
{
  "code": 200,
  "message": "操作成功",
  "data": {
    // 具体数据内容
  }
}
```

### 错误响应

```
{
  "code": 400,
  "message": "请求参数错误",
  "data": null,
  "errors": [
    {
      "field": "username",
      "message": "用户名不能为空"
    }
  ]
}
```

## 错误码说明

| 错误码 | 描述           |
| ------ | -------------- |
| 200    | 操作成功       |
| 400    | 请求参数错误   |
| 401    | 未授权         |
| 403    | 权限不足       |
| 404    | 资源不存在     |
| 409    | 资源冲突       |
| 500    | 服务器内部错误 |