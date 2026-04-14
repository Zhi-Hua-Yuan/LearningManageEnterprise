CREATE TABLE IF NOT EXISTS `role`
(
    `id`          BIGINT       NOT NULL COMMENT '角色主键',
    `tenant_id`   BIGINT       NOT NULL COMMENT '所属租户ID',
    `code`        VARCHAR(64)  NOT NULL COMMENT '角色编码（租户内稳定标识）',
    `name`        VARCHAR(128) NOT NULL COMMENT '角色名称',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态: 1启用, 0停用',
    `is_system`   TINYINT      NOT NULL DEFAULT 0 COMMENT '是否系统内置角色: 1是, 0否',
    `remark`      VARCHAR(255)          DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_delete`   TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0未删除, 1已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_tenant_code` (`tenant_id`, `code`),
    KEY `idx_role_tenant_id` (`tenant_id`),
    KEY `idx_role_tenant_status` (`tenant_id`, `status`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='租户角色表';

