package com.spt.learningmanage.service;

import java.util.Set;

public interface RbacService {

    /**
     * 查询指定用户在指定租户下的权限编码集合。
     */
    Set<String> getUserPermissions(Long userId, Long tenantId);

    /**
     * 判断指定用户在指定租户下是否为超级管理员。
     */
    boolean isSuperAdmin(Long userId, Long tenantId);

    /**
     * 判断指定用户在指定租户下是否具备某个权限编码。
     */
    boolean hasPermission(Long userId, Long tenantId, String permissionCode);

    /**
     * 查询指定用户在当前租户下的权限编码集合。
     */
    Set<String> listPermissionCodesByUser(Long userId);

    /**
     * 判断指定用户在当前租户下是否为超级管理员。
     */
    boolean isSuperAdmin(Long userId);

    /**
     * 判断指定用户在当前租户下是否具备某个权限编码。
     */
    boolean hasPermission(Long userId, String permissionCode);
}
