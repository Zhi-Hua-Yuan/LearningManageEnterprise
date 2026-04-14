# RBAC 方法级注解规范 v1

## 目标

定义 `@RequirePermission` 第一轮最小方案，只约束注解职责、参数、使用位置和边界，不扩展为表达式权限系统。

## 注解职责

`@RequirePermission` 只做一件事：

- 声明“调用该方法需要某个权限码”。

它不负责：

- 解析用户或租户
- 查询数据库
- 超级管理员放行判断
- 403 响应生成

以上能力由切面与服务层负责。

## 参数规范

第一轮仅保留单参数：

- `value: String`（必填）
- 格式：`资源:操作`，例如 `project:create`

推荐写法：

```java
@RequirePermission("project:create")
@RequirePermission("project:update")
```

建议在业务代码中优先使用权限常量，例如：

```java
@RequirePermission(PermissionConstants.PROJECT_CREATE)
```

## 为什么只做单权限匹配

- 与 `permission.code` 一一对应，语义清晰
- 切面实现最简单，验证成本最低
- 适合第一轮试点，避免改造范围扩大
- 后续可渐进扩展，不会破坏现有语义

## 推荐使用位置

第一轮优先使用在 Controller 方法上：

- 接口入口边界清晰
- 便于快速验证 403 行为
- 试点范围可控

当前试点建议以 `ProjectController` 为主。

## 不建议使用位置

- Entity / DTO / VO
- Mapper 接口
- 私有方法、工具方法
- 同类内部高频互调的方法

## 关键边界：Spring AOP 自调用

Spring AOP 基于代理。类内方法直接调用同类方法时，通常不会经过代理，注解可能失效。

因此第一轮建议：

- 不把主要注解落在 Service 内部互调路径
- 优先放在 Controller 入口方法

## 本轮明确不做

- 多权限数组
- AND / OR 逻辑组合
- SpEL 表达式
- 基于方法参数的数据级权限
- 注解级超级管理员绕过配置
- 前端菜单/按钮注解模型
- 类级与方法级复杂合并规则

## 当前实现对照

- 注解定义：`src/main/java/com/spt/learningmanage/annotation/RequirePermission.java`
- 试点控制器：`src/main/java/com/spt/learningmanage/controller/ProjectController.java`
- 权限常量：`src/main/java/com/spt/learningmanage/constant/PermissionConstants.java`

该方案满足 RBAC 第一轮“最小可用、低风险、可演进”的目标。

