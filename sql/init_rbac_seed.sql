-- 权限字典初始化（可重复执行）
INSERT INTO `permission` (`id`, `code`, `name`, `resource`, `action`, `status`, `remark`, `is_delete`)
VALUES (10001, 'project:create', '创建项目', 'project', 'create', 1, '项目创建权限', 0),
       (10002, 'project:view', '查看项目', 'project', 'view', 1, '项目查看权限', 0),
       (10003, 'project:update', '更新项目', 'project', 'update', 1, '项目更新权限', 0),
       (10004, 'project:delete', '删除项目', 'project', 'delete', 1, '项目删除权限', 0),
       (10005, 'project:archive', '归档项目', 'project', 'archive', 1, '项目归档权限', 0),
       (11001, 'task:create', '创建任务', 'task', 'create', 1, '任务创建权限', 0),
       (11002, 'task:view', '查看任务', 'task', 'view', 1, '任务查看权限', 0),
       (11003, 'task:update', '更新任务', 'task', 'update', 1, '任务更新权限', 0),
       (11004, 'task:delete', '删除任务', 'task', 'delete', 1, '任务删除权限', 0),
       (11005, 'task:complete', '完成任务', 'task', 'complete', 1, '任务完成权限', 0)
ON DUPLICATE KEY UPDATE
    `name`      = VALUES(`name`),
    `resource`  = VALUES(`resource`),
    `action`    = VALUES(`action`),
    `status`    = VALUES(`status`),
    `remark`    = VALUES(`remark`),
    `is_delete` = VALUES(`is_delete`);

-- 默认租户角色初始化（tenant_id = 0）
INSERT INTO `role` (`id`, `tenant_id`, `code`, `name`, `status`, `is_system`, `remark`, `is_delete`)
VALUES (20001, 0, 'tenant_admin', '租户管理员', 1, 1, '默认租户管理员角色', 0),
       (20002, 0, 'member', '普通成员', 1, 1, '默认租户普通成员角色', 0)
ON DUPLICATE KEY UPDATE
    `name`      = VALUES(`name`),
    `status`    = VALUES(`status`),
    `is_system` = VALUES(`is_system`),
    `remark`    = VALUES(`remark`),
    `is_delete` = VALUES(`is_delete`);

-- 默认角色与权限绑定
INSERT INTO `role_permission` (`id`, `tenant_id`, `role_id`, `permission_id`, `is_delete`)
VALUES (30001, 0, 20001, 10001, 0),
       (30002, 0, 20001, 10002, 0),
       (30003, 0, 20001, 10003, 0),
       (30004, 0, 20001, 10004, 0),
       (30005, 0, 20001, 10005, 0),
       (30006, 0, 20001, 11001, 0),
       (30007, 0, 20001, 11002, 0),
       (30008, 0, 20001, 11003, 0),
       (30009, 0, 20001, 11004, 0),
       (30010, 0, 20001, 11005, 0),
       (30011, 0, 20002, 10002, 0),
       (30012, 0, 20002, 11002, 0)
ON DUPLICATE KEY UPDATE
    `is_delete` = VALUES(`is_delete`);

