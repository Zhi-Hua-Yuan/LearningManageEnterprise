-- 清理集成测试数据（幂等）
DELETE FROM `user_role` WHERE `id` IN (84001, 84002);
DELETE FROM `role_permission` WHERE `id` IN (83001, 83002, 83003);
DELETE FROM `role` WHERE `id` IN (82001, 82002);
DELETE FROM `permission` WHERE `id` IN (10002, 10007);

DELETE FROM `task` WHERE `project_id` IN (70001, 70002);
DELETE FROM `project` WHERE `id` IN (70001, 70002);
DELETE FROM `tenant` WHERE `id` IN (100, 200);
DELETE FROM `user` WHERE `id` IN (92001, 92002, 92003);

