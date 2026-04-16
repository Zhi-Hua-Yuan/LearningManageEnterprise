package com.spt.learningmanage.service.impl;

import com.spt.learningmanage.mapper.AuthorizationMapper;
import com.spt.learningmanage.service.TenantService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RbacServiceImplTest {

    @Mock
    private TenantService tenantService;

    @Mock
    private AuthorizationMapper authorizationMapper;

    @InjectMocks
    private RbacServiceImpl rbacService;

    private static final Long TENANT_ID = 0L;
    private static final Long OTHER_TENANT_ID = 1L;
    private static final Long USER_ID = 1001L;

    @Test
    void getUserPermissions_shouldReturnMergedPermissionCodes() {
        when(authorizationMapper.listPermissionCodesByUserAndTenant(TENANT_ID, USER_ID))
                .thenReturn(List.of("project:create", "project:view", "project:view"));

        Set<String> codes = rbacService.getUserPermissions(USER_ID, TENANT_ID);

        assertEquals(2, codes.size());
        assertTrue(codes.contains("project:create"));
        assertTrue(codes.contains("project:view"));
    }

    @Test
    void getUserPermissions_shouldReturnEmpty_whenUserHasNoRole() {
        when(authorizationMapper.listPermissionCodesByUserAndTenant(TENANT_ID, USER_ID)).thenReturn(List.of());

        Set<String> codes = rbacService.getUserPermissions(USER_ID, TENANT_ID);

        assertTrue(codes.isEmpty());
    }

    @Test
    void listPermissionCodesByUser_shouldDelegateToCurrentTenant() {
        when(tenantService.resolveCurrentTenantId()).thenReturn(TENANT_ID);
        when(authorizationMapper.listPermissionCodesByUserAndTenant(TENANT_ID, USER_ID))
                .thenReturn(List.of("project:view"));

        Set<String> codes = rbacService.listPermissionCodesByUser(USER_ID);

        assertEquals(Set.of("project:view"), codes);
        verify(tenantService).resolveCurrentTenantId();
    }

    @Test
    void hasPermission_shouldReturnFalse_whenCodeNotGranted() {
        when(authorizationMapper.countUserPermissionByCode(TENANT_ID, USER_ID, "project:delete")).thenReturn(0L);

        assertFalse(rbacService.hasPermission(USER_ID, TENANT_ID, "project:delete"));
    }

    @Test
    void hasPermission_shouldReturnTrue_whenCodeGranted() {
        when(authorizationMapper.countUserPermissionByCode(TENANT_ID, USER_ID, "project:view")).thenReturn(1L);

        assertTrue(rbacService.hasPermission(USER_ID, TENANT_ID, "project:view"));
    }

    @Test
    void isSuperAdmin_shouldReturnTrue_whenUserHasSuperAdminRoleInTenant() {
        when(authorizationMapper.countUserRoleByCode(TENANT_ID, USER_ID, "super_admin")).thenReturn(1L);

        assertTrue(rbacService.isSuperAdmin(USER_ID, TENANT_ID));
    }

    @Test
    void isSuperAdmin_shouldBeTenantScoped_whenSameUserInDifferentTenant() {
        // 对齐种子语义：super_admin 是租户内角色，同一用户不应跨租户自动放行。
        when(authorizationMapper.countUserRoleByCode(TENANT_ID, USER_ID, "super_admin")).thenReturn(1L);
        when(authorizationMapper.countUserRoleByCode(OTHER_TENANT_ID, USER_ID, "super_admin")).thenReturn(0L);

        assertTrue(rbacService.isSuperAdmin(USER_ID, TENANT_ID));
        assertFalse(rbacService.isSuperAdmin(USER_ID, OTHER_TENANT_ID));
    }

    @Test
    void isSuperAdmin_shouldUseCurrentTenant_whenTenantParamNotProvided() {
        when(tenantService.resolveCurrentTenantId()).thenReturn(TENANT_ID);
        when(authorizationMapper.countUserRoleByCode(TENANT_ID, USER_ID, "super_admin")).thenReturn(1L);

        assertTrue(rbacService.isSuperAdmin(USER_ID));
        verify(tenantService).resolveCurrentTenantId();
    }
}
