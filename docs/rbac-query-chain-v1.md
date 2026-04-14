# RBAC 查询链路规范 v1

## 目标

定义第一轮 RBAC 的底层权限查询链路，确保权限判断严格绑定当前租户，避免跨租户串权限。

## 核心能力

- `getUserPermissions(userId, tenantId) -> Set<String>`
- `isSuperAdmin(userId, tenantId) -> boolean`
- `hasPermission(userId, tenantId, permissionCode) -> boolean`

说明：
- 三个方法都要求显式传入 `tenantId`。
- 现有“仅传 userId”的重载方法仅作为兼容层，内部从 `TenantContext`（当前为 `TenantHolder`）解析租户后再委托到显式方法。

## 标准联查路径

### 1) 查询用户权限集合

固定链路：

`user_role -> role -> role_permission -> permission`

返回：
- 去重后的 `permission.code` 集合。

### 2) 查询是否超级管理员

固定链路：

`user_role -> role`

判断条件：
- `role.code = 'super_admin'`

## SQL 约束要求

租户相关表必须显式带租户条件：

- `user_role.tenant_id = :tenantId`
- `role.tenant_id = :tenantId`
- `role_permission.tenant_id = :tenantId`

全局字典表 `permission` 不带 `tenant_id`。

同时建议统一附加有效性过滤：

- `status = 1`（对有状态字段的表）
- `is_delete = 0`

## 当前实现映射

- Mapper: `AuthorizationMapper`
- XML: `src/main/resources/mapper/AuthorizationMapper.xml`
- Service: `RbacServiceImpl`

当前关键查询：

- `listPermissionCodesByUserAndTenant(tenantId, userId)`
- `countUserRoleByCode(tenantId, userId, roleCode)`
- `countUserPermissionByCode(tenantId, userId, permissionCode)`

## 性能策略（第一轮）

- `hasPermission` 走 `countUserPermissionByCode`，避免先查全量权限集合。
- `getUserPermissions` 保留，用于批量判定或调试场景。

## 风险与防线

1. 只在单表加租户条件会放大脏数据风险。
2. 缓存键必须包含租户维度，禁止只用 `userId`。
3. 超级管理员判断使用 `role_code`，不要依赖 `role_id`。

## 后续预留

- 可增加缓存：
  - `perm:{tenantId}:{userId}`
  - `superAdmin:{tenantId}:{userId}`
- 缓存失效触发建议：
  - `user_role` 变更
  - `role_permission` 变更
  - `role.status` 变更
  - `permission.status` 变更

