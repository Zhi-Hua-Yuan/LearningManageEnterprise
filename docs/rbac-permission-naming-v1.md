# RBAC Permission Naming v1

## Scope

This document defines the first-round RBAC permission naming convention and the minimal permission set for Project/Task pilot.

## Naming Convention

- Format: `resource:action`
- Lowercase only
- English words only
- Singular resource name
- Use `:` as the only separator

Examples:

- `project:create`
- `project:view`
- `task:complete`

## Field Semantics

- `permission.code`: machine-readable unique key (used by annotation and permission check)
- `permission.name`: human-readable label
- `permission.resource`: resource segment from `code`
- `permission.action`: action segment from `code`

Note: current schema does not include `perm_type`. If needed later, keep it lightweight (for example `api`/`action`) and avoid menu-tree semantics in round 1.

## Minimal Permission Set (Round 1)

### Project

- `project:create`
- `project:view`
- `project:update`
- `project:delete`
- `project:archive`

### Task

- `task:create`
- `task:view`
- `task:update`
- `task:delete`
- `task:complete`

## Anti-Patterns

Do not use:

- `create:project` (action first)
- `projectCreate` / `project_create` (mixed styles)
- `admin:project:create` (role mixed into permission)
- `project:create_or_update` (multiple actions in one code)
- `tenant1:task:update` (tenant mixed into permission)

## Operational Notes

- Keep permission code stable after release.
- Add a new code only when business action is independently authorizable.
- Enforce tenant isolation through tenant-aware relationship tables and `TenantContext`, not through permission code naming.

