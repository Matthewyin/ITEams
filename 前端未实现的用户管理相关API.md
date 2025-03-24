根据对比用户管理API手册和前端代码，我发现前端还没有实现调用的API如下：

## 1. 用户管理相关API

### 用户个人信息管理

1. **更新当前用户信息** - `/api/users/profile`
2. **修改当前用户密码** - `/api/users/change-password`
3. **更新用户头像** - `/api/users/{id}/avatar`
4. **更新当前用户头像** - `/api/users/profile/avatar`
5. **上传当前用户头像文件** - `/api/users/profile/avatar/upload`
6. **上传用户头像文件** - `/api/users/{id}/avatar/upload`
7. **解锁用户账户** - `/api/users/{userId}/unlock`

### 用户批量操作

1. **批量创建用户** - `/api/users/batch`
2. **批量删除用户** - `/api/users/batch`
3. **批量启用用户** - `/api/users/batch/enable`
4. **批量禁用用户** - `/api/users/batch/disable`
5. **批量分配角色** - `/api/users/batch/roles`
6. **批量解锁用户账户** - `/api/users/batch/unlock`

## 2. 部门管理API

前端完全没有实现部门管理相关API，包括：

1. **获取部门列表** - `/api/departments`
2. **获取所有部门** - `/api/departments/all`
3. **获取部门详情** - `/api/departments/{id}`
4. **创建部门** - `/api/departments`
5. **更新部门** - `/api/departments/{id}`
6. **删除部门** - `/api/departments/{id}`
7. **获取部门树** - `/api/departments/tree`
8. **获取部门用户** - `/api/departments/{id}/users`

## 3. 用户组管理API

前端完全没有实现用户组管理相关API，包括：

1. **获取用户组列表** - `/api/groups`
2. **获取所有用户组** - `/api/groups/all`
3. **获取用户组详情** - `/api/groups/{id}`
4. **创建用户组** - `/api/groups`
5. **更新用户组** - `/api/groups/{id}`
6. **删除用户组** - `/api/groups/{id}`
7. **获取用户组用户** - `/api/groups/{id}/users`
8. **添加用户到用户组** - `/api/groups/{id}/users`
9. **从用户组移除用户** - `/api/groups/{id}/users`

## 4. 权限管理API的部分功能

1. **获取用户权限编码** - `/api/permissions/user/{userId}/codes`
2. **获取用户菜单** - `/api/permissions/user/{userId}/menus`

## 5. 角色管理API的部分功能

1. **获取除超级管理员外的所有角色** - `/api/roles/all/except-superadmin`

这些API在后端已经实现，但前端尚未集成调用。特别是部门管理和用户组管理的API，前端完全没有相应的实现文件。根据之前的记忆，系统已经实现了用户批量操作功能和用户登录失败次数限制功能，但前端尚未完全集成这些功能的API调用。