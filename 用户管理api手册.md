# ITEams 系统用户管理 API 手册

## 简介

本文档提供了ITEams系统中与用户管理相关的API接口说明，包括用户认证、用户信息管理、用户角色分配、权限管理和用户批量操作等功能。系统实现了用户登录失败次数限制和账户锁定功能，默认允许5次登录失败，超过后锁定账户30分钟。

## API响应格式说明

所有API接口的响应均采用统一的JSON格式结构，结构如下：

```json
{
  "success": boolean,  // 操作是否成功，true表示成功，false表示失败
  "code": "string",    // 业务状态码，成功固定为"200"，失败则使用自定义错误码
  "message": "string", // 提示消息，成功或失败的具体描述信息
  "data": any,         // 响应数据，成功时返回的业务数据，可能是对象、数组或null
  "timestamp": "long"  // 响应时间戳，记录响应生成的时间点（毫秒级时间戳）
}
```

状态码说明：
- `200`: 请求成功
- `400`: 请求参数错误
- `401`: 未授权或认证失败
- `403`: 权限不足
- `404`: 资源不存在
- `500`: 服务器内部错误

## 1. 认证 API

### 1.1 用户登录

- **URL**: `/api/auth/login`
- **方法**: POST
- **描述**: 用户登录并获取访问令牌
- **请求体**:
  ```json
  {
    "username": "string",
    "password": "string",
    "remember": boolean
  }
  ```
- **响应**:
  ```json
  {
    "success": true,
    "code": "200",
    "message": "登录成功",
    "data": {
      "token": "string",
      "userInfo": {
        "id": "long",
        "username": "string",
        "realName": "string",
        "email": "string",
        "phone": "string",
        "avatarUrl": "string",
        "lastLoginTime": "string",
        "roles": ["string"],
        "permissions": ["string"]
      }
    },
    "timestamp": "long"
  }
  ```

### 1.2 获取当前用户信息

- **URL**: `/api/auth/user`
- **方法**: GET
- **描述**: 获取当前登录用户的信息
- **响应**:
  ```json
  {
    "success": true,
    "code": "200",
    "message": "获取用户信息成功",
    "data": {
      "id": "long",
      "username": "string",
      "realName": "string",
      "email": "string",
      "phone": "string",
      "avatarUrl": "string",
      "lastLoginTime": "string",
      "roles": ["string"],
      "permissions": ["string"]
    },
    "timestamp": "long"
  }
  ```

### 1.3 退出登录

- **URL**: `/api/auth/logout`
- **方法**: POST
- **描述**: 用户退出登录
- **响应**:
  ```json
  {
    "success": true,
    "code": "200",
    "message": "登出成功",
    "data": null,
    "timestamp": "long"
  }
  ```

## 2. 用户管理 API

### 2.1 获取用户列表

- **URL**: `/api/users`
- **方法**: GET
- **描述**: 获取用户列表，支持分页和筛选
- **权限**: `ADMIN`, `SUPER_ADMIN`
- **请求参数**:
  - `username`: 用户名模糊查询（可选）
  - `realName`: 姓名模糊查询（可选）
  - `department`: 部门ID或名称模糊查询（可选）
  - `page`: 页码，默认0
  - `size`: 每页记录数，默认10
  - `sort`: 排序字段
- **响应**:
  ```json
  {
    "success": true,
    "code": "200",
    "message": "获取用户列表成功",
    "data": {
      "content": [
        {
          "id": "long",
          "username": "string",
          "realName": "string",
          "email": "string",
          "phone": "string",
          "departmentId": "long",
          "departmentName": "string",
          "groupIds": ["long"],
          "groupNames": ["string"],
          "status": "int",
          "roles": ["string"]
        }
      ],
      "totalElements": "long",
      "totalPages": "int",
      "size": "int",
      "number": "int",
      "first": "boolean",
      "last": "boolean",
      "empty": "boolean"
    },
    "timestamp": "long"
  }
  ```

### 2.2 获取用户详情

- **URL**: `/api/users/{id}`
- **方法**: GET
- **描述**: 获取指定用户的详细信息
- **权限**: `ADMIN`, `SUPER_ADMIN`
- **响应**:
  ```json
  {
    "success": true,
    "code": "200",
    "message": "获取用户详情成功",
    "data": {
      "id": "long",
      "username": "string",
      "realName": "string",
      "email": "string",
      "phone": "string",
      "departmentId": "long",
      "departmentName": "string",
      "groupIds": ["long"],
      "groupNames": ["string"],
      "status": "int",
      "roles": ["string"]
    },
    "timestamp": "long"
  }
  ```

### 2.3 创建用户

