INSERT INTO `tenant` (`id`, `code`, `name`, `status`, `is_default`, `remark`, `is_delete`)
VALUES (100, 'baseline_t100', '基线租户100', 1, 0, 'api baseline', 0)
ON DUPLICATE KEY UPDATE
    `name` = VALUES(`name`),
    `status` = VALUES(`status`),
    `is_delete` = VALUES(`is_delete`);

INSERT INTO `user` (`id`, `account`, `username`, `password`, `user_role`, `is_delete`)
VALUES (92001, 'baseline_user', '基线用户', 'N/A', 'user', 0)
ON DUPLICATE KEY UPDATE
    `username` = VALUES(`username`),
    `is_delete` = VALUES(`is_delete`);

INSERT INTO `user` (`id`, `account`, `username`, `password`, `user_role`, `is_delete`)
VALUES (92002, 'baseline_user_b', '基线用户B', 'N/A', 'user', 0)
ON DUPLICATE KEY UPDATE
    `username` = VALUES(`username`),
    `is_delete` = VALUES(`is_delete`);

INSERT INTO `project` (`id`, `tenant_id`, `user_id`, `name`, `goal`, `status`, `order_no`, `progress`, `is_delete`, `deleted_at`)
VALUES (70001, 100, 92001, '基线项目A', '用于project/task/stats基准', 0, 0, 45.00, 0, NULL),
       (70002, 100, 92001, '基线项目B', '用于weekly review focus', 0, 1, 70.00, 0, NULL)
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

INSERT INTO `project` (`id`, `tenant_id`, `user_id`, `name`, `goal`, `status`, `order_no`, `progress`, `is_delete`, `deleted_at`)
VALUES (70003, 100, 92002, '基线项目C', '用于同租户多用户基准', 0, 2, 30.00, 0, NULL)
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

INSERT INTO `task` (`id`, `tenant_id`, `project_id`, `milestone_id`, `user_id`, `title`, `description`, `status`, `priority`, `due_date`, `completed_at`, `is_delete`)
VALUES
    (71001, 100, 70001, NULL, 92001, 'task-1', 'today todo', 0, 1, CURDATE(), NULL, 0),
    (71002, 100, 70001, NULL, 92001, 'task-2', 'overdue todo', 1, 2, DATE_SUB(CURDATE(), INTERVAL 2 DAY), NULL, 0),
    (71003, 100, 70001, NULL, 92001, 'task-3', 'done this week', 2, 2, DATE_SUB(CURDATE(), INTERVAL 1 DAY), NOW(), 0),
    (71004, 100, 70001, NULL, 92001, 'task-4', 'done this week', 2, 3, CURDATE(), NOW(), 0),
    (71005, 100, 70002, NULL, 92001, 'task-5', 'done this week for project B', 2, 1, CURDATE(), NOW(), 0),
    (71006, 100, 70002, NULL, 92001, 'task-6', 'todo future', 0, 0, DATE_ADD(CURDATE(), INTERVAL 3 DAY), NULL, 0),
    (71007, 100, 70002, NULL, 92001, 'task-7', 'in progress future', 1, 1, DATE_ADD(CURDATE(), INTERVAL 5 DAY), NULL, 0),
    (71008, 100, 70001, NULL, 92001, 'task-8', 'done this week', 2, 1, CURDATE(), NOW(), 0)
ON DUPLICATE KEY UPDATE
    `tenant_id` = VALUES(`tenant_id`),
    `project_id` = VALUES(`project_id`),
    `user_id` = VALUES(`user_id`),
    `title` = VALUES(`title`),
    `description` = VALUES(`description`),
    `status` = VALUES(`status`),
    `priority` = VALUES(`priority`),
    `due_date` = VALUES(`due_date`),
    `completed_at` = VALUES(`completed_at`),
    `is_delete` = VALUES(`is_delete`);

INSERT INTO `task` (`id`, `tenant_id`, `project_id`, `milestone_id`, `user_id`, `title`, `description`, `status`, `priority`, `due_date`, `completed_at`, `is_delete`)
VALUES
    (71009, 100, 70003, NULL, 92002, 'task-b-1', 'user B todo', 0, 1, CURDATE(), NULL, 0),
    (71010, 100, 70003, NULL, 92002, 'task-b-2', 'user B doing', 1, 1, CURDATE(), NULL, 0),
    (71011, 100, 70003, NULL, 92002, 'task-b-3', 'user B done', 2, 2, CURDATE(), NOW(), 0)
ON DUPLICATE KEY UPDATE
    `tenant_id` = VALUES(`tenant_id`),
    `project_id` = VALUES(`project_id`),
    `user_id` = VALUES(`user_id`),
    `title` = VALUES(`title`),
    `description` = VALUES(`description`),
    `status` = VALUES(`status`),
    `priority` = VALUES(`priority`),
    `due_date` = VALUES(`due_date`),
    `completed_at` = VALUES(`completed_at`),
    `is_delete` = VALUES(`is_delete`);

