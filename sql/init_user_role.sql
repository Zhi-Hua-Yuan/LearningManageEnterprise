CREATE TABLE IF NOT EXISTS `user_role`
(
    `id`          BIGINT   NOT NULL COMMENT '关联主键',
    `tenant_id`   BIGINT   NOT NULL COMMENT '所属租户ID',
    `user_id`     BIGINT   NOT NULL COMMENT '用户ID',
    `role_id`     BIGINT   NOT NULL COMMENT '角色ID',
    `status`      TINYINT  NOT NULL DEFAULT 1 COMMENT '关联状态: 1有效, 0停用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_delete`   TINYINT  NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0未删除, 1已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_role_tenant_user_role` (`tenant_id`, `user_id`, `role_id`),
    KEY `idx_user_role_tenant_user` (`tenant_id`, `user_id`),
    KEY `idx_user_role_tenant_role` (`tenant_id`, `role_id`),
    KEY `idx_user_role_tenant_status` (`tenant_id`, `status`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户角色关联表';

