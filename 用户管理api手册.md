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
        "phone": "string",
        "avatarUrl": "string",
        "lastLoginTime": "string",
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
      "phone": "string",
      "avatarUrl": "string",
      "lastLoginTime": "string",
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
    "code": 200,
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
      "number": "int"
    }
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
    "code": 200,
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
    }
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
    "code": 201,
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
    }
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
    "code": 200,
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
    }
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
    "code": 200,
    "message": "删除用户成功",
    "data": null
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
    "code": 200,
    "message": "重置密码成功",
    "data": {
      "password": "string" // 随机生成的初始密码
    }
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
    "code": 200,
    "message": "分配角色成功",
    "data": null
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
    "code": 200,
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
    }
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
    "code": 200,
    "message": "密码修改成功",
    "data": null
  }
  ```

## 3. 角色管理 API

### 3.1 获取角色列表

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

### 3.2 获取所有角色

- **URL**: `/api/roles/all`
- **方法**: GET
- **描述**: 获取所有角色，不分页
- **权限**: `SUPER_ADMIN`
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

### 3.3 获取角色详情

- **URL**: `/api/roles/{id}`
- **方法**: GET
- **描述**: 获取指定角色的详细信息
- **权限**: `SUPER_ADMIN`
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

### 3.5 更新角色

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
    "code": 200,
    "message": "更新角色成功",
    "data": {
      "id": "long",
      "name": "string",
      "code": "string",
      "description": "string"
    }
  }
  ```

### 3.6 删除角色

- **URL**: `/api/roles/{id}`
- **方法**: DELETE
- **描述**: 删除指定角色
- **权限**: `SUPER_ADMIN`
- **响应**:
  ```json
  {
    "code": 200,
    "message": "删除角色成功",
    "data": null
  }
  ```

### 3.7 获取角色权限

- **URL**: `/api/roles/{id}/permissions`
- **方法**: GET
- **描述**: 获取指定角色的权限编码列表
- **权限**: `SUPER_ADMIN`
- **响应**:
  ```json
  {
    "code": 200,
    "message": "获取角色权限成功",
    "data": ["string"] // 权限编码列表
  }
  ```

