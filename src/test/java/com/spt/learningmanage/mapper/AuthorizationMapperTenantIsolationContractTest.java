package com.spt.learningmanage.mapper;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthorizationMapperTenantIsolationContractTest {

    @Test
    void shouldContainTenantFiltersInCoreAuthorizationQueries() throws IOException {
        String xml = readMapperXml();

        assertTrue(xml.contains("<select id=\"listPermissionCodesByUserAndTenant\""));
        assertTrue(xml.contains("<select id=\"countUserRoleByCode\""));
        assertTrue(xml.contains("<select id=\"countUserPermissionByCode\""));

        assertTrue(xml.contains("ur.tenant_id = #{tenantId}"));
        assertTrue(xml.contains("r.tenant_id = #{tenantId}"));
        assertTrue(xml.contains("rp.tenant_id = #{tenantId}"));
    }

    private static String readMapperXml() throws IOException {
        try (InputStream inputStream = AuthorizationMapperTenantIsolationContractTest.class
                .getClassLoader()
                .getResourceAsStream("mapper/AuthorizationMapper.xml")) {
            if (inputStream == null) {
                throw new IOException("未找到 mapper/AuthorizationMapper.xml");
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}