- **URL**: `/api/users`
- **方法**: POST
- **描述**: 创建新用户
- **权限**: `ADMIN`, `SUPER_ADMIN`
- **请求体**:
  ```json
  {
    "username": "string",
    "password": "string",
    "realName": "string",
    "email": "string",
    "phone": "string",
    "departmentId": "long",
    "groupIds": ["long"],
    "status": "int"
  }
  ```
- **响应**:
  ```json
  {
    "success": true,
    "code": "201",
    "message": "创建用户成功",
    "data": {
      "id": "long",
      "username": "string",
      "realName": "string",
      "email": "string",
      "phone": "string",
      "departmentId": "long",
      "departmentName": "string",
      "groupIds": ["long"],
      "groupNames": ["string"],
      "status": "int",
      "roles": ["string"]
    },
    "timestamp": "long"
  }
  ```

### 2.4 更新用户

- **URL**: `/api/users/{id}`
- **方法**: PUT
- **描述**: 更新用户信息
- **权限**: `ADMIN`, `SUPER_ADMIN`
- **请求体**:
  ```json
  {
    "realName": "string",
    "email": "string",
    "phone": "string",
    "departmentId": "long",
    "groupIds": ["long"],
    "status": "int"
  }
  ```
- **响应**:
  ```json
  {
    "success": true,
    "code": "200",
    "message": "更新用户成功",
    "data": {
      "id": "long",
      "username": "string",
      "realName": "string",
      "email": "string",
      "phone": "string",
      "departmentId": "long",
      "departmentName": "string",
      "groupIds": ["long"],
      "groupNames": ["string"],
      "status": "int",
      "roles": ["string"]
    },
    "timestamp": "long"
  }
  ```

### 2.5 删除用户

- **URL**: `/api/users/{id}`
- **方法**: DELETE
- **描述**: 删除指定用户
- **权限**: `ADMIN`, `SUPER_ADMIN`
- **响应**:
  ```json
  {
    "success": true,
    "code": "200",
    "message": "删除用户成功",
    "data": null,
    "timestamp": "long"
  }
  ```

### 2.6 重置用户密码

- **URL**: `/api/users/reset-password/{id}`
- **方法**: POST
- **描述**: 重置指定用户的密码
- **权限**: `ADMIN`, `SUPER_ADMIN`
- **响应**:
  ```json
  {
    "success": true,
    "code": "200",
    "message": "重置密码成功",
    "data": {
      "password": "string" // 随机生成的初始密码
    },
    "timestamp": "long"
  }
  ```

### 2.7 分配用户角色

- **URL**: `/api/users/{userId}/roles`
- **方法**: POST
- **描述**: 为指定用户分配角色
- **权限**: `SUPER_ADMIN`
- **请求体**:
  ```json
  [
    "long" // 角色ID列表
  ]
  ```
- **响应**:
  ```json
  {
    "success": true,
    "code": "200",
    "message": "分配角色成功",
    "data": null,
    "timestamp": "long"
  }
  ```

### 2.8 更新当前用户信息

- **URL**: `/api/users/profile`
- **方法**: PUT
- **描述**: 更新当前登录用户的个人信息
- **权限**: 所有已认证用户
- **请求体**:
  ```json
  {
    "realName": "string",
    "email": "string",
    "phone": "string"
  }
  ```
- **响应**:
  ```json
  {
    "success": true,
    "code": "200",
    "message": "更新个人信息成功",
    "data": {
      "id": "long",
      "username": "string",
      "realName": "string",
      "email": "string",
      "phone": "string",
      "departmentId": "long",
      "departmentName": "string",
      "groupIds": ["long"],
      "groupNames": ["string"]
    },
    "timestamp": "long"
  }
  ```

### 2.9 修改当前用户密码

- **URL**: `/api/users/change-password`
- **方法**: POST
- **描述**: 修改当前登录用户的密码
- **权限**: 所有已认证用户
- **请求体**:
  ```json
  {
    "oldPassword": "string",
    "newPassword": "string",
    "confirmPassword": "string"
  }
  ```
- **响应**:
  ```json
  {
    "success": true,
    "code": "200",
    "message": "密码修改成功",
    "data": null,
    "timestamp": "long"
  }
  ```

### 2.10 更新用户头像

- **URL**: `/api/users/{id}/avatar`
- **方法**: PUT
- **描述**: 更新指定用户的头像
- **权限**: `ADMIN`, `SUPER_ADMIN`
- **请求体**:
  ```json
  {
    "avatarUrl": "string"
  }
  ```
