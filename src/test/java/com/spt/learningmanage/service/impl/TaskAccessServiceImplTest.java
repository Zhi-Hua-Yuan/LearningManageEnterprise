package com.spt.learningmanage.service.impl;

import com.spt.learningmanage.exception.BusinessException;
import com.spt.learningmanage.exception.ErrorCode;
import com.spt.learningmanage.mapper.MilestoneMapper;
import com.spt.learningmanage.mapper.TaskMapper;
import com.spt.learningmanage.model.entity.Project;
import com.spt.learningmanage.model.entity.Task;
import com.spt.learningmanage.service.ProjectAccessService;
import com.spt.learningmanage.service.TenantService;
import com.spt.learningmanage.utils.UserHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskAccessServiceImplTest {

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private MilestoneMapper milestoneMapper;

    @Mock
    private ProjectAccessService projectAccessService;

    @Mock
    private TenantService tenantService;

    @InjectMocks
    private TaskAccessServiceImpl taskAccessService;

    @AfterEach
    void tearDown() {
        UserHolder.remove();
    }

    @Test
    void requireOwnedTask_shouldReturnTask_whenTaskOwnedByCurrentTenantAndUser() {
        UserHolder.set(1001L);
        when(tenantService.resolveCurrentTenantId()).thenReturn(0L);
        Task task = new Task();
        task.setId(3001L);
        task.setTenantId(0L);
        task.setUserId(1001L);
        when(taskMapper.selectOne(any())).thenReturn(task);

        Task result = taskAccessService.requireOwnedTask(3001L);

        assertSame(task, result);
    }

    @Test
    void requireAccessibleProjectForTaskCreate_shouldReturnProject_whenAccessible() {
        Project project = new Project();
        project.setId(2001L);
        project.setTenantId(0L);
        project.setUserId(1001L);
        when(projectAccessService.requireAccessibleProject(2001L)).thenReturn(project);

        Project result = taskAccessService.requireAccessibleProjectForTaskCreate(2001L);

        assertSame(project, result);
    }

    @Test
    void requireOwnedTask_shouldThrowNotLogin_whenNoUserContext() {
        BusinessException exception = assertThrows(BusinessException.class,
                () -> taskAccessService.requireOwnedTask(3001L));

        assertEquals(ErrorCode.NOT_LOGIN_ERROR.getCode(), exception.getErrorCode().getCode());
    }

    @Test
    void ownedQuery_shouldBuildOwnedScope_whenUserAndTenantAvailable() {
        UserHolder.set(1001L);
        when(tenantService.resolveCurrentTenantId()).thenReturn(0L);

        assertNotNull(taskAccessService.ownedQuery());
        verify(tenantService).resolveCurrentTenantId();
    }

    @Test
    void ownedUpdate_shouldBuildOwnedScope_whenUserAndTenantAvailable() {
        UserHolder.set(1001L);
        when(tenantService.resolveCurrentTenantId()).thenReturn(0L);

        assertNotNull(taskAccessService.ownedUpdate());
        verify(tenantService).resolveCurrentTenantId();
    }

    @Test
    void ownedTaskStatsBaseQuery_shouldBuildOwnedScope_whenUserAndTenantAvailable() {
        UserHolder.set(1001L);

        assertNotNull(taskAccessService.ownedTaskStatsBaseQuery(0L));
    }
}

