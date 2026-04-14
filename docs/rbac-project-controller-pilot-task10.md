# RBAC ProjectController 试点方案 v1（任务10）

## 目标

在 RBAC 第一轮中，仅对 `ProjectController` 做最小试点接入，控制在 2~3 个核心接口，形成可验证闭环。

## 试点接口与权限映射

首批保留 3 个接口：

- `addProject` -> `project:create`
- `getProjectById` -> `project:view`
- `deleteProject` -> `project:delete`

说明：
- 读取类本轮选择 `getProjectById`。
- `listProject`、`updateProject`、`archiveProject`、`recoverProject`、`reorderProject` 暂不纳入首批注解试点。

## 最小改造原则

- 只在 Controller 方法上加 `@RequirePermission`
- 不在 Controller/Service 内手写权限判断
- 不改现有业务校验逻辑（参数、存在性、归属校验）
- 无权限统一走 `ForbiddenException -> HTTP 403`

## 联调重点

每个试点接口至少验证两类结果：

- 有权限：接口正常通过
- 无权限：统一返回 403

建议联调顺序：

1. `project:view`
2. `project:create`
3. `project:delete`

## 不在本轮范围

- 归档/恢复/批量接口权限注解化
- 复杂筛选或统计接口
- 前端按钮显隐或路由权限

## 受影响对象

- `src/main/java/com/spt/learningmanage/controller/ProjectController.java`
- `src/main/java/com/spt/learningmanage/annotation/RequirePermission.java`
- `src/main/java/com/spt/learningmanage/aspect/RequirePermissionAspect.java`
- `src/main/java/com/spt/learningmanage/exception/GlobalExceptionHandler.java`

