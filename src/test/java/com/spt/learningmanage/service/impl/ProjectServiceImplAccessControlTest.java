package com.spt.learningmanage.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.spt.learningmanage.constant.ProjectConstant;
import com.spt.learningmanage.exception.BusinessException;
import com.spt.learningmanage.exception.ErrorCode;
import com.spt.learningmanage.mapper.ProjectMapper;
import com.spt.learningmanage.model.dto.project.ProjectUpdateRequest;
import com.spt.learningmanage.model.entity.Project;
import com.spt.learningmanage.model.vo.project.ProjectVo;
import com.spt.learningmanage.service.ProjectAccessService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplAccessControlTest {

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private ProjectAccessService projectAccessService;

    @InjectMocks
    private ProjectServiceImpl projectService;

    @Test
    void getById_shouldDelegateAccessCheckToProjectAccessService() {
        Project project = buildProject();
        when(projectAccessService.requireOwnedProject(1L)).thenReturn(project);

        ProjectVo result = projectService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(projectAccessService).requireOwnedProject(1L);
    }

    @Test
    void update_shouldUseUnifiedAccessCheck() {
        Project existing = buildProject();
        when(projectAccessService.requireOwnedProject(1L)).thenReturn(existing);
        when(projectMapper.updateById(any(Project.class))).thenReturn(1);
        ProjectUpdateRequest request = new ProjectUpdateRequest();
        request.setId(1L);

        projectService.update(request);

        verify(projectAccessService).requireOwnedProject(1L);
        verify(projectMapper).updateById(any(Project.class));
    }

    @Test
    void delete_shouldUseUnifiedAccessCheck() {
        Project existing = buildProject();
        when(projectAccessService.requireOwnedProject(1L)).thenReturn(existing);
        when(projectMapper.updateById(any(Project.class))).thenReturn(1);

        projectService.delete(1L);

        verify(projectAccessService).requireOwnedProject(1L);
        verify(projectMapper).updateById(any(Project.class));
    }

    @Test
    void recover_shouldUseUnifiedAccessCheck() {
        Project existing = buildProject();
        existing.setDeletedAt(LocalDateTime.now().minusDays(1));
        when(projectAccessService.requireOwnedProject(1L)).thenReturn(existing);
        when(projectMapper.updateById(any(Project.class))).thenReturn(1);

        projectService.recover(1L);

        verify(projectAccessService).requireOwnedProject(1L);
        verify(projectMapper).updateById(any(Project.class));
    }

    @Test
    void archive_shouldUseOwnedScopeFromAccessService() {
        Project existing = buildProject();
        @SuppressWarnings("unchecked")
        LambdaUpdateWrapper<Project> ownedUpdateWrapper = mock(LambdaUpdateWrapper.class);
        when(projectAccessService.listOwnedProjectsByIds(List.of(1L))).thenReturn(List.of(existing));
        when(projectAccessService.ownedUpdate()).thenReturn(ownedUpdateWrapper);
        when(ownedUpdateWrapper.in(any(), anyCollection())).thenReturn(ownedUpdateWrapper);
        when(ownedUpdateWrapper.set(any(), any())).thenReturn(ownedUpdateWrapper);
        when(projectMapper.update(any(), any())).thenReturn(1);

        projectService.archive(List.of(1L));

        verify(projectAccessService).listOwnedProjectsByIds(List.of(1L));
        verify(projectAccessService).ownedUpdate();
        verify(projectMapper).update(any(), any());
    }

    @Test
    void getById_shouldKeepOriginalParamValidation() {
        BusinessException exception = assertThrows(BusinessException.class, () -> projectService.getById(null));

        assertEquals(ErrorCode.PARAMS_ERROR.getCode(), exception.getErrorCode().getCode());
    }

    private Project buildProject() {
        Project project = new Project();
        project.setId(1L);
        project.setTenantId(0L);
        project.setUserId(1001L);
        project.setName("Demo");
        project.setStatus(ProjectConstant.STATUS_ACTIVE);
        project.setStartDate(LocalDate.now());
        project.setEndDate(LocalDate.now().plusDays(1));
        return project;
    }
}