- **响应**:
  ```json
  {
    "success": true,
    "code": "200",
    "message": "更新头像成功",
    "data": {
      "id": "long",
      "username": "string",
      "realName": "string",
      "email": "string",
      "phone": "string",
      "departmentId": "long",
      "departmentName": "string",
      "groupIds": ["long"],
      "groupNames": ["string"],
      "status": "int",
      "roles": ["string"]
    },
    "timestamp": "long"
  }
  ```

### 2.11 更新当前用户头像

- **URL**: `/api/users/profile/avatar`
- **方法**: PUT
- **描述**: 更新当前登录用户的头像
- **权限**: 所有已认证用户
- **请求体**:
  ```json
  {
    "avatarUrl": "string"
  }
  ```
- **响应**:
  ```json
  {
    "success": true,
    "code": "200",
    "message": "更新头像成功",
    "data": {
      "id": "long",
      "username": "string",
      "realName": "string",
      "email": "string",
      "phone": "string",
      "departmentId": "long",
      "departmentName": "string",
      "groupIds": ["long"],
      "groupNames": ["string"],
      "status": "int"
    },
    "timestamp": "long"
  }
  ```

### 2.12 上传当前用户头像文件

- **URL**: `/api/users/profile/avatar/upload`
- **方法**: POST
- **描述**: 上传当前登录用户的头像文件
- **权限**: 所有已认证用户
- **请求体**: `multipart/form-data`
  - `file`: 头像文件
- **响应**:
  ```json
  {
    "success": true,
    "code": "200",
    "message": "头像上传成功",
    "data": {
      "id": "long",
      "username": "string",
      "realName": "string",
      "email": "string",
      "phone": "string",
      "departmentId": "long",
      "departmentName": "string",
      "groupIds": ["long"],
      "groupNames": ["string"],
      "status": "int"
    },
    "timestamp": "long"
  }
  ```

### 2.13 上传用户头像文件

- **URL**: `/api/users/{id}/avatar/upload`
- **方法**: POST
- **描述**: 上传指定用户的头像文件
- **权限**: `ADMIN`, `SUPER_ADMIN`
- **请求体**: `multipart/form-data`
  - `file`: 头像文件
- **响应**:
  ```json
  {
    "success": true,
    "code": "200",
    "message": "头像上传成功",
    "data": {
      "id": "long",
      "username": "string",
      "realName": "string",
      "email": "string",
      "phone": "string",
      "departmentId": "long",
      "departmentName": "string",
      "groupIds": ["long"],
      "groupNames": ["string"],
      "status": "int",
      "roles": ["string"]
    },
    "timestamp": "long"
  }
  ```

### 2.14 解锁用户账户

- **URL**: `/api/users/{userId}/unlock`
- **方法**: POST
- **描述**: 解锁被锁定的用户账户（当用户登录失败次数超过限制时，账户会被锁定）
- **权限**: `ADMIN`, `SUPER_ADMIN`
- **响应**:
  ```json
  {
    "success": true,
    "code": "200",
    "message": "用户账户解锁成功",
    "data": {
      "id": "long",
      "username": "string",
      "realName": "string",
      "email": "string",
      "phone": "string",
      "departmentId": "long",
      "departmentName": "string",
      "groupIds": ["long"],
      "groupNames": ["string"],
      "status": "int",
      "roles": ["string"]
    },
    "timestamp": "long"
  }
  ```

## 3. 用户批量操作 API

### 3.1 批量创建用户

- **URL**: `/api/users/batch`
- **方法**: POST
- **描述**: 批量创建新用户
- **权限**: `ADMIN`, `SUPER_ADMIN`
- **请求体**:
  ```json
  [
    {
      "username": "string",
      "password": "string",
      "realName": "string",
      "email": "string",
      "phone": "string",
      "departmentId": "long",
      "groupIds": ["long"],
      "status": "int"
    }
  ]
  ```
- **响应**:
  ```json
  {
    "success": true,
    "code": "200",
    "message": "批量创建用户成功",
    "data": [
      {
        "id": "long",
        "username": "string",
        "realName": "string",
        "email": "string",
        "phone": "string",
        "departmentId": "long",
        "departmentName": "string",
        "groupIds": ["long"],
        "groupNames": ["string"],
        "status": "int",
        "roles": ["string"]
      }
    ],
    "timestamp": "long"
  }
  ```

### 3.2 批量删除用户

- **URL**: `/api/users/batch`
- **方法**: DELETE
- **描述**: 批量删除用户
- **权限**: `ADMIN`, `SUPER_ADMIN`
- **请求体**:
  ```json
  [
    "long" // 用户ID列表
  ]
  ```
- **响应**:
  ```json
  {
    "success": true,
    "code": "200",
    "message": "批量删除用户成功",
    "data": {
      "count": "int" // 成功删除的用户数量
    },
    "timestamp": "long"
  }
  ```

