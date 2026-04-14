package com.spt.learningmanage.aspect;

import com.spt.learningmanage.annotation.RequirePermission;
import com.spt.learningmanage.exception.BusinessException;
import com.spt.learningmanage.exception.ErrorCode;
import com.spt.learningmanage.exception.ForbiddenException;
import com.spt.learningmanage.service.CurrentUserProvider;
import com.spt.learningmanage.service.RbacService;
import com.spt.learningmanage.utils.TenantHolder;
import jakarta.annotation.Resource;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 权限注解切面，负责在方法执行前校验权限。
 */
@Aspect
@Component
public class RequirePermissionAspect {

    @Resource
    private CurrentUserProvider currentUserProvider;

    @Resource
    private RbacService rbacService;

    @Around("@annotation(requirePermission)")
    public Object around(ProceedingJoinPoint joinPoint, RequirePermission requirePermission) throws Throwable {
        Long userId = currentUserProvider.getRequiredCurrentUserId();

        Long tenantId = TenantHolder.get();
        if (tenantId == null || tenantId < 0) {
            throw new BusinessException(ErrorCode.TENANT_CONTEXT_MISSING);
        }

        String permissionCode = requirePermission.value();
        if (!StringUtils.hasText(permissionCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "权限编码不能为空");
        }

        String normalizedPermissionCode = permissionCode.trim();
        if (rbacService.isSuperAdmin(userId, tenantId)
                || rbacService.hasPermission(userId, tenantId, normalizedPermissionCode)) {
            return joinPoint.proceed();
        }

        throw new ForbiddenException();
    }
}
