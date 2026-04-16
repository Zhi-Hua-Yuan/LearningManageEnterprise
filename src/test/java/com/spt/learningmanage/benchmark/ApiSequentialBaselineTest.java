package com.spt.learningmanage.benchmark;

import com.spt.learningmanage.utils.JwtUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = "classpath:sql/api_baseline_schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:sql/api_baseline_cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:sql/api_baseline_seed.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:sql/api_baseline_cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class ApiSequentialBaselineTest {

    private static final long USER_ID = 92001L;
    private static final String TENANT_ID = "100";
    private static final int ROUNDS = 10;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("最小串行基准：project list / task list / task stats / weekly review stats")
    void runSequentialApiBaseline() throws Exception {
        List<ScenarioResult> results = new ArrayList<>();

        results.add(measure("project list", ROUNDS, this::requestProjectList));
        results.add(measure("task list", ROUNDS, this::requestTaskList));
        results.add(measure("task stats", ROUNDS, this::requestTaskStats));
        results.add(measure("weekly review stats", ROUNDS, this::requestWeeklyReviewStats));

        printResults(results);
    }

    private void requestProjectList() throws Exception {
        mockMvc.perform(get("/project/list")
                        .header("Authorization", bearer())
                        .header("X-Tenant-Id", TENANT_ID)
                        .param("pageNum", "1")
                        .param("pageSize", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    private void requestTaskList() throws Exception {
        mockMvc.perform(get("/task/list")
                        .header("Authorization", bearer())
                        .header("X-Tenant-Id", TENANT_ID)
                        .param("current", "1")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    private void requestTaskStats() throws Exception {
        mockMvc.perform(get("/stats/overview")
                        .header("Authorization", bearer())
                        .header("X-Tenant-Id", TENANT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    private void requestWeeklyReviewStats() throws Exception {
        mockMvc.perform(get("/review/current")
                        .header("Authorization", bearer())
                        .header("X-Tenant-Id", TENANT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    private ScenarioResult measure(String name, int rounds, ThrowingRunnable runnable) throws Exception {
        long totalNanos = 0L;
        long minNanos = Long.MAX_VALUE;
        long maxNanos = Long.MIN_VALUE;

        for (int i = 0; i < rounds; i++) {
            long start = System.nanoTime();
            runnable.run();
            long elapsed = System.nanoTime() - start;

            totalNanos += elapsed;
            minNanos = Math.min(minNanos, elapsed);
            maxNanos = Math.max(maxNanos, elapsed);
        }

        return new ScenarioResult(name, rounds, totalNanos, minNanos, maxNanos);
    }

    private void printResults(List<ScenarioResult> results) {
        System.out.println("\n========== API Sequential Baseline ==========");
        System.out.println("rounds per scenario = " + ROUNDS);
        for (ScenarioResult result : results) {
            double totalMs = nanosToMillis(result.totalNanos());
            double avgMs = totalMs / result.rounds();
            double minMs = nanosToMillis(result.minNanos());
            double maxMs = nanosToMillis(result.maxNanos());
            System.out.printf("%-20s total=%.2fms avg=%.2fms min=%.2fms max=%.2fms%n",
                    result.name(), totalMs, avgMs, minMs, maxMs);
        }
        System.out.println("=============================================\n");
    }

    private double nanosToMillis(long nanos) {
        return nanos / 1_000_000.0;
    }

    private String bearer() {
        return "Bearer " + JwtUtils.createToken(USER_ID);
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Exception;
    }

    private record ScenarioResult(String name, int rounds, long totalNanos, long minNanos, long maxNanos) {
    }
}