### 3.3 批量启用用户

- **URL**: `/api/users/batch/enable`
- **方法**: POST
- **描述**: 批量启用用户
- **权限**: `ADMIN`, `SUPER_ADMIN`
- **请求体**:
  ```json
  [
    "long" // 用户ID列表
  ]
  ```
- **响应**:
  ```json
  {
    "success": true,
    "code": "200",
    "message": "批量启用用户成功",
    "data": {
      "count": "int" // 成功启用的用户数量
    },
    "timestamp": "long"
  }
  ```

### 3.4 批量禁用用户

- **URL**: `/api/users/batch/disable`
- **方法**: POST
- **描述**: 批量禁用用户
- **权限**: `ADMIN`, `SUPER_ADMIN`
- **请求体**:
  ```json
  [
    "long" // 用户ID列表
  ]
  ```
- **响应**:
  ```json
  {
    "success": true,
    "code": "200",
    "message": "批量禁用用户成功",
    "data": {
      "count": "int" // 成功禁用的用户数量
    },
    "timestamp": "long"
  }
  ```

### 3.5 批量分配角色

- **URL**: `/api/users/batch/roles`
- **方法**: POST
- **描述**: 批量为用户分配角色
- **权限**: `SUPER_ADMIN`
- **请求体**:
  ```json
  {
    "userIds": ["long"], // 用户ID列表
    "roleIds": ["long"]  // 角色ID列表
  }
  ```
- **响应**:
  ```json
  {
    "success": true,
    "code": "200",
    "message": "批量分配角色成功",
    "data": {
      "count": "int" // 成功分配角色的用户数量
    },
    "timestamp": "long"
  }
  ```

### 3.6 批量解锁用户账户

- **URL**: `/api/users/batch/unlock`
- **方法**: POST
- **描述**: 批量解锁被锁定的用户账户
- **权限**: `ADMIN`, `SUPER_ADMIN`
- **请求体**:
  ```json
  {
    "userIds": ["long"] // 用户ID列表
  }
  ```
- **响应**:
  ```json
  {
    "success": true,
    "code": "200",
    "message": "批量解锁用户账户成功",
    "data": {
      "count": "int" // 成功解锁的用户数量
    },
    "timestamp": "long"
  }
  ```

## 4. 角色管理 API

### 4.1 获取角色列表

- **URL**: `/api/roles`
- **方法**: GET
- **描述**: 获取角色列表，支持分页和筛选
- **权限**: `SUPER_ADMIN`
- **请求参数**:
  - `name`: 角色名称模糊查询（可选）
  - `page`: 页码，默认0
  - `size`: 每页记录数，默认10
  - `sort`: 排序字段
- **响应**:
  ```json
  {
    "success": true,
    "code": "200",
    "message": "获取角色列表成功",
    "data": {
      "content": [
        {
          "id": "long",
          "name": "string",
          "code": "string",
          "description": "string"
        }
      ],
      "totalElements": "long",
      "totalPages": "int",
      "size": "int",
      "number": "int",
      "first": "boolean",
      "last": "boolean",
      "empty": "boolean"
    },
    "timestamp": "long"
  }
  ```

### 4.2 获取所有角色

- **URL**: `/api/roles/all`
- **方法**: GET
- **描述**: 获取所有角色，不分页
- **权限**: `SUPER_ADMIN`
- **响应**:
  ```json
  {
    "success": true,
    "code": "200",
    "message": "获取角色列表成功",
    "data": [
      {
        "id": "long",
        "name": "string",
        "code": "string",
        "description": "string"
      }
    ],
    "timestamp": "long"
  }
  ```

### 4.3 获取角色详情

- **URL**: `/api/roles/{id}`
- **方法**: GET
- **描述**: 获取指定角色的详细信息
- **权限**: `SUPER_ADMIN`
- **响应**:
  ```json
  {
    "success": true,
    "code": "200",
    "message": "获取角色详情成功",
    "data": {
      "id": "long",
      "name": "string",
      "code": "string",
      "description": "string"
    },
    "timestamp": "long"
  }
  ```

### 4.4 创建角色

- **URL**: `/api/roles`
- **方法**: POST
- **描述**: 创建新角色
- **权限**: `SUPER_ADMIN`
- **请求体**:
  ```json
  {
    "name": "string",
    "code": "string",
    "description": "string"
  }
  ```
- **响应**:
  ```json
  {
    "success": true,
    "code": "201",
    "message": "创建角色成功",
    "data": {
      "id": "long",
      "name": "string",
      "code": "string",
      "description": "string"
    },
    "timestamp": "long"
  }
  ```

### 4.5 更新角色

