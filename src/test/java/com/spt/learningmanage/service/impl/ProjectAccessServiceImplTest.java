package com.spt.learningmanage.service.impl;

import com.spt.learningmanage.exception.BusinessException;
import com.spt.learningmanage.exception.ErrorCode;
import com.spt.learningmanage.constant.PermissionConstants;
import com.spt.learningmanage.mapper.ProjectMapper;
import com.spt.learningmanage.model.entity.Project;
import com.spt.learningmanage.service.RbacService;
import com.spt.learningmanage.service.TenantService;
import com.spt.learningmanage.utils.UserHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectAccessServiceImplTest {

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private TenantService tenantService;

    @Mock
    private RbacService rbacService;

    @InjectMocks
    private ProjectAccessServiceImpl projectAccessService;

    @AfterEach
    void tearDown() {
        UserHolder.remove();
    }

    @Test
    void requireOwnedProject_shouldReturnProject_whenMatchCurrentTenantAndUser() {
        UserHolder.set(1001L);
        when(tenantService.resolveCurrentTenantId()).thenReturn(0L);
        Project project = new Project();
        project.setId(2001L);
        project.setTenantId(0L);
        project.setUserId(1001L);
        when(projectMapper.selectOne(any())).thenReturn(project);

        Project result = projectAccessService.requireOwnedProject(2001L);

        assertSame(project, result);
    }

    @Test
    void requireOwnedProject_shouldThrowNotLogin_whenNoUserInContext() {
        BusinessException exception = assertThrows(BusinessException.class,
                () -> projectAccessService.requireOwnedProject(2001L));

        assertEquals(ErrorCode.NOT_LOGIN_ERROR.getCode(), exception.getErrorCode().getCode());
    }

    @Test
    void requireOwnedProject_shouldThrowProjectNotFound_whenNoOwnedProject() {
        UserHolder.set(1001L);
        when(tenantService.resolveCurrentTenantId()).thenReturn(0L);
        when(projectMapper.selectOne(any())).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> projectAccessService.requireOwnedProject(2001L));

        assertEquals(ErrorCode.PROJECT_NOT_FOUND.getCode(), exception.getErrorCode().getCode());
    }

    @Test
    void requireAccessibleProject_shouldReturnProject_whenHasTeamVisibilityPermission() {
        UserHolder.set(1001L);
        when(tenantService.resolveCurrentTenantId()).thenReturn(0L);

        Project teamVisibleProject = new Project();
        teamVisibleProject.setId(2002L);
        teamVisibleProject.setTenantId(0L);
        teamVisibleProject.setUserId(2002L);

        when(projectMapper.selectOne(any()))
                .thenReturn(null)
                .thenReturn(teamVisibleProject);
        when(rbacService.hasPermission(1001L, 0L, PermissionConstants.PROJECT_VIEW_TEAM)).thenReturn(true);

        Project result = projectAccessService.requireAccessibleProject(2002L);

        assertSame(teamVisibleProject, result);
    }
}

