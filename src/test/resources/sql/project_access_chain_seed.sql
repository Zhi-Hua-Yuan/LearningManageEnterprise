-- 租户准备
INSERT INTO `tenant` (`id`, `code`, `name`, `status`, `is_default`, `remark`, `is_delete`)
VALUES (100, 't100', '租户100', 1, 0, '集成测试租户', 0),
       (200, 't200', '租户200', 1, 0, '集成测试租户', 0)
ON DUPLICATE KEY UPDATE
    `name` = VALUES(`name`),
    `status` = VALUES(`status`),
    `is_delete` = VALUES(`is_delete`);

-- 用户准备（登录拦截器只解析JWT，但保留数据便于链路完整性）
INSERT INTO `user` (`id`, `account`, `username`, `password`, `user_role`, `is_delete`)
VALUES (92001, 'it_a', '用户A', 'N/A', 'user', 0),
       (92002, 'it_b', '用户B', 'N/A', 'user', 0),
       (92003, 'it_c', '用户C', 'N/A', 'user', 0)
ON DUPLICATE KEY UPDATE
    `username` = VALUES(`username`),
    `is_delete` = VALUES(`is_delete`);

-- 项目准备：同租户A/B各一条
INSERT INTO `project` (`id`, `tenant_id`, `user_id`, `name`, `goal`, `status`, `order_no`, `progress`, `is_delete`, `deleted_at`)
VALUES (70001, 100, 92001, 'A-项目', 'owner A', 0, 0, 0.00, 0, NULL),
       (70002, 100, 92002, 'B-项目', 'owner B', 0, 1, 0.00, 0, NULL)
ON DUPLICATE KEY UPDATE
    `tenant_id` = VALUES(`tenant_id`),
    `user_id` = VALUES(`user_id`),
    `name` = VALUES(`name`),
    `goal` = VALUES(`goal`),
    `status` = VALUES(`status`),
    `order_no` = VALUES(`order_no`),
    `progress` = VALUES(`progress`),
    `is_delete` = VALUES(`is_delete`),
    `deleted_at` = VALUES(`deleted_at`);

-- 权限字典：仅确保 project:view 存在
INSERT INTO `permission` (`id`, `code`, `name`, `resource`, `action`, `status`, `remark`, `is_delete`)
VALUES (10002, 'project:view', '查看项目', 'project', 'view', 1, '集成测试权限', 0)
ON DUPLICATE KEY UPDATE
    `name` = VALUES(`name`),
    `resource` = VALUES(`resource`),
    `action` = VALUES(`action`),
    `status` = VALUES(`status`),
    `is_delete` = VALUES(`is_delete`);

INSERT INTO `permission` (`id`, `code`, `name`, `resource`, `action`, `status`, `remark`, `is_delete`)
VALUES (10007, 'project:view_team', '查看团队项目', 'project', 'view_team', 1, '协作可见权限（测试）', 0)
ON DUPLICATE KEY UPDATE
    `name` = VALUES(`name`),
    `resource` = VALUES(`resource`),
    `action` = VALUES(`action`),
    `status` = VALUES(`status`),
    `is_delete` = VALUES(`is_delete`);

-- 租户100给用户B授权 project:view
INSERT INTO `role` (`id`, `tenant_id`, `code`, `name`, `status`, `is_system`, `remark`, `is_delete`)
VALUES (82001, 100, 'it_viewer_t100', '测试查看角色T100', 1, 0, '集成测试角色', 0)
ON DUPLICATE KEY UPDATE
    `name` = VALUES(`name`),
    `status` = VALUES(`status`),
    `is_delete` = VALUES(`is_delete`);

INSERT INTO `role_permission` (`id`, `tenant_id`, `role_id`, `permission_id`, `is_delete`)
VALUES (83001, 100, 82001, 10002, 0)
ON DUPLICATE KEY UPDATE
    `is_delete` = VALUES(`is_delete`);

INSERT INTO `user_role` (`id`, `tenant_id`, `user_id`, `role_id`, `status`, `is_delete`)
VALUES (84001, 100, 92002, 82001, 1, 0)
ON DUPLICATE KEY UPDATE
    `status` = VALUES(`status`),
    `is_delete` = VALUES(`is_delete`);

-- 租户200给用户C授权 project:view（用于跨租户隔离验证）
INSERT INTO `role` (`id`, `tenant_id`, `code`, `name`, `status`, `is_system`, `remark`, `is_delete`)
VALUES (82002, 200, 'it_viewer_t200', '测试查看角色T200', 1, 0, '集成测试角色', 0)
ON DUPLICATE KEY UPDATE
    `name` = VALUES(`name`),
    `status` = VALUES(`status`),
    `is_delete` = VALUES(`is_delete`);

INSERT INTO `role_permission` (`id`, `tenant_id`, `role_id`, `permission_id`, `is_delete`)
VALUES (83002, 200, 82002, 10002, 0)
ON DUPLICATE KEY UPDATE
    `is_delete` = VALUES(`is_delete`);

INSERT INTO `user_role` (`id`, `tenant_id`, `user_id`, `role_id`, `status`, `is_delete`)
VALUES (84002, 200, 92003, 82002, 1, 0)
ON DUPLICATE KEY UPDATE
    `status` = VALUES(`status`),
    `is_delete` = VALUES(`is_delete`);

