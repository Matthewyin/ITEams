
# 中文

# ITEams 系统用户管理 API 手册

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
    "code": 200,
    "message": "登录成功",
    "data": {
      "token": "string",
      "userInfo": {
        "id": "long",
        "username": "string",
        "realName": "string",
        "email": "string",
        "department": "string",
        "roles": ["string"],
        "permissions": ["string"]
      }
    }
  }
  ```

### 1.2 获取当前用户信息

- **URL**: `/api/auth/user`
- **方法**: GET
- **描述**: 获取当前登录用户的信息
- **响应**:
  ```json
  {
    "code": 200,
    "message": "获取用户信息成功",
    "data": {
      "id": "long",
      "username": "string",
      "realName": "string",
      "email": "string",
      "department": "string",
      "roles": ["string"],
      "permissions": ["string"]
    }
  }
  ```

### 1.3 退出登录

- **URL**: `/api/auth/logout`
- **方法**: POST
- **描述**: 用户退出登录
- **响应**:
  ```json
  {
    "code": 200,
    "message": "登出成功",
    "data": null
  }
  ```

## 2. 用户管理 API

### 2.1 获取用户列表

- **URL**: `/api/users`
- **方法**: GET
- **描述**: 获取用户列表，支持分页和筛选
- **权限**: 管理员、超级管理员
- **请求参数**:
  - `page`: 页码，默认0
  - `size`: 每页记录数，默认10
  - `sort`: 排序字段，默认id
  - `username`: 用户名模糊查询
  - `realName`: 姓名模糊查询
  - `department`: 部门模糊查询
- **响应**:
  ```json
  {
    "code": 200,
    "message": "获取用户列表成功",
    "data": {
      "content": [
        {
          "id": "long",
          "username": "string",
          "realName": "string",
          "email": "string",
          "department": "string",
          "roles": ["string"]
        }
      ],
      "totalElements": "long",
      "totalPages": "int",
      "size": "int",
      "number": "int"
    }
  }
  ```

### 2.2 获取用户详情

- **URL**: `/api/users/{id}`
- **方法**: GET
- **描述**: 获取指定用户的详细信息
- **权限**: 管理员、超级管理员
- **响应**:
  ```json
  {
    "code": 200,
    "message": "获取用户详情成功",
    "data": {
      "id": "long",
      "username": "string",
      "realName": "string",
      "email": "string",
      "department": "string",
      "roles": ["string"]
    }
  }
  ```

### 2.3 创建用户

- **URL**: `/api/users`
- **方法**: POST
- **描述**: 创建新用户，默认角色为普通用户
- **权限**: 管理员、超级管理员
- **请求体**:
  ```json
  {
    "username": "string",
    "password": "string",
    "realName": "string",
    "email": "string",
    "department": "string"
  }
  ```
- **响应**:
  ```json
  {
    "code": 201,
    "message": "创建用户成功",
    "data": {
      "id": "long",
      "username": "string",
      "realName": "string",
      "email": "string",
      "department": "string",
      "roles": ["string"]
    }
  }
  ```

### 2.4 更新用户

- **URL**: `/api/users/{id}`
- **方法**: PUT
- **描述**: 更新用户信息
- **权限**: 管理员、超级管理员
- **请求体**:
  ```json
  {
    "realName": "string",
    "email": "string",
    "department": "string"
  }
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "message": "更新用户成功",
    "data": {
      "id": "long",
      "username": "string",
      "realName": "string",
      "email": "string",
      "department": "string",
      "roles": ["string"]
    }
  }
  ```

### 2.5 删除用户

- **URL**: `/api/users/{id}`
- **方法**: DELETE
- **描述**: 删除指定用户
- **权限**: 管理员、超级管理员
- **响应**:
  ```json
  {
    "code": 200,
    "message": "删除用户成功",
    "data": null
  }
  ```

### 2.6 重置用户密码

- **URL**: `/api/users/reset-password/{id}`
- **方法**: POST
- **描述**: 重置指定用户的密码
- **权限**: 管理员、超级管理员
- **响应**:
  ```json
  {
    "code": 200,
    "message": "重置密码成功",
    "data": {
      "password": "string" // 随机生成的初始密码
    }
  }
  ```

## 3. 角色管理 API

### 3.1 获取所有角色

- **URL**: `/api/roles/all`
- **方法**: GET
- **描述**: 获取所有角色，不分页
- **权限**: 超级管理员
- **响应**:
  ```json
  {
    "code": 200,
    "message": "获取角色列表成功",
    "data": [
      {
        "id": "long",
        "name": "string",
        "code": "string",
        "description": "string"
      }
    ]
  }
  ```

### 3.2 获取角色列表

- **URL**: `/api/roles`
- **方法**: GET
- **描述**: 获取角色列表，支持分页和筛选
- **权限**: 超级管理员
- **请求参数**:
  - `page`: 页码，默认0
  - `size`: 每页记录数，默认10
  - `sort`: 排序字段，默认id
  - `name`: 角色名称模糊查询
- **响应**:
  ```json
  {
    "code": 200,
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
      "number": "int"
    }
  }
  ```

### 3.3 获取角色详情

- **URL**: `/api/roles/{id}`
- **方法**: GET
- **描述**: 获取指定角色的详细信息
- **权限**: 超级管理员
- **响应**:
  ```json
  {
    "code": 200,
    "message": "获取角色详情成功",
    "data": {
      "id": "long",
      "name": "string",
      "code": "string",
      "description": "string"
    }
  }
  ```

### 3.4 创建角色

- **URL**: `/api/roles`
- **方法**: POST
- **描述**: 创建新角色
- **权限**: 超级管理员
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
    "code": 201,
    "message": "创建角色成功",
    "data": {
      "id": "long",
      "name": "string",
      "code": "string",
      "description": "string"
    }
  }
  ```

### 3.5 分配用户角色

- **URL**: `/api/roles/user/{userId}`
- **方法**: POST
- **描述**: 为用户分配角色
- **权限**: 超级管理员
- **请求体**:
  ```json
  [
    "long" // 角色ID列表
  ]
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "message": "分配角色成功",
    "data": null
  }
  ```

## 4. 自助服务 API

### 4.1 更新个人信息

- **URL**: `/api/users/profile`
- **方法**: PUT
- **描述**: 更新当前用户个人信息
- **权限**: 所有已认证用户
- **请求体**:
  ```json
  {
    "realName": "string",
    "email": "string",
    "department": "string"
  }
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "message": "更新个人信息成功",
    "data": {
      "id": "long",
      "username": "string",
      "realName": "string",
      "email": "string",
      "department": "string"
    }
  }
  ```

### 4.2 修改密码

- **URL**: `/api/users/change-password`
- **方法**: POST
- **描述**: 修改当前用户密码
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
    "code": 200,
    "message": "密码修改成功",
    "data": null
  }
  ```
