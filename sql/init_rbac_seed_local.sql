-- 本地/测试环境：默认超级管理员用户绑定（可重复执行）
-- TODO: replace ${BOOTSTRAP_ADMIN_USER_ID} with your local/test admin user id.

-- 示例：将用户 1 在默认租户 0 绑定为 super_admin（role_id = 20000）
INSERT INTO `user_role` (`id`, `tenant_id`, `user_id`, `role_id`, `status`, `is_delete`)
VALUES (40001, 0, 1, 20000, 1, 0)
ON DUPLICATE KEY UPDATE
    `status`    = VALUES(`status`),
    `is_delete` = VALUES(`is_delete`);

