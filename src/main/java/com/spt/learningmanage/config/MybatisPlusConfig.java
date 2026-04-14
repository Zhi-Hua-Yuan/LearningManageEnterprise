package com.spt.learningmanage.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.spt.learningmanage.constant.TenantConstant;
import com.spt.learningmanage.utils.TenantHolder;
import jakarta.annotation.Resource;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Locale;
import java.util.Set;

@Configuration
public class MybatisPlusConfig {

    private static final Set<String> TENANT_CORE_TABLES = Set.of(
            "project", "task", "milestone", "weekly_review",
            "role", "user_role", "role_permission"
    );

    @Resource
    private TenantProperties tenantProperties;

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 第一轮仅对白名单核心表启用租户隔离，避免对复杂历史 SQL 产生非预期影响。
        if (tenantProperties.isMybatisTenantEnabled()) {
            interceptor.addInnerInterceptor(new TenantLineInnerInterceptor(new TenantLineHandler() {
                @Override
                public Expression getTenantId() {
                    Long tenantId = TenantHolder.get();
                    long safeTenantId = tenantId == null ? TenantConstant.DEFAULT_TENANT_ID : tenantId;
                    return new LongValue(safeTenantId);
                }

                @Override
                public String getTenantIdColumn() {
                    return "tenant_id";
                }

                @Override
                public boolean ignoreTable(String tableName) {
                    String normalized = tableName == null ? "" : tableName.toLowerCase(Locale.ROOT);
                    // 白名单策略：只有 core4 表参与自动租户拼接，其他表（含 tenant/user）默认忽略。
                    return !TENANT_CORE_TABLES.contains(normalized);
                }

                @Override
                public boolean ignoreInsert(java.util.List<net.sf.jsqlparser.schema.Column> columns, String tenantIdColumn) {
                    if (columns == null || columns.isEmpty()) {
                        return false;
                    }
                    // INSERT 优先使用业务层显式 tenant_id，已显式提供则跳过自动注入。
                    return columns.stream()
                            .map(net.sf.jsqlparser.schema.Column::getColumnName)
                            .anyMatch(name -> tenantIdColumn.equalsIgnoreCase(name));
                }
            }));
        }

        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
