package com.spt.learningmanage.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AuthorizationMapper {

    /**
     * 查询用户在指定租户下的权限编码集合。
     */
    List<String> listPermissionCodesByUserAndTenant(@Param("tenantId") Long tenantId, @Param("userId") Long userId);

    /**
     * 统计用户在指定租户下是否拥有目标角色编码。
     */
    Long countUserRoleByCode(@Param("tenantId") Long tenantId,
                             @Param("userId") Long userId,
                             @Param("roleCode") String roleCode);

    /**
     * 统计用户在指定租户下是否拥有目标权限编码。
     */
    Long countUserPermissionByCode(@Param("tenantId") Long tenantId,
                                   @Param("userId") Long userId,
                                   @Param("permissionCode") String permissionCode);
}
