# Collab Pre-Acceptance TODO (Task #4 Debt Tracking)

> Purpose: track pre-acceptance scenarios that are **not yet implementable** in current owner-only model, so requirements do not get lost.

## Scope

- Current baseline has been fixed in `docs/collab-pre-acceptance-matrix-v1.md`.
- This TODO only records scenario debt for:
  - #3 User B can create tasks under team-shared project
  - #4 After assigning task to user C, C can view and update task

## Scenario Debt List

## #3 User B can create tasks under team-shared project

- **Current status**: Not implementable yet
- **Owner**: Collaboration backend owner (Team/Access model)
- **Preconditions required**:
  1. Team/member relation model available (at least `team_member` equivalent capability)
  2. Project visibility rule can include team members (not only `project.user_id` owner)
  3. Task create access check supports "project is accessible" semantics instead of owner-only
- **Suggested test entry**:
  - Controller integration: `POST /task/add`
  - Service chain: `TaskServiceImpl#create` with project access checker
- **Why blocked now**:
  - Current `TaskServiceImpl` create path still validates by owner-scope project access.
  - Team visibility relation is not implemented.
- **Definition of done (for this scenario)**:
  - In same tenant, user A creates project.
  - User B (non-owner) gains team visibility.
  - User B can create task under A's project via controller/service/db chain.

## #4 After assigning task to user C, C can view and update task

- **Current status**: Not implementable yet
- **Owner**: Task collaboration owner (Assignment + Access policy)
- **Preconditions required**:
  1. Task assignee field/model introduced (e.g. `assignee_user_id`)
  2. Assignment write path implemented (controller/service + persistence)
  3. Access policy updated: assignee can view/update assigned task under tenant scope
  4. Assignment and access rules aligned with tenant isolation and RBAC
- **Suggested test entry**:
  - Controller integration:
    - assignment endpoint (placeholder exists): `POST /task/assign`
    - verify endpoints: `GET /task/get/{id}`, `POST /task/update`
  - Service chain:
    - assignment service + `TaskServiceImpl#getById/update`
- **Why blocked now**:
  - Current `/task/assign` is RBAC placeholder only (`ok(true)`), no persistence side effects.
  - No assignee model exists; access still owner-only.
- **Definition of done (for this scenario)**:
  - User B assigns task to user C.
  - User C can read and update that task within same tenant.
  - Cross-tenant users still cannot access assigned task.

## Trigger Points For Activation

- Trigger A: team/member data model merged
- Trigger B: task assignment model merged
- Trigger C: access checker upgraded from owner-only to collaboration-aware policy

When all related triggers are merged, convert #3/#4 into executable integration tests and remove this TODO debt record.

