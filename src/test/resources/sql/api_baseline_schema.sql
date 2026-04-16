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

CREATE TABLE IF NOT EXISTS `weekly_review`
(
    `id`                   BIGINT       NOT NULL AUTO_INCREMENT,
    `tenant_id`            BIGINT       NOT NULL DEFAULT 0,
    `user_id`              BIGINT       NOT NULL,
    `year`                 INT          NOT NULL,
    `week_no`              INT          NOT NULL,
    `start_date`           DATE         NOT NULL,
    `end_date`             DATE         NOT NULL,
    `completed_task_count` INT          NOT NULL DEFAULT 0,
    `focus_project_name`   VARCHAR(100)          DEFAULT NULL,
    `reflection`           TEXT                  DEFAULT NULL,
    `next_plan`            TEXT                  DEFAULT NULL,
    `create_time`          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_weekly_review_tenant_user_year_week` (`tenant_id`, `user_id`, `year`, `week_no`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

