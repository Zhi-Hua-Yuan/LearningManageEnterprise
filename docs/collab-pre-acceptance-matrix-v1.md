# Collab Pre-Acceptance Matrix v1

> Current baseline is **owner-only visibility**. This matrix defines what is verified now before team collaboration features are implemented.

## Scope

- Test class: `src/test/java/com/spt/learningmanage/controller/ProjectAccessChainIntegrationTest.java`
- Test class: `src/test/java/com/spt/learningmanage/controller/CollabPreAcceptanceIntegrationTest.java`
- Goal: lock scenarios #1 / #2 / #5 to avoid regression during collaboration refactor.

## Scenarios

| ID | Scenario | Expected (Current Phase) | Covered By |
|---|---|---|---|
| #1 | Same tenant user A/B project visibility | Owner-only: A sees A project, B sees B project; cross-owner get by id returns `PROJECT_NOT_FOUND` | `sameTenantUsersShouldSeeOwnProjectsOnly` |
| #2 | RBAC grant effect | `project:view` grant changes entry from `403` to business layer; does **not** expand owner visibility | `rbacGrantShouldChangeProjectGetAccessResult` |
| #5 | Cross-tenant isolation | Tenant 200 user cannot view tenant 100 project (`PROJECT_NOT_FOUND`); list in tenant 200 returns empty | `crossTenantShouldRemainInvisible` |
| #3 (half) | Same-tenant collaboration precondition | User B can create task under A's project **after** `project:view_team` grant | `tenantTeamVisibilityGrantShouldAllowTaskCreateOnNonOwnerProject` |

## Not Covered Yet

- Team-based project visibility (`team_member` model)
- Task assignment (`assignee`) visibility and update rights
- Same-tenant multi-user collaboration behavior after team sharing

