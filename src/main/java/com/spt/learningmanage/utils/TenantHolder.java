package com.spt.learningmanage.utils;

/**
 * 租户上下文持有器，用于当前请求线程的租户信息存储与取用。
 */
public final class TenantHolder {

    private static final ThreadLocal<Long> TENANT_ID_HOLDER = new ThreadLocal<>();

    private TenantHolder() {
    }

    /**
     * 设置当前线程的租户ID。
     */
    public static void set(Long tenantId) {
        TENANT_ID_HOLDER.set(tenantId);
    }

    /**
     * 获取当前线程的租户ID。
     */
    public static Long get() {
        return TENANT_ID_HOLDER.get();
    }

    /**
     * 清除当前线程的租户ID，防止内存泄漏。
     */
    public static void remove() {
        TENANT_ID_HOLDER.remove();
    }
}

