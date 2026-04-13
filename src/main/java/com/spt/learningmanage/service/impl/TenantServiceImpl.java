package com.spt.learningmanage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.spt.learningmanage.constant.TenantConstant;
import com.spt.learningmanage.exception.BusinessException;
import com.spt.learningmanage.exception.ErrorCode;
import com.spt.learningmanage.mapper.TenantMapper;
import com.spt.learningmanage.model.entity.Tenant;
import com.spt.learningmanage.service.TenantService;
import com.spt.learningmanage.utils.TenantHolder;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Service
public class TenantServiceImpl implements TenantService {

    /**
     * 租户启用状态值。
     */
    private static final int ENABLED_STATUS = 1;

    @Resource
    private TenantMapper tenantMapper;

    @Override
    public Long getDefaultTenantId() {
        try {
            LambdaQueryWrapper<Tenant> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Tenant::getIsDefault, 1)
                    .eq(Tenant::getIsDelete, 0)
                    .last("limit 1");
            Tenant tenant = tenantMapper.selectOne(wrapper);
            if (tenant == null || tenant.getId() == null) {
                return TenantConstant.DEFAULT_TENANT_ID;
            }
            return tenant.getId();
        } catch (Exception ignored) {
            // 数据库异常时回退到常量默认值
            return TenantConstant.DEFAULT_TENANT_ID;
        }
    }

    @Override
    public Long resolveCurrentTenantId() {
        Long tenantId = TenantHolder.get();
        return tenantId == null ? getDefaultTenantId() : tenantId;
    }

    @Override
    public Long parseTenantId(String tenantHeader, boolean required) {
        if (!StringUtils.hasText(tenantHeader)) {
            if (required) {
                throw new BusinessException(ErrorCode.TENANT_CONTEXT_MISSING);
            }
            return getDefaultTenantId();
        }
        try {
            long tenantId = Long.parseLong(tenantHeader.trim());
            if (tenantId < 0) {
                throw new BusinessException(ErrorCode.TENANT_HEADER_INVALID);
            }
            return tenantId;
        } catch (NumberFormatException e) {
            throw new BusinessException(ErrorCode.TENANT_HEADER_INVALID);
        }
    }

    @Override
    public void validateTenantAvailable(Long tenantId) {
        Long targetTenantId = tenantId == null ? getDefaultTenantId() : tenantId;
        LambdaQueryWrapper<Tenant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Tenant::getId, targetTenantId)
                .eq(Tenant::getIsDelete, 0)
                .last("limit 1");
        Tenant tenant = tenantMapper.selectOne(wrapper);
        if (tenant == null) {
            throw new BusinessException(ErrorCode.TENANT_NOT_FOUND);
        }
        if (!Objects.equals(tenant.getStatus(), ENABLED_STATUS)) {
            throw new BusinessException(ErrorCode.TENANT_STATUS_INVALID);
        }
    }
}

