# RBAC 切面链路规范 v1（任务7）

## 目标

定义基于 `@RequirePermission` 的最小可用 AOP 鉴权链路，仅覆盖方法级权限判定，不扩展到登录认证、JWT 解析或数据级权限。

## 执行流程

1. AOP 拦截到带 `@RequirePermission` 的方法。
2. 读取注解权限码（单个字符串）。
3. 从 `UserHolder` 获取 `userId`。
4. 从 `TenantHolder` 获取 `tenantId`。
5. 先调用 `isSuperAdmin(userId, tenantId)`。
6. 若不是超级管理员，再调用 `hasPermission(userId, tenantId, permissionCode)`。
7. 判定通过则 `proceed()`，否则抛 `ForbiddenException`。

## 责任边界

切面只负责：

- 读取注解声明
- 触发权限判定
- 决定放行或抛出 403 异常

切面不负责：

- 登录认证
- JWT 解析
- 权限数据查询细节
- 业务数据范围控制

## 异常约定

- 用户上下文缺失：`NOT_LOGIN_ERROR`
- 租户上下文缺失：`TENANT_CONTEXT_MISSING`
- 权限不足：`ForbiddenException`（统一映射 403）

## 为什么使用 AOP

- 与方法级注解语义天然匹配
- 只拦截打了注解的方法，灰度成本低
- 不污染 Controller/Service 业务代码

## 生效边界与风险

生效边界：

- Spring 管理 Bean
- 公共方法
- 通过代理对象的外部调用

主要风险：

- 同类自调用导致切面失效
- 注解打在 private/static/非 Bean 方法上无效
- 注解值与权限字典不一致导致误判

## 第一轮建议

- 优先在 `ProjectController` 公开接口试点
- 统一使用 `PermissionConstants`
- 不扩展多权限数组、AND/OR、SpEL

