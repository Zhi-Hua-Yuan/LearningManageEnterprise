package com.spt.learningmanage.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "tenant")
public class TenantProperties {

    /**
     * 用于传递租户ID的请求头名称。
     */
    private String headerName = "X-Tenant-Id";

    /**
     * 是否在拦截器中启用租户可用性校验。
     */
    private boolean validationEnabled = false;

    /**
     * 是否强制每个请求都需要包含租户头。
     */
    private boolean required = false;

    /**
     * 是否启用 MyBatis 层租户拦截（建议按环境灰度开启）。
     */
    private boolean mybatisTenantEnabled = false;
}
