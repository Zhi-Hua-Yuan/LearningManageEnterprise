# RBAC 第一轮验收清单 v1（任务11）

## 目标

建立 RBAC 第一轮最小验证闭环，只覆盖当前明确落地能力：

- RBAC 四表模型可承载授权关系
- `@RequirePermission` 拦截生效
- 超级管理员放行生效
- 普通用户有权限可访问
- 无权限统一 403
- 权限查询受租户上下文约束，不跨租户串用

## 自动化验证映射

### 1) ProjectController 试点范围

- 测试：`src/test/java/com/spt/learningmanage/controller/ProjectControllerPermissionAnnotationTest.java`
- 目的：保证仅对任务10约定的 3 个接口挂注解：
  - `addProject`
  - `getProjectById`
  - `deleteProject`

### 2) 403 统一口径

- 测试：`src/test/java/com/spt/learningmanage/exception/ForbiddenResponseContractTest.java`
- 目的：验证 `ForbiddenException` 统一映射为：
  - HTTP 403
  - `ErrorCode.FORBIDDEN_ERROR` 返回体

### 3) 租户隔离查询约束

- 测试：`src/test/java/com/spt/learningmanage/mapper/AuthorizationMapperTenantIsolationContractTest.java`
- 目的：验证授权核心 SQL 显式包含：
  - `ur.tenant_id = #{tenantId}`
  - `r.tenant_id = #{tenantId}`
  - `rp.tenant_id = #{tenantId}`

### 4) 权限链路与超管判定

- 测试：`src/test/java/com/spt/learningmanage/service/impl/RbacServiceImplTest.java`
- 目的：验证 `userId + tenantId` 维度的权限判定与超管判定行为。

### 5) AOP 入口行为

- 测试：`src/test/java/com/spt/learningmanage/aspect/RequirePermissionAspectTest.java`
- 目的：验证切面在有权限/无权限/上下文缺失下的分支行为。

## 执行命令

```powershell
Set-Location "D:\ajavacode\enterprise-project\LearningManageEnterprise"
.\mvnw.cmd "-Dtest=ProjectControllerPermissionAnnotationTest,ForbiddenResponseContractTest,AuthorizationMapperTenantIsolationContractTest,RequirePermissionAspectTest,RbacServiceImplTest,ThreadLocalCurrentUserProviderTest" test
```

## 通过标准

满足以下条件即视为任务11通过：

1. 试点接口注解范围符合任务10定义（2~3个核心接口）。
2. 无权限场景统一返回 403 且业务码口径一致。
3. 授权 SQL 查询显式绑定租户条件，不依赖单点过滤。
4. 超级管理员与普通用户权限判定路径都可验证。
5. 关键测试全部通过。

## 本轮不覆盖

- 前端菜单/按钮权限
- 全量历史接口注解化
- Spring Security / JWT / OAuth
- 数据级与字段级权限
- 缓存一致性与审计平台

