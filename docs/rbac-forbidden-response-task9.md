# RBAC 无权限统一 403 规范 v1（任务9）

## 目标

在 RBAC 第一轮中统一“无权限”返回口径：

- 权限不足统一抛专用异常
- 全局异常处理统一映射 HTTP 403
- 返回消息保持简洁，不泄露内部权限细节

## 最小方案

1. 使用专用异常：`ForbiddenException`
2. 权限拦截失败时统一抛 `ForbiddenException`
3. 全局异常处理器捕获 `ForbiddenException` 并返回 403
4. 响应结构沿用项目统一 `BaseResponse`

## 语义边界

- 未登录：`NOT_LOGIN_ERROR`
- 无权限：`FORBIDDEN_ERROR`（HTTP 403）
- 参数问题：`PARAMS_ERROR`

这三类语义在第一轮必须分离，避免口径混淆。

## 消息边界

前端返回建议：

- 固定使用 `FORBIDDEN_ERROR` 默认文案
- 不返回权限码、角色码、租户信息等内部细节

不建议直接返回：

- `project:update` 等权限码
- 角色列表
- 租户 ID
- 内部授权命中路径

## 当前落地对照

- 异常类型：`src/main/java/com/spt/learningmanage/exception/ForbiddenException.java`
- 权限切面：`src/main/java/com/spt/learningmanage/aspect/RequirePermissionAspect.java`
- 全局处理：`src/main/java/com/spt/learningmanage/exception/GlobalExceptionHandler.java`

## 风险控制

- 不混用通用业务异常承载“无权限”
- 不把未登录与无权限合并成同一错误返回
- 不向前端暴露授权内部细节

