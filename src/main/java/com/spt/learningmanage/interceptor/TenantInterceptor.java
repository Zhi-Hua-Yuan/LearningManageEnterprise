package com.spt.learningmanage.interceptor;

import com.spt.learningmanage.config.TenantProperties;
import com.spt.learningmanage.service.TenantService;
import com.spt.learningmanage.utils.TenantHolder;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 租户解析拦截器，负责从请求中提取租户ID并绑定到当前线程。
 */
@Component
public class TenantInterceptor implements HandlerInterceptor {

    @Resource
    private TenantService tenantService;

    @Resource
    private TenantProperties tenantProperties;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        String tenantHeader = request.getHeader(tenantProperties.getHeaderName());
        Long tenantId = tenantService.parseTenantId(tenantHeader, tenantProperties.isRequired());
        if (tenantProperties.isValidationEnabled()) {
            tenantService.validateTenantAvailable(tenantId);
        }
        TenantHolder.set(tenantId);
        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                @NonNull Object handler, @Nullable Exception ex) {
        TenantHolder.remove();
    }
}