- **URL**: `/api/roles/{id}`
- **方法**: PUT
- **描述**: 更新角色信息
- **权限**: `SUPER_ADMIN`
- **请求体**:
  ```json
  {
    "name": "string",
    "code": "string",
    "description": "string"
  }
  ```
- **响应**:
  ```json
  {
    "success": true,
    "code": "200",
    "message": "更新角色成功",
    "data": {
      "id": "long",
      "name": "string",
      "code": "string",
      "description": "string"
    },
    "timestamp": "long"
  }
  ```

### 4.6 删除角色

- **URL**: `/api/roles/{id}`
- **方法**: DELETE
- **描述**: 删除指定角色
- **权限**: `SUPER_ADMIN`
- **响应**:
  ```json
  {
    "success": true,
    "code": "200",
    "message": "删除角色成功",
    "data": null,
    "timestamp": "long"
  }
  ```

### 4.7 获取角色权限

- **URL**: `/api/roles/{id}/permissions`
- **方法**: GET
- **描述**: 获取指定角色的权限编码列表
- **权限**: `SUPER_ADMIN`
- **响应**:
  ```json
  {
    "success": true,
    "code": "200",
    "message": "获取角色权限成功",
    "data": ["string"], // 权限编码列表
    "timestamp": "long"
  }
  ```

### 4.8 分配角色权限

- **URL**: `/api/roles/{id}/permissions`
- **方法**: POST
- **描述**: 为指定角色分配权限
- **权限**: `SUPER_ADMIN`
- **请求体**:
  ```json
  [
    "long" // 权限ID列表
  ]
  ```
- **响应**:
  ```json
  {
    "success": true,
    "code": "200",
    "message": "分配权限成功",
    "data": null,
    "timestamp": "long"
  }
  ```

### 4.9 获取用户角色

- **URL**: `/api/roles/user/{userId}`
- **方法**: GET
- **描述**: 获取指定用户的角色编码列表
- **权限**: `SUPER_ADMIN`
- **响应**:
  ```json
  {
    "success": true,
    "code": "200",
    "message": "获取用户角色成功",
    "data": ["string"], // 角色编码列表
    "timestamp": "long"
  }
  ```

## 5. 权限管理 API

### 5.1 获取所有权限

- **URL**: `/api/permissions`
- **方法**: GET
- **描述**: 获取所有权限列表
- **权限**: `SUPER_ADMIN`
- **响应**:
  ```json
  {
    "success": true,
    "code": "200",
    "message": "获取权限列表成功",
    "data": [
      {
        "id": "long",
        "name": "string",
        "code": "string",
        "description": "string"
      }
    ],
    "timestamp": "long"
  }
  ```

### 5.2 获取权限详情

- **URL**: `/api/permissions/{id}`
- **方法**: GET
- **描述**: 获取指定权限的详细信息
- **权限**: `SUPER_ADMIN`
- **响应**:
  ```json
  {
    "success": true,
    "code": "200",
    "message": "获取权限详情成功",
    "data": {
      "id": "long",
      "name": "string",
      "code": "string",
      "description": "string"
    },
    "timestamp": "long"
  }
  ```

### 5.3 创建权限

- **URL**: `/api/permissions`
- **方法**: POST
- **描述**: 创建新权限
- **权限**: `SUPER_ADMIN`
- **请求体**:
  ```json
  {
    "name": "string",
    "code": "string",
    "description": "string"
  }
  ```
- **响应**:
  ```json
  {
    "success": true,
    "code": "201",
    "message": "创建权限成功",
    "data": {
      "id": "long",
      "name": "string",
      "code": "string",
      "description": "string"
    },
    "timestamp": "long"
  }
  ```

### 5.4 更新权限

- **URL**: `/api/permissions/{id}`
- **方法**: PUT
- **描述**: 更新权限信息
- **权限**: `SUPER_ADMIN`
- **请求体**:
  ```json
  {
    "name": "string",
    "code": "string",
    "description": "string"
  }
  ```
- **响应**:
  ```json
  {
    "success": true,
    "code": "200",
    "message": "更新权限成功",
    "data": {
      "id": "long",
      "name": "string",
      "code": "string",
      "description": "string"
    },
    "timestamp": "long"
  }
  ```

### 5.5 删除权限

- **URL**: `/api/permissions/{id}`
- **方法**: DELETE
- **描述**: 删除指定权限
- **权限**: `SUPER_ADMIN`
- **响应**:
  ```json
  {
    "success": true,
    "code": "200",
    "message": "删除权限成功",
    "data": null,
    "timestamp": "long"
  }
  ```

### 5.6 获取角色权限