### 3.8 分配角色权限

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
    "code": 200,
    "message": "分配权限成功",
    "data": null
  }
  ```

### 3.9 获取用户角色

- **URL**: `/api/roles/user/{userId}`
- **方法**: GET
- **描述**: 获取指定用户的角色编码列表
- **权限**: `SUPER_ADMIN`
- **响应**:
  ```json
  {
    "code": 200,
    "message": "获取用户角色成功",
    "data": ["string"] // 角色编码列表
  }
  ```

## 5. 部门管理 API

### 5.1 获取部门列表

- **URL**: `/api/departments`
- **方法**: GET
- **描述**: 获取部门列表，支持分页和筛选
- **权限**: `ADMIN`, `SUPER_ADMIN`
- **请求参数**:
  - `name`: 部门名称模糊查询（可选）
  - `page`: 页码，默认0
  - `size`: 每页记录数，默认10
  - `sort`: 排序字段
- **响应**:
  ```json
  {
    "code": 200,
    "message": "获取部门列表成功",
    "data": {
      "content": [
        {
          "id": "long",
          "name": "string",
          "description": "string",
          "userCount": "int"
        }
      ],
      "totalElements": "long",
      "totalPages": "int",
      "size": "int",
      "number": "int"
    }
  }
  ```

### 5.2 获取所有部门

- **URL**: `/api/departments/all`
- **方法**: GET
- **描述**: 获取所有部门，不分页
- **权限**: `ADMIN`, `SUPER_ADMIN`
- **响应**:
  ```json
  {
    "code": 200,
    "message": "获取部门列表成功",
    "data": [
      {
        "id": "long",
        "name": "string",
        "description": "string",
        "userCount": "int"
      }
    ]
  }
  ```

### 5.3 获取部门详情

- **URL**: `/api/departments/{id}`
- **方法**: GET
- **描述**: 获取指定部门的详细信息
- **权限**: `ADMIN`, `SUPER_ADMIN`
- **响应**:
  ```json
  {
    "code": 200,
    "message": "获取部门详情成功",
    "data": {
      "id": "long",
      "name": "string",
      "description": "string",
      "userCount": "int",
      "users": [
        {
          "id": "long",
          "username": "string",
          "realName": "string"
        }
      ]
    }
  }
  ```

### 5.4 创建部门

- **URL**: `/api/departments`
- **方法**: POST
- **描述**: 创建新部门
- **权限**: `ADMIN`, `SUPER_ADMIN`
- **请求体**:
  ```json
  {
    "name": "string",
    "description": "string"
  }
  ```
- **响应**:
  ```json
  {
    "code": 201,
    "message": "创建部门成功",
    "data": {
      "id": "long",
      "name": "string",
      "description": "string",
      "userCount": 0
    }
  }
  ```

### 5.5 更新部门

- **URL**: `/api/departments/{id}`
- **方法**: PUT
- **描述**: 更新部门信息
- **权限**: `ADMIN`, `SUPER_ADMIN`
- **请求体**:
  ```json
  {
    "name": "string",
    "description": "string"
  }
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "message": "更新部门成功",
    "data": {
      "id": "long",
      "name": "string",
      "description": "string",
      "userCount": "int"
    }
  }
  ```

### 5.6 删除部门

- **URL**: `/api/departments/{id}`
- **方法**: DELETE
- **描述**: 删除指定部门
- **权限**: `ADMIN`, `SUPER_ADMIN`
- **响应**:
  ```json
  {
    "code": 200,
    "message": "删除部门成功",
    "data": null
  }
  ```

### 5.7 获取部门用户

- **URL**: `/api/departments/{id}/users`
- **方法**: GET
- **描述**: 获取指定部门的用户列表
- **权限**: `ADMIN`, `SUPER_ADMIN`
- **响应**:
  ```json
  {
    "code": 200,
    "message": "获取部门用户成功",
    "data": [
      {
        "id": "long",
        "username": "string",
        "realName": "string",
        "email": "string",
        "phone": "string",
        "status": "int"
      }
    ]
  }
  ```

## 6. 用户组管理 API

### 6.1 获取用户组列表

- **URL**: `/api/groups`
- **方法**: GET
- **描述**: 获取用户组列表，支持分页和筛选
- **权限**: `ADMIN`, `SUPER_ADMIN`
- **请求参数**:
  - `name`: 用户组名称模糊查询（可选）
  - `page`: 页码，默认0
  - `size`: 每页记录数，默认10
  - `sort`: 排序字段
- **响应**:
  ```json
  {
    "code": 200,
    "message": "获取用户组列表成功",
    "data": {
      "content": [
        {
          "id": "long",
          "name": "string",
          "description": "string",
          "userCount": "int"
        }
      ],
      "totalElements": "long",
      "totalPages": "int",
      "size": "int",
      "number": "int"
    }
  }
  ```

### 6.2 获取所有用户组

- **URL**: `/api/groups/all`
- **方法**: GET
- **描述**: 获取所有用户组，不分页
- **权限**: `ADMIN`, `SUPER_ADMIN`
- **响应**:
  ```json
  {
    "code": 200,
    "message": "获取用户组列表成功",
    "data": [
      {
        "id": "long",
        "name": "string",
        "description": "string",
        "userCount": "int"
      }
    ]
  }
  ```

### 6.3 获取用户组详情

- **URL**: `/api/groups/{id}`
- **方法**: GET
- **描述**: 获取指定用户组的详细信息
- **权限**: `ADMIN`, `SUPER_ADMIN`
- **响应**:
  ```json
  {
    "code": 200,
    "message": "获取用户组详情成功",
    "data": {
      "id": "long",
      "name": "string",
      "description": "string",
      "userCount": "int",
      "users": [
        {
          "id": "long",
          "username": "string",
          "realName": "string"
        }
      ]
    }
  }
  ```

### 6.4 创建用户组

- **URL**: `/api/groups`
- **方法**: POST
- **描述**: 创建新用户组
- **权限**: `ADMIN`, `SUPER_ADMIN`
- **请求体**:
  ```json
  {
    "name": "string",
    "description": "string"
  }
  ```
- **响应**:
  ```json
  {
    "code": 201,
    "message": "创建用户组成功",
    "data": {
      "id": "long",
      "name": "string",
      "description": "string",
      "userCount": 0
    }
  }
  ```

### 6.5 更新用户组

- **URL**: `/api/groups/{id}`
- **方法**: PUT
- **描述**: 更新用户组信息
- **权限**: `ADMIN`, `SUPER_ADMIN`
- **请求体**:
  ```json
  {
    "name": "string",
    "description": "string"
  }
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "message": "更新用户组成功",
    "data": {
      "id": "long",
      "name": "string",
      "description": "string",
      "userCount": "int"
    }
  }
  ```

### 6.6 删除用户组

- **URL**: `/api/groups/{id}`
- **方法**: DELETE
- **描述**: 删除指定用户组
- **权限**: `ADMIN`, `SUPER_ADMIN`
- **响应**:
  ```json
  {
    "code": 200,
    "message": "删除用户组成功",
    "data": null
  }
  ```

### 6.7 获取用户组用户

- **URL**: `/api/groups/{id}/users`
- **方法**: GET
- **描述**: 获取指定用户组的用户列表
- **权限**: `ADMIN`, `SUPER_ADMIN`
- **响应**:
  ```json
  {
    "code": 200,
    "message": "获取用户组用户成功",
    "data": [
      {
        "id": "long",
        "username": "string",
        "realName": "string",
        "email": "string",
        "phone": "string",
        "status": "int"
      }
    ]
  }
  ```

### 6.8 添加用户到用户组

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
    "code": 200,
    "message": "添加用户到用户组成功",
    "data": null
  }
  ```

