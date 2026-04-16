# Week 2 Minimal Performance Baseline

## Scope

- Baseline interfaces:
  - `project list`
  - `task list`
  - `task stats`
  - `weekly review stats`
- Test entry: `src/test/java/com/spt/learningmanage/benchmark/ApiSequentialBaselineTest.java`
- Goal: keep a minimal, repeatable baseline before collaboration access rules become more complex.

## Scenario Matrix

| Group | User Model | Tenant Filter | Access-Check Layer | Status |
|---|---|---|---|---|
| single-tenant-single-user | Single user in one tenant | `tenant_id` enabled (`X-Tenant-Id`) | Current behavior (before collaboration access expansion) | TODO |
| single-tenant-multi-user | Multiple users in the same tenant | `tenant_id` enabled (`X-Tenant-Id`) | Current behavior (before collaboration access expansion) | TODO |
| access-check-compare-slot | Single/multi-user compare | `tenant_id` enabled (`X-Tenant-Id`) | Reserved for future access-check layer comparison | TODO |

## Result Table

Fill one row per interface per group.

| Group | Interface | Rounds | total (ms) | avg (ms) | min (ms) | max (ms) | Data Scale | Cache/Cold Start Notes |
|---|---|---:|---:|---:|---:|---:|---|---|
| single-tenant-single-user | project list | TODO | TODO | TODO | TODO | TODO | TODO | TODO |
| single-tenant-single-user | task list | TODO | TODO | TODO | TODO | TODO | TODO | TODO |
| single-tenant-single-user | task stats | TODO | TODO | TODO | TODO | TODO | TODO | TODO |
| single-tenant-single-user | weekly review stats | TODO | TODO | TODO | TODO | TODO | TODO | TODO |
| single-tenant-multi-user | project list | TODO | TODO | TODO | TODO | TODO | TODO | TODO |
| single-tenant-multi-user | task list | TODO | TODO | TODO | TODO | TODO | TODO | TODO |
| single-tenant-multi-user | task stats | TODO | TODO | TODO | TODO | TODO | TODO | TODO |
| single-tenant-multi-user | weekly review stats | TODO | TODO | TODO | TODO | TODO | TODO | TODO |
| access-check-compare-slot | project list | TODO | TODO | TODO | TODO | TODO | TODO | TODO |
| access-check-compare-slot | task list | TODO | TODO | TODO | TODO | TODO | TODO | TODO |
| access-check-compare-slot | task stats | TODO | TODO | TODO | TODO | TODO | TODO | TODO |
| access-check-compare-slot | weekly review stats | TODO | TODO | TODO | TODO | TODO | TODO | TODO |

## Run Notes

- Keep the same test profile and seed scripts for repeatability.
- Record whether the first run includes cold start overhead.
- If rerun multiple times, keep this document with the same run order for consistency.