- **URL**: `/api/permissions/role/{roleId}`
- **方法**: GET
- **描述**: 获取指定角色的权限列表
- **权限**: `SUPER_ADMIN`
- **响应**:
  ```json
  {
    "success": true,
    "code": "200",
    "message": "获取角色权限成功",
    "data": [
      {
        "id": "long",
        "name": "string",
        "code": "string",
        "description": "string"
      }
    ],
    "timestamp": "long"
  }
  ```

### 5.7 获取用户权限

- **URL**: `/api/permissions/user/{userId}`
- **方法**: GET
- **描述**: 获取指定用户的权限列表
- **权限**: `SUPER_ADMIN`
- **响应**:
  ```json
  {
    "success": true,
    "code": "200",
    "message": "获取用户权限成功",
    "data": [
      {
        "id": "long",
        "name": "string",
        "code": "string",
        "description": "string"
      }
    ],
    "timestamp": "long"
  }
  ```

### 5.8 获取用户权限编码

- **URL**: `/api/permissions/user/{userId}/codes`
- **方法**: GET
- **描述**: 获取指定用户的权限编码列表
- **权限**: `SUPER_ADMIN`
- **响应**:
  ```json
  {
    "success": true,
    "code": "200",
    "message": "获取用户权限编码成功",
    "data": ["string"], // 权限编码列表
    "timestamp": "long"
  }
  ```

## 6. 部门管理 API

### 6.1 获取部门列表

- **URL**: `/api/departments`
- **方法**: GET
- **描述**: 获取部门列表，支持分页和条件查询
- **权限**: `ADMIN`, `SUPER_ADMIN`
- **请求参数**:
  - `name`: 部门名称（模糊查询，可选）
  - `code`: 部门编码（可选）
  - `enabled`: 是否启用（可选）
  - `page`: 页码（默认0）
  - `size`: 每页条数（默认10）
- **响应**:
  ```json
  {
    "success": true,
    "code": "200",
    "message": "获取部门列表成功",
    "data": {
      "content": [
        {
          "id": "long",
          "name": "string",
          "code": "string",
          "description": "string",
          "enabled": "boolean",
          "parentId": "long",
          "path": "string",
          "createdTime": "string",
          "updatedTime": "string"
        }
      ],
      "totalElements": "long",
      "totalPages": "int",
      "size": "int",
      "number": "int",
      "first": "boolean",
      "last": "boolean",
      "empty": "boolean"
    },
    "timestamp": "long"
  }
  ```

### 6.2 获取所有部门（不分页）

- **URL**: `/api/departments/all`
- **方法**: GET
- **描述**: 获取所有部门列表，不分页
- **权限**: `ADMIN`, `SUPER_ADMIN`
- **请求参数**:
  - `enabled`: 是否启用（可选）
- **响应**:
  ```json
  {
    "success": true,
    "code": "200",
    "message": "获取部门列表成功",
    "data": [
      {
        "id": "long",
        "name": "string",
        "code": "string",
        "description": "string",
        "enabled": "boolean",
        "parentId": "long",
        "path": "string",
        "createdTime": "string",
        "updatedTime": "string"
      }
    ],
    "timestamp": "long"
  }
  ```

### 6.3 获取部门详情

- **URL**: `/api/departments/{id}`
- **方法**: GET
- **描述**: 获取指定部门的详细信息
- **权限**: `ADMIN`, `SUPER_ADMIN`
- **响应**:
  ```json
  {
    "success": true,
    "code": "200",
    "message": "获取部门详情成功",
    "data": {
      "id": "long",
      "name": "string",
      "code": "string",
      "description": "string",
      "enabled": "boolean",
      "parentId": "long",
      "path": "string",
      "createdTime": "string",
      "updatedTime": "string"
    },
    "timestamp": "long"
  }
  ```

### 6.4 创建部门

- **URL**: `/api/departments`
- **方法**: POST
- **描述**: 创建新部门
- **权限**: `ADMIN`, `SUPER_ADMIN`
- **请求体**:
  ```json
  {
    "name": "string",
    "code": "string",
    "description": "string",
    "enabled": "boolean",
    "parentId": "long"
  }
  ```
- **响应**:
  ```json
  {
    "success": true,
    "code": "200",
    "message": "创建部门成功",
    "data": {
      "id": "long",
      "name": "string",
      "code": "string",
      "description": "string",
      "enabled": "boolean",
      "parentId": "long",
      "path": "string",
      "createdTime": "string",
      "updatedTime": "string"
    },
    "timestamp": "long"
  }
  ```

### 6.5 更新部门