### 6.9 从用户组移除用户

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
    "code": 200,
    "message": "从用户组移除用户成功",
    "data": null
  }
  ```

## 7. 权限管理 API

### 7.1 获取所有权限

- **URL**: `/api/permissions`
- **方法**: GET
- **描述**: 获取所有权限列表
- **权限**: `SUPER_ADMIN`
- **响应**:
  ```json
  {
    "code": 200,
    "message": "获取权限列表成功",
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

### 7.2 获取权限详情

- **URL**: `/api/permissions/{id}`
- **方法**: GET
- **描述**: 获取指定权限的详细信息
- **权限**: `SUPER_ADMIN`
- **响应**:
  ```json
  {
    "code": 200,
    "message": "获取权限详情成功",
    "data": {
      "id": "long",
      "name": "string",
      "code": "string",
      "description": "string"
    }
  }
  ```

### 7.3 创建权限

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
    "code": 201,
    "message": "创建权限成功",
    "data": {
      "id": "long",
      "name": "string",
      "code": "string",
      "description": "string"
    }
  }
  ```

### 7.4 更新权限

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
    "code": 200,
    "message": "更新权限成功",
    "data": {
      "id": "long",
      "name": "string",
      "code": "string",
      "description": "string"
    }
  }
  ```

### 7.5 删除权限

- **URL**: `/api/permissions/{id}`
- **方法**: DELETE
- **描述**: 删除指定权限
- **权限**: `SUPER_ADMIN`
- **响应**:
  ```json
  {
    "code": 200,
    "message": "删除权限成功",
    "data": null
  }
  ```

### 7.6 获取角色权限

- **URL**: `/api/permissions/role/{roleId}`
- **方法**: GET
- **描述**: 获取指定角色的权限列表
- **权限**: `SUPER_ADMIN`
- **响应**:
  ```json
  {
    "code": 200,
    "message": "获取角色权限成功",
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

### 7.7 获取用户权限

- **URL**: `/api/permissions/user/{userId}`
- **方法**: GET
- **描述**: 获取指定用户的权限列表
- **权限**: `SUPER_ADMIN`
- **响应**:
  ```json
  {
    "code": 200,
    "message": "获取用户权限成功",
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

### 7.8 获取用户权限编码

- **URL**: `/api/permissions/user/{userId}/codes`
- **方法**: GET
- **描述**: 获取指定用户的权限编码列表
- **权限**: `SUPER_ADMIN`
- **响应**:
  ```json
  {
    "code": 200,
    "message": "获取用户权限编码成功",
    "data": ["string"] // 权限编码集合
  }
  ```
