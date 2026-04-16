package com.spt.learningmanage.controller;

import com.spt.learningmanage.exception.ErrorCode;
import com.spt.learningmanage.utils.JwtUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = "classpath:sql/project_access_chain_schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:sql/project_access_chain_cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:sql/project_access_chain_seed.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:sql/project_access_chain_cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
/**
 * 协作预验收基线（owner-only 可见模型）。
 * 当前阶段仅验证：
 * 1) 同租户默认按 owner 可见；
 * 2) RBAC project:view 只改变接口准入，不改变 owner 可见范围；
 * 3) 跨租户严格不可见。
 */
class ProjectAccessChainIntegrationTest {

    private static final long USER_A = 92001L;
    private static final long USER_B = 92002L;
    private static final long USER_C = 92003L;

    private static final String TENANT_100 = "100";
    private static final String TENANT_200 = "200";

    private static final long PROJECT_A_ID = 70001L;
    private static final long PROJECT_B_ID = 70002L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("同租户A/B项目访问差异：A仅看A项目，B仅看B项目")
    void sameTenantUsersShouldSeeOwnProjectsOnly() throws Exception {
        mockMvc.perform(get("/project/list")
                        .header("Authorization", bearer(USER_A))
                        .header("X-Tenant-Id", TENANT_100)
                        .param("pageNum", "1")
                        .param("pageSize", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.records.length()").value(1))
                .andExpect(jsonPath("$.data.records[0].id").value(70001));

        mockMvc.perform(get("/project/list")
                        .header("Authorization", bearer(USER_B))
                        .header("X-Tenant-Id", TENANT_100)
                        .param("pageNum", "1")
                        .param("pageSize", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.records.length()").value(1))
                .andExpect(jsonPath("$.data.records[0].id").value(70002));
    }

    @Test
    @DisplayName("RBAC授权后访问变化：无project:view为403，授权后进入业务层返回项目不存在")
    void rbacGrantShouldChangeProjectGetAccessResult() throws Exception {
        jdbcTemplate.update("DELETE FROM `user_role` WHERE `id` = ?", 84001L);

        mockMvc.perform(get("/project/list")
                        .header("Authorization", bearer(USER_B))
                        .header("X-Tenant-Id", TENANT_100)
                        .param("pageNum", "1")
                        .param("pageSize", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.records.length()").value(1))
                .andExpect(jsonPath("$.data.records[0].id").value(PROJECT_B_ID));

        mockMvc.perform(get("/project/get/{id}", PROJECT_A_ID)
                        .header("Authorization", bearer(USER_B))
                        .header("X-Tenant-Id", TENANT_100))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(ErrorCode.FORBIDDEN_ERROR.getCode()));

        jdbcTemplate.update(
                "INSERT INTO `user_role` (`id`,`tenant_id`,`user_id`,`role_id`,`status`,`is_delete`) VALUES (?,?,?,?,?,?)",
                84001L, 100L, USER_B, 82001L, 1, 0
        );

        mockMvc.perform(get("/project/get/{id}", PROJECT_A_ID)
                        .header("Authorization", bearer(USER_B))
                        .header("X-Tenant-Id", TENANT_100))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.PROJECT_NOT_FOUND.getCode()));

        mockMvc.perform(get("/project/list")
                        .header("Authorization", bearer(USER_B))
                        .header("X-Tenant-Id", TENANT_100)
                        .param("pageNum", "1")
                        .param("pageSize", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.records.length()").value(1))
                .andExpect(jsonPath("$.data.records[0].id").value(PROJECT_B_ID));
    }

    @Test
    @DisplayName("不同租户隔离：租户200用户即使有project:view也不可见租户100项目")
    void crossTenantShouldRemainInvisible() throws Exception {
        mockMvc.perform(get("/project/get/{id}", PROJECT_A_ID)
                        .header("Authorization", bearer(USER_C))
                        .header("X-Tenant-Id", TENANT_200))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.PROJECT_NOT_FOUND.getCode()));

        mockMvc.perform(get("/project/list")
                        .header("Authorization", bearer(USER_C))
                        .header("X-Tenant-Id", TENANT_200)
                        .param("pageNum", "1")
                        .param("pageSize", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.records.length()").value(0));
    }

    @Test
    @DisplayName("同租户协作前半：B获得project:view_team后可在A项目下创建任务")
    void tenantTeamVisibilityGrantShouldAllowTaskCreateOnNonOwnerProject() throws Exception {
        mockMvc.perform(post("/task/add")
                        .header("Authorization", bearer(USER_B))
                        .header("X-Tenant-Id", TENANT_100)
                        .contentType(APPLICATION_JSON)
                        .content(taskCreatePayload(PROJECT_A_ID, "B-create-on-A-before-grant")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.PROJECT_NOT_FOUND.getCode()));

        jdbcTemplate.update(
                "INSERT INTO `role_permission` (`id`,`tenant_id`,`role_id`,`permission_id`,`is_delete`) VALUES (?,?,?,?,?)",
                83003L, 100L, 82001L, 10007L, 0
        );

        mockMvc.perform(post("/task/add")
                        .header("Authorization", bearer(USER_B))
                        .header("X-Tenant-Id", TENANT_100)
                        .contentType(APPLICATION_JSON)
                        .content(taskCreatePayload(PROJECT_A_ID, "B-create-on-A-after-grant")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        Integer createdCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM `task` WHERE `tenant_id` = ? AND `project_id` = ? AND `user_id` = ? AND `title` = ?",
                Integer.class,
                100L,
                PROJECT_A_ID,
                USER_B,
                "B-create-on-A-after-grant"
        );
        org.junit.jupiter.api.Assertions.assertEquals(1, createdCount);
    }

    private String bearer(Long userId) {
        return "Bearer " + JwtUtils.createToken(userId);
    }

    private String taskCreatePayload(long projectId, String title) {
        return String.format("{\"projectId\":%d,\"title\":\"%s\"}", projectId, title);
    }
}