- **URL**: `/api/departments/{id}`
- **方法**: PUT
- **描述**: 更新指定部门的信息
- **权限**: `ADMIN`, `SUPER_ADMIN`
- **请求体**:
  ```json
  {
    "name": "string",
    "code": "string",
    "description": "string",
    "enabled": "boolean",
    "parentId": "long"
  }
  ```
- **响应**:
  ```json
  {
    "success": true,
    "code": "200",
    "message": "更新部门成功",
    "data": {
      "id": "long",
      "name": "string",
      "code": "string",
      "description": "string",
      "enabled": "boolean",
      "parentId": "long",
      "path": "string",
      "createdTime": "string",
      "updatedTime": "string"
    },
    "timestamp": "long"
  }
  ```

### 6.6 删除部门

- **URL**: `/api/departments/{id}`
- **方法**: DELETE
- **描述**: 删除指定部门
- **权限**: `ADMIN`, `SUPER_ADMIN`
- **响应**:
  ```json
  {
    "success": true,
    "code": "200",
    "message": "删除部门成功",
    "data": null,
    "timestamp": "long"
  }
  ```

### 6.7 获取部门用户

- **URL**: `/api/departments/{id}/users`
- **方法**: GET
- **描述**: 获取指定部门的用户列表
- **权限**: `ADMIN`, `SUPER_ADMIN`
- **请求参数**:
  - `page`: 页码（默认0）
  - `size`: 每页条数（默认10）
- **响应**:
  ```json
  {
    "success": true,
    "code": "200",
    "message": "获取部门用户成功",
    "data": {
      "content": [
        {
          "id": "long",
          "username": "string",
          "realName": "string",
          "email": "string",
          "phone": "string",
          "departmentId": "long",
          "departmentName": "string",
          "status": "int"
        }
      ],
      "totalElements": "long",
      "totalPages": "int",
      "size": "int",
      "number": "int",
      "first": "boolean",
      "last": "boolean",
      "empty": "boolean"
    },
    "timestamp": "long"
  }
  ```

## 7. 用户组管理 API

### 7.1 获取用户组列表

- **URL**: `/api/groups`
- **方法**: GET
- **描述**: 获取用户组列表，支持分页和条件查询
- **权限**: `ADMIN`, `SUPER_ADMIN`
- **请求参数**:
  - `name`: 用户组名称（模糊查询，可选）
  - `code`: 用户组编码（可选）
  - `enabled`: 是否启用（可选）
  - `page`: 页码（默认0）
  - `size`: 每页条数（默认10）
- **响应**:
  ```json
  {
    "success": true,
    "code": "200",
    "message": "获取用户组列表成功",
    "data": {
      "content": [
        {
          "id": "long",
          "name": "string",
          "code": "string",
          "description": "string",
          "enabled": "boolean",
          "createdTime": "string",
          "updatedTime": "string"
        }
      ],
      "pageable": {
        "pageNumber": "int",
        "pageSize": "int",
        "sort": {
          "sorted": "boolean",
          "unsorted": "boolean",
          "empty": "boolean"
        },
        "offset": "long",
        "paged": "boolean",
        "unpaged": "boolean"
      },
      "totalPages": "int",
      "totalElements": "long",
      "last": "boolean",
      "size": "int",
      "number": "int",
      "sort": {
        "sorted": "boolean",
        "unsorted": "boolean",
        "empty": "boolean"
      },
      "numberOfElements": "int",
      "first": "boolean",
      "empty": "boolean"
    },
    "timestamp": "long"
  }
  ```

### 7.2 获取所有用户组（不分页）

- **URL**: `/api/groups/all`
- **方法**: GET
- **描述**: 获取所有用户组列表，不分页
- **权限**: `ADMIN`, `SUPER_ADMIN`
- **请求参数**:
  - `enabled`: 是否启用（可选）
- **响应**:
  ```json
  {
    "success": true,
    "code": "200",
    "message": "获取用户组列表成功",
    "data": [
      {
        "id": "long",
        "name": "string",
        "code": "string",
        "description": "string",
        "enabled": "boolean",
        "createdTime": "string",
        "updatedTime": "string"
      }
    ],
    "timestamp": "long"
  }
  ```

### 7.3 获取用户组详情

- **URL**: `/api/groups/{id}`
- **方法**: GET
- **描述**: 获取指定用户组的详细信息
- **权限**: `ADMIN`, `SUPER_ADMIN`
- **响应**:
  ```json
  {
    "success": true,
    "code": "200",
    "message": "获取用户组详情成功",
    "data": {
      "id": "long",
      "name": "string",
      "code": "string",
      "description": "string",
      "enabled": "boolean",
      "createdTime": "string",
      "updatedTime": "string"
    },
    "timestamp": "long"
  }
  ```

### 7.4 创建用户组

