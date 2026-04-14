# LearningManage Backend

Spring Boot + MyBatis Plus + MySQL backend skeleton for the learning management project.

## Quick Start

### Requirements
- JDK 17
- Maven 3.9+ (or use `mvnw`)
- MySQL 8.x

### Run
- Default profile: `dev`
- Command:

```bash
./mvnw spring-boot:run
```

Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

### Run with profile

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

## Base Config
- Port: `8123`
- API prefix: `/api`
- Health endpoint: `GET /api/health`

## Environment Config Files
- `src/main/resources/application.yml` (shared config + active profile)
- `src/main/resources/application-dev.yml`
- `src/main/resources/application-test.yml`
- `src/main/resources/application-prod.yml`

## Response Format

Success example (`GET /api/health`):

```json
{
  "code": 0,
  "message": "OK",
  "data": "ok"
}
```

Error example (`GET /api/demo/error/business`):

```json
{
  "code": 1000,
  "message": "Demo business exception",
  "data": null
}
```

## Global Exception Demo Endpoints
- Business exception: `GET /api/demo/error/business`
- System exception: `GET /api/demo/error/system`
- Validation exception: `GET /api/demo/error/validate?value=0`

## RBAC MVP (Round 1)

RBAC 第一轮落地了 4 张核心表：

- `role`（租户内角色，带 `tenant_id`）
- `permission`（全局权限字典，不带 `tenant_id`）
- `user_role`（用户在租户下的角色绑定，带 `tenant_id`）
- `role_permission`（角色权限绑定，带 `tenant_id`）

配套内容：

- SQL: `sql/init_role.sql`, `sql/init_permission.sql`, `sql/init_user_role.sql`, `sql/init_role_permission.sql`, `sql/init_rbac_seed.sql`
- Entity: `Role`, `Permission`, `UserRole`, `RolePermission`
- Mapper: `RoleMapper`, `PermissionMapper`, `UserRoleMapper`, `RolePermissionMapper`
- Service: `RbacService` / `RbacServiceImpl`

说明：

- 权限查询通过 `TenantService.resolveCurrentTenantId()` 获取当前租户。
- MyBatis 租户拦截白名单已纳入 `role`、`user_role`、`role_permission`。

本地验证：

```powershell
.\mvnw.cmd -DskipTests compile
.\mvnw.cmd test
```

权限命名规范文档：`docs/rbac-permission-naming-v1.md`

