package com.spt.learningmanage.config;

import com.spt.learningmanage.interceptor.LoginInterceptor;
import com.spt.learningmanage.interceptor.TenantInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Resource
    private LoginInterceptor loginInterceptor;

    @Resource
    private TenantInterceptor tenantInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 先建立用户登录态，再解析租户上下文，便于后续基于登录态/JWT的租户来源扩展。
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/user/login",
                        "/user/register",
                        "/doc.html",
                        "/webjars/**",
                        "/swagger-resources/**",
                        "/v3/api-docs/**"
                );

        registry.addInterceptor(tenantInterceptor)
                .addPathPatterns("/**");
    }
}
