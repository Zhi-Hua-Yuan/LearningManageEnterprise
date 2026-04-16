# RBAC Collaboration Entry Reserved Points

## Scope
This document only reserves RBAC entry points for the next collaboration stage.
It does not introduce team or member business implementation in this round.

## Reserved Permission Codes
- `team:create`
- `team:manage_member`
- `project:assign`
- `task:assign`
- `project:view_team`

## Current Boundary
- Only constants and seed initialization are added.
- No team/team_member domain model is implemented.
- No full controller annotation rollout is performed.
- Existing API behavior remains unchanged.

## Next Stage Suggested Use
- Team creation entry: `team:create`
- Team member management entry: `team:manage_member`
- Project assignment entry: `project:assign`
- Task assignment entry: `task:assign`
- Team-level project visibility entry: `project:view_team`

