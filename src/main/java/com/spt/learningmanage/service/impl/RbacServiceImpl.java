package com.spt.learningmanage.service.impl;

import com.spt.learningmanage.constant.RoleConstants;
import com.spt.learningmanage.exception.BusinessException;
import com.spt.learningmanage.exception.ErrorCode;
import com.spt.learningmanage.mapper.AuthorizationMapper;
import com.spt.learningmanage.service.RbacService;
import com.spt.learningmanage.service.TenantService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class RbacServiceImpl implements RbacService {

    @Resource
    private TenantService tenantService;

    @Resource
    private AuthorizationMapper authorizationMapper;

    @Override
    public Set<String> getUserPermissions(Long userId, Long tenantId) {
        validateUserId(userId);
        validateTenantId(tenantId);
        List<String> permissionCodes = authorizationMapper.listPermissionCodesByUserAndTenant(tenantId, userId);
        if (permissionCodes == null || permissionCodes.isEmpty()) {
            return Set.of();
        }

        LinkedHashSet<String> normalizedCodes = new LinkedHashSet<>();
        for (String permissionCode : permissionCodes) {
            if (StringUtils.hasText(permissionCode)) {
                normalizedCodes.add(permissionCode.trim());
            }
        }
        return normalizedCodes;
    }

    @Override
    public boolean isSuperAdmin(Long userId, Long tenantId) {
        validateUserId(userId);
        validateTenantId(tenantId);
        Long count = authorizationMapper.countUserRoleByCode(tenantId, userId, RoleConstants.SUPER_ADMIN);
        return count != null && count > 0;
    }

    @Override
    public boolean hasPermission(Long userId, Long tenantId, String permissionCode) {
        validateUserId(userId);
        validateTenantId(tenantId);
        if (!StringUtils.hasText(permissionCode)) {
            return false;
        }
        Long count = authorizationMapper.countUserPermissionByCode(tenantId, userId, permissionCode.trim());
        return count != null && count > 0;
    }

    @Override
    public Set<String> listPermissionCodesByUser(Long userId) {
        return getUserPermissions(userId, tenantService.resolveCurrentTenantId());
    }

    @Override
    public boolean isSuperAdmin(Long userId) {
        return isSuperAdmin(userId, tenantService.resolveCurrentTenantId());
    }

    @Override
    public boolean hasPermission(Long userId, String permissionCode) {
        Long tenantId = tenantService.resolveCurrentTenantId();
        return hasPermission(userId, tenantId, permissionCode);
    }

    private static void validateUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不合法");
        }
    }

    private static void validateTenantId(Long tenantId) {
        if (tenantId == null || tenantId < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "租户ID不合法");
        }
    }
}
