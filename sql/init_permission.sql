CREATE TABLE IF NOT EXISTS `permission`
(
    `id`          BIGINT       NOT NULL COMMENT '权限主键',
    `code`        VARCHAR(64)  NOT NULL COMMENT '权限编码（资源:动作）',
    `name`        VARCHAR(128) NOT NULL COMMENT '权限名称',
    `resource`    VARCHAR(64)  NOT NULL COMMENT '资源标识',
    `action`      VARCHAR(64)  NOT NULL COMMENT '动作标识',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态: 1启用, 0停用',
    `remark`      VARCHAR(255)          DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_delete`   TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0未删除, 1已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_permission_code` (`code`),
    KEY `idx_permission_resource_action` (`resource`, `action`),
    KEY `idx_permission_status` (`status`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='权限字典表';

