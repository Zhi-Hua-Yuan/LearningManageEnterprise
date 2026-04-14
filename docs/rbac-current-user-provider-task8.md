# RBAC 当前用户来源抽象 v1（任务8）

## 目标

为 RBAC 第一轮提供稳定的“当前用户来源抽象”，降低权限切面对登录实现细节的耦合。

## 设计结论

- 新增抽象接口：`CurrentUserProvider`
- 第一轮只提供最小能力：`getRequiredCurrentUserId()`
- 默认实现：`ThreadLocalCurrentUserProvider`
- 默认实现内部复用现有 `UserHolder`

## 为什么要做这个抽象

- 切面需要“当前用户”，但不应直接依赖具体上下文工具类
- 后续登录实现变化时，权限层不需要跟着重写
- 测试可以通过 mock `CurrentUserProvider` 更容易替换用户来源

## 本轮接口与边界

接口：

- `Long getRequiredCurrentUserId()`

语义：

- 当前用户存在：返回用户 ID
- 当前用户缺失：抛 `NOT_LOGIN_ERROR`

本轮不做：

- 返回完整 User 对象
- 暴露 token / claims / SecurityContext
- 引入 Spring Security 或 JWT 解析

## 与现有链路衔接

1. 登录拦截器继续把用户 ID 写入 `UserHolder`
2. `ThreadLocalCurrentUserProvider` 从 `UserHolder` 读取用户 ID
3. `RequirePermissionAspect` 仅依赖 `CurrentUserProvider`

## 受影响对象

- `src/main/java/com/spt/learningmanage/service/CurrentUserProvider.java`
- `src/main/java/com/spt/learningmanage/service/impl/ThreadLocalCurrentUserProvider.java`
- `src/main/java/com/spt/learningmanage/aspect/RequirePermissionAspect.java`

## 风险控制

- 切面中不再硬编码 `UserHolder.get()`
- 统一“当前用户缺失”异常语义，避免调用方判空风格分裂

