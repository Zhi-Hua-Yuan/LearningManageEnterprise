CREATE TABLE IF NOT EXISTS `role_permission`
(
    `id`            BIGINT   NOT NULL COMMENT '关联主键',
    `tenant_id`     BIGINT   NOT NULL COMMENT '所属租户ID',
    `role_id`       BIGINT   NOT NULL COMMENT '角色ID',
    `permission_id` BIGINT   NOT NULL COMMENT '权限ID',
    `create_time`   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_delete`     TINYINT  NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0未删除, 1已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_permission_tenant_role_perm` (`tenant_id`, `role_id`, `permission_id`),
    KEY `idx_role_permission_tenant_role` (`tenant_id`, `role_id`),
    KEY `idx_role_permission_tenant_permission` (`tenant_id`, `permission_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='角色权限关联表';

