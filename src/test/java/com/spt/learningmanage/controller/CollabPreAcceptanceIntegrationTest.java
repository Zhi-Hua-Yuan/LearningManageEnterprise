package com.spt.learningmanage.controller;

import com.spt.learningmanage.exception.ErrorCode;
import com.spt.learningmanage.utils.JwtUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
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
class CollabPreAcceptanceIntegrationTest {

    private static final long USER_B = 92002L;
    private static final long USER_C = 92003L;

    private static final String TENANT_100 = "100";

    private static final long PROJECT_A_ID = 70001L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("#3 同租户协作：B获得project:view_team后可在A项目下创建任务")
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
        Assertions.assertEquals(1, createdCount);
    }

    @Test
    @Disabled("blocked by assignee model: /task/assign is placeholder and assignee persistence is not implemented")
    @DisplayName("#4 待启用：任务指派给C后，C可查看并更新")
    void assigneeCanReadAndUpdateAfterAssignment_pendingModel() throws Exception {
        // 预期路径（当前未实现，先保留可执行骨架）：
        // 1) 用户B对任务执行 /task/assign（写入 assignee_user_id = USER_C）
        // 2) 用户C调用 /task/get/{id} 成功，返回任务详情
        // 3) 用户C调用 /task/update 成功，任务字段发生变化
        // 4) 跨租户用户仍不可读不可改

        mockMvc.perform(post("/task/assign")
                        .header("Authorization", bearer(USER_B))
                        .header("X-Tenant-Id", TENANT_100)
                        .contentType(APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());

        // NOTE: enable and complete assertions after assignee model + assignment persistence are merged.
        Assertions.assertTrue(USER_C > 0);
    }

    private String bearer(Long userId) {
        return "Bearer " + JwtUtils.createToken(userId);
    }

    private String taskCreatePayload(long projectId, String title) {
        return String.format("{\"projectId\":%d,\"title\":\"%s\"}", projectId, title);
    }
}

