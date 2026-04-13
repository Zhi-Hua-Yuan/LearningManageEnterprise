-- 租户主档案表（第一轮最小可用模型）
CREATE TABLE IF NOT EXISTS `tenant`
(
    `id`          BIGINT       NOT NULL COMMENT '租户主键ID',
    `code`        VARCHAR(64)  NOT NULL COMMENT '租户编码（系统级稳定标识）',
    `name`        VARCHAR(128) NOT NULL COMMENT '租户名称',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态: 1启用, 0停用',
    `is_default`  TINYINT      NOT NULL DEFAULT 0 COMMENT '是否默认租户: 1是, 0否',
    `remark`      VARCHAR(255)          DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_delete`   TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0未删除, 1已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_tenant_code` (`code`),
    KEY `idx_tenant_status` (`status`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='租户主档案表';

-- 默认租户（兼容历史单租户数据落点）
INSERT INTO `tenant` (`id`, `code`, `name`, `status`, `is_default`, `remark`, `is_delete`)
VALUES (0, 'default', '默认租户', 1, 1, '系统默认租户，兼容历史与未显式归属数据', 0)
ON DUPLICATE KEY UPDATE
    `code` = VALUES(`code`),
    `name` = VALUES(`name`),
    `status` = VALUES(`status`),
    `is_default` = VALUES(`is_default`),
    `remark` = VALUES(`remark`),
    `is_delete` = VALUES(`is_delete`);

