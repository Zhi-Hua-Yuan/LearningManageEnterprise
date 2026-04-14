package com.spt.learningmanage.aspect;

import com.spt.learningmanage.annotation.RequirePermission;
import com.spt.learningmanage.exception.BusinessException;
import com.spt.learningmanage.exception.ErrorCode;
import com.spt.learningmanage.exception.ForbiddenException;
import com.spt.learningmanage.service.CurrentUserProvider;
import com.spt.learningmanage.service.RbacService;
import com.spt.learningmanage.utils.TenantHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequirePermissionAspectTest {

    @Mock
    private CurrentUserProvider currentUserProvider;

    @Mock
    private RbacService rbacService;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @InjectMocks
    private RequirePermissionAspect requirePermissionAspect;

    private static final Long USER_ID = 1001L;
    private static final Long TENANT_ID = 0L;

    @AfterEach
    void cleanup() {
        TenantHolder.remove();
    }

    @Test
    void around_shouldPass_whenUserIsSuperAdmin() throws Throwable {
        when(currentUserProvider.getRequiredCurrentUserId()).thenReturn(USER_ID);
        TenantHolder.set(TENANT_ID);
        when(rbacService.isSuperAdmin(USER_ID, TENANT_ID)).thenReturn(true);
        when(joinPoint.proceed()).thenReturn("ok");

        Object result = requirePermissionAspect.around(joinPoint, requirePermission("project:update"));

        assertEquals("ok", result);
        verify(rbacService).isSuperAdmin(USER_ID, TENANT_ID);
        verify(rbacService, never()).hasPermission(USER_ID, TENANT_ID, "project:update");
        verify(joinPoint).proceed();
    }

    @Test
    void around_shouldPass_whenUserHasPermission() throws Throwable {
        when(currentUserProvider.getRequiredCurrentUserId()).thenReturn(USER_ID);
        TenantHolder.set(TENANT_ID);
        when(rbacService.isSuperAdmin(USER_ID, TENANT_ID)).thenReturn(false);
        when(rbacService.hasPermission(USER_ID, TENANT_ID, "project:view")).thenReturn(true);
        when(joinPoint.proceed()).thenReturn("ok");

        Object result = requirePermissionAspect.around(joinPoint, requirePermission("project:view"));

        assertEquals("ok", result);
        verify(rbacService).hasPermission(USER_ID, TENANT_ID, "project:view");
        verify(joinPoint).proceed();
    }

    @Test
    void around_shouldThrowForbiddenException_whenNoPermission() {
        when(currentUserProvider.getRequiredCurrentUserId()).thenReturn(USER_ID);
        TenantHolder.set(TENANT_ID);
        when(rbacService.isSuperAdmin(USER_ID, TENANT_ID)).thenReturn(false);
        when(rbacService.hasPermission(USER_ID, TENANT_ID, "project:delete")).thenReturn(false);

        ForbiddenException exception = assertThrows(ForbiddenException.class,
                () -> requirePermissionAspect.around(joinPoint, requirePermission("project:delete")));

        assertEquals(ErrorCode.FORBIDDEN_ERROR, exception.getErrorCode());
    }

    @Test
    void around_shouldThrowNotLogin_whenUserMissing() {
        TenantHolder.set(TENANT_ID);
        when(currentUserProvider.getRequiredCurrentUserId()).thenThrow(new BusinessException(ErrorCode.NOT_LOGIN_ERROR));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> requirePermissionAspect.around(joinPoint, requirePermission("project:view")));

        assertEquals(ErrorCode.NOT_LOGIN_ERROR, exception.getErrorCode());
    }

    @Test
    void around_shouldThrowTenantContextMissing_whenTenantMissing() {
        when(currentUserProvider.getRequiredCurrentUserId()).thenReturn(USER_ID);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> requirePermissionAspect.around(joinPoint, requirePermission("project:view")));

        assertEquals(ErrorCode.TENANT_CONTEXT_MISSING, exception.getErrorCode());
    }

    private static RequirePermission requirePermission(String code) {
        try {
            Method method = StubController.class.getDeclaredMethod("securedMethod");
            RequirePermission annotation = method.getAnnotation(RequirePermission.class);
            return new RequirePermission() {
                @Override
                public String value() {
                    return code;
                }

                @Override
                public Class<? extends java.lang.annotation.Annotation> annotationType() {
                    return annotation.annotationType();
                }
            };
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    static class StubController {
        @RequirePermission("project:view")
        public void securedMethod() {
        }
    }
}
