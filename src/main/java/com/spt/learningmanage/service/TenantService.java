package com.spt.learningmanage.service;

public interface TenantService {

    /**
     * 获取默认租户ID（兼容模式）。
     */
    Long getDefaultTenantId();

    /**
     * 获取当前请求上下文中的租户ID。
     */
    Long resolveCurrentTenantId();

    /**
     * 从请求头中解析租户ID。
     */
    Long parseTenantId(String tenantHeader, boolean required);

    /**
     * 校验租户是否存在且处于启用状态。
     */
    void validateTenantAvailable(Long tenantId);
}

