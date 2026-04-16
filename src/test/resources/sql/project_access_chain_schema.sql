CREATE TABLE IF NOT EXISTS `tenant`
(
    `id`          BIGINT       NOT NULL,
    `code`        VARCHAR(64)  NOT NULL,
    `name`        VARCHAR(128) NOT NULL,
    `status`      TINYINT      NOT NULL DEFAULT 1,
    `is_default`  TINYINT      NOT NULL DEFAULT 0,
    `remark`      VARCHAR(255)          DEFAULT NULL,
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `is_delete`   TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_tenant_code` (`code`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `user`
(
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `account`     VARCHAR(256) NOT NULL,
    `username`    VARCHAR(256) NOT NULL,
    `password`    VARCHAR(512) NOT NULL,
    `user_role`   VARCHAR(256) NOT NULL DEFAULT 'user',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `is_delete`   TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_account` (`account`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `project`
(
    `id`          BIGINT        NOT NULL AUTO_INCREMENT,
    `tenant_id`   BIGINT        NOT NULL DEFAULT 0,
    `user_id`     BIGINT        NOT NULL,
    `name`        VARCHAR(100)  NOT NULL,
    `goal`        VARCHAR(500)           DEFAULT NULL,
    `status`      TINYINT       NOT NULL DEFAULT 0,
    `order_no`    INT           NOT NULL DEFAULT 0,
    `progress`    DECIMAL(5, 2) NOT NULL DEFAULT 0.00,
    `start_date`  DATE                   DEFAULT NULL,
    `end_date`    DATE                   DEFAULT NULL,
    `is_delete`   TINYINT       NOT NULL DEFAULT 0,
    `create_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted_at`  DATETIME               DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_project_tenant_user_id` (`tenant_id`, `user_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `task`
(
    `id`           BIGINT       NOT NULL AUTO_INCREMENT,
    `tenant_id`    BIGINT       NOT NULL DEFAULT 0,
    `project_id`   BIGINT       NOT NULL,
    `milestone_id` BIGINT                DEFAULT NULL,
    `user_id`      BIGINT       NOT NULL,
    `title`        VARCHAR(60)  NOT NULL,
    `description`  VARCHAR(550)          DEFAULT NULL,
    `status`       TINYINT      NOT NULL DEFAULT 0,
    `priority`     TINYINT      NOT NULL DEFAULT 0,
    `due_date`     DATE                  DEFAULT NULL,
    `completed_at` DATETIME              DEFAULT NULL,
    `is_delete`    TINYINT      NOT NULL DEFAULT 0,
    `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_task_tenant_user_id` (`tenant_id`, `user_id`),
    KEY `idx_task_tenant_project_id` (`tenant_id`, `project_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `permission`
(
    `id`          BIGINT       NOT NULL,
    `code`        VARCHAR(64)  NOT NULL,
    `name`        VARCHAR(128) NOT NULL,
    `resource`    VARCHAR(64)  NOT NULL,
    `action`      VARCHAR(64)  NOT NULL,
    `status`      TINYINT      NOT NULL DEFAULT 1,
    `remark`      VARCHAR(255)          DEFAULT NULL,
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `is_delete`   TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_permission_code` (`code`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `role`
(
    `id`          BIGINT       NOT NULL,
    `tenant_id`   BIGINT       NOT NULL,
    `code`        VARCHAR(64)  NOT NULL,
    `name`        VARCHAR(128) NOT NULL,
    `status`      TINYINT      NOT NULL DEFAULT 1,
    `is_system`   TINYINT      NOT NULL DEFAULT 0,
    `remark`      VARCHAR(255)          DEFAULT NULL,
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `is_delete`   TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_tenant_code` (`tenant_id`, `code`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `user_role`
(
    `id`          BIGINT   NOT NULL,
    `tenant_id`   BIGINT   NOT NULL,
    `user_id`     BIGINT   NOT NULL,
    `role_id`     BIGINT   NOT NULL,
    `status`      TINYINT  NOT NULL DEFAULT 1,
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `is_delete`   TINYINT  NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_role_tenant_user_role` (`tenant_id`, `user_id`, `role_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `role_permission`
(
    `id`            BIGINT   NOT NULL,
    `tenant_id`     BIGINT   NOT NULL,
    `role_id`       BIGINT   NOT NULL,
    `permission_id` BIGINT   NOT NULL,
    `create_time`   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `is_delete`     TINYINT  NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_permission_tenant_role_perm` (`tenant_id`, `role_id`, `permission_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