- **URL**: `/api/groups`
- **方法**: POST
- **描述**: 创建新用户组
- **权限**: `ADMIN`, `SUPER_ADMIN`
- **请求体**:
  ```json
  {
    "name": "string",
    "code": "string",
    "description": "string",
    "enabled": "boolean"
  }
  ```
- **响应**:
  ```json
  {
    "success": true,
    "code": "200",
    "message": "创建用户组成功",
    "data": {
      "id": "long",
      "name": "string",
      "code": "string",
      "description": "string",
      "enabled": "boolean",
      "createdTime": "string",
      "updatedTime": "string"
    },
    "timestamp": "long"
  }
  ```

### 7.5 更新用户组

- **URL**: `/api/groups/{id}`
- **方法**: PUT
- **描述**: 更新指定用户组的信息
- **权限**: `ADMIN`, `SUPER_ADMIN`
- **请求体**:
  ```json
  {
    "name": "string",
    "code": "string",
    "description": "string",
    "enabled": "boolean"
  }
  ```
- **响应**:
  ```json
  {
    "success": true,
    "code": "200",
    "message": "更新用户组成功",
    "data": {
      "id": "long",
      "name": "string",
      "code": "string",
      "description": "string",
      "enabled": "boolean",
      "createdTime": "string",
      "updatedTime": "string"
    },
    "timestamp": "long"
  }
  ```

### 7.6 删除用户组

- **URL**: `/api/groups/{id}`
- **方法**: DELETE
- **描述**: 删除指定用户组
- **权限**: `ADMIN`, `SUPER_ADMIN`
- **响应**:
  ```json
  {
    "success": true,
    "code": "200",
    "message": "删除用户组成功",
    "data": null,
    "timestamp": "long"
  }
  ```

### 7.7 获取用户组用户

- **URL**: `/api/groups/{id}/users`
- **方法**: GET
- **描述**: 获取指定用户组的用户列表
- **权限**: `ADMIN`, `SUPER_ADMIN`
- **请求参数**:
  - `page`: 页码（默认0）
  - `size`: 每页条数（默认10）
- **响应**:
  ```json
  {
    "success": true,
    "code": "200",
    "message": "获取用户组用户成功",
    "data": {
      "content": [
        {
          "id": "long",
          "username": "string",
          "realName": "string",
          "email": "string",
          "phone": "string",
          "department": "string",
          "enabled": "boolean",
          "createdTime": "string",
          "updatedTime": "string"
        }
      ],
      "pageable": {
        "pageNumber": "int",
        "pageSize": "int"
      },
      "totalPages": "int",
      "totalElements": "long"
    },
    "timestamp": "long"
  }
  ```

### 7.8 添加用户到用户组

- **URL**: `/api/groups/{id}/users`
- **方法**: POST
- **描述**: 添加用户到指定用户组
- **权限**: `ADMIN`, `SUPER_ADMIN`
- **请求体**:
  ```json
  [
    "long" // 用户ID列表
  ]
  ```
- **响应**:
  ```json
  {
    "success": true,
    "code": "200",
    "message": "添加用户到用户组成功",
    "data": {
      "id": "long",
      "name": "string",
      "code": "string",
      "description": "string",
      "enabled": "boolean",
      "createdTime": "string",
      "updatedTime": "string"
    },
    "timestamp": "long"
  }
  ```

### 7.9 从用户组移除用户

- **URL**: `/api/groups/{id}/users`
- **方法**: DELETE
- **描述**: 从指定用户组移除用户
- **权限**: `ADMIN`, `SUPER_ADMIN`
- **请求体**:
  ```json
  [
    "long" // 用户ID列表
  ]
  ```
- **响应**:
  ```json
  {
    "success": true,
    "code": "200",
    "message": "从用户组移除用户成功",
    "data": {
      "id": "long",
      "name": "string",
      "code": "string",
      "description": "string",
      "enabled": "boolean",
      "createdTime": "string",
      "updatedTime": "string"
    },
    "timestamp": "long"
  }
  ```

## 8. 总结

所有API响应均包含以下字段：

- `success`: 表示请求是否成功，类型为boolean
- `code`: 状态码，类型为string
- `message`: 响应消息，类型为string
- `data`: 响应数据，类型根据API不同而不同
- `timestamp`: 响应时间戳，类型为long（毫秒级Unix时间戳）

本文档提供了ITEams系统中与用户管理相关的API接口说明，包括用户认证、用户信息管理、用户批量操作、角色管理、权限管理、部门管理和用户组管理等功能。系统实现了用户登录失败次数限制和账户锁定功能，默认允许5次登录失败，超过后锁定账户30分钟。如有任何问题，请联系系统管理员。
