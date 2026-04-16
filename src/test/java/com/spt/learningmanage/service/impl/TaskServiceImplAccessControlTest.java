package com.spt.learningmanage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spt.learningmanage.mapper.MilestoneMapper;
import com.spt.learningmanage.mapper.ProjectMapper;
import com.spt.learningmanage.mapper.TaskMapper;
import com.spt.learningmanage.model.dto.task.TaskCreateRequest;
import com.spt.learningmanage.model.dto.task.TaskQueryRequest;
import com.spt.learningmanage.model.dto.task.TaskUpdateRequest;
import com.spt.learningmanage.model.entity.Project;
import com.spt.learningmanage.model.entity.Task;
import com.spt.learningmanage.model.vo.task.TaskVo;
import com.spt.learningmanage.service.TaskAccessService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplAccessControlTest {

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private MilestoneMapper milestoneMapper;

    @Mock
    private TaskAccessService taskAccessService;

    @InjectMocks
    private TaskServiceImpl taskService;

    @Test
    void create_shouldUseUnifiedProjectAccessEntry() {
        Project project = new Project();
        project.setId(2001L);
        project.setTenantId(0L);
        project.setUserId(1001L);
        when(taskAccessService.requireCurrentUserId()).thenReturn(1001L);
        when(taskAccessService.requireAccessibleProjectForTaskCreate(2001L)).thenReturn(project);

        Task inserted = new Task();
        inserted.setId(3001L);
        when(taskMapper.insert(any(Task.class))).thenAnswer(invocation -> {
            Task arg = invocation.getArgument(0);
            arg.setId(inserted.getId());
            return 1;
        });
        when(taskAccessService.ownedTaskStatsBaseQuery(0L)).thenReturn(new QueryWrapper<>());
        when(taskAccessService.ownedProjectProgressUpdate(2001L, 0L)).thenReturn(new UpdateWrapper<>());
        when(projectMapper.update(eq(null), any(UpdateWrapper.class))).thenReturn(1);
        when(taskMapper.selectCount(any())).thenReturn(0L);

        TaskCreateRequest request = new TaskCreateRequest();
        request.setProjectId(2001L);
        request.setTitle("task-1");

        Long id = taskService.create(request);

        assertEquals(3001L, id);
        verify(taskAccessService).requireAccessibleProjectForTaskCreate(2001L);
    }

    @Test
    void getById_shouldUseUnifiedTaskAccessEntry() {
        Task task = new Task();
        task.setId(3001L);
        when(taskAccessService.requireOwnedTask(3001L)).thenReturn(task);

        TaskVo result = taskService.getById(3001L);

        assertNotNull(result);
        assertEquals(3001L, result.getId());
        verify(taskAccessService).requireOwnedTask(3001L);
    }

    @Test
    void list_shouldUseOwnedScopeFromAccessService() {
        @SuppressWarnings("unchecked")
        LambdaQueryWrapper<Task> ownedQueryWrapper = org.mockito.Mockito.mock(LambdaQueryWrapper.class);
        when(taskAccessService.ownedQuery()).thenReturn(ownedQueryWrapper);
        when(ownedQueryWrapper.eq(any(), any())).thenReturn(ownedQueryWrapper);
        when(ownedQueryWrapper.like(any(), any())).thenReturn(ownedQueryWrapper);
        when(ownedQueryWrapper.orderByDesc(org.mockito.Mockito.<SFunction<Task, ?>>any())).thenReturn(ownedQueryWrapper);

        Page<Task> pageResult = new Page<>(1, 10, 0);
        when(taskMapper.selectPage(any(Page.class), eq(ownedQueryWrapper))).thenReturn(pageResult);

        TaskQueryRequest request = new TaskQueryRequest();
        request.setStatus(0);
        request.setTitle("task");

        taskService.list(request);

        verify(taskAccessService).ownedQuery();
        verify(taskMapper).selectPage(any(Page.class), eq(ownedQueryWrapper));
    }

    @Test
    void update_shouldUseOwnedUpdateFromAccessService() {
        Task existing = new Task();
        existing.setId(3001L);
        existing.setTenantId(0L);
        existing.setUserId(1001L);
        existing.setProjectId(2001L);
        existing.setStatus(0);

        @SuppressWarnings("unchecked")
        LambdaUpdateWrapper<Task> ownedUpdateWrapper = org.mockito.Mockito.mock(LambdaUpdateWrapper.class);

        when(taskAccessService.requireOwnedTask(3001L)).thenReturn(existing);
        when(taskAccessService.ownedUpdate()).thenReturn(ownedUpdateWrapper);
        when(ownedUpdateWrapper.eq(any(), any())).thenReturn(ownedUpdateWrapper);
        when(ownedUpdateWrapper.set(any(), any())).thenReturn(ownedUpdateWrapper);
        when(taskMapper.update(eq(null), eq(ownedUpdateWrapper))).thenReturn(1);

        TaskUpdateRequest request = new TaskUpdateRequest();
        request.setId(3001L);
        request.setTitle("new title");

        taskService.update(request);

        verify(taskAccessService).requireOwnedTask(3001L);
        verify(taskAccessService).ownedUpdate();
        verify(taskMapper).update(eq(null), eq(ownedUpdateWrapper));
    }


    @Test
    void delete_shouldUseUnifiedTaskAccessEntry() {
        Task existing = new Task();
        existing.setId(3001L);
        existing.setStatus(0);
        existing.setProjectId(2001L);
        existing.setTenantId(0L);
        existing.setUserId(1001L);
        @SuppressWarnings("unchecked")
        LambdaQueryWrapper<Task> ownedQueryWrapper = org.mockito.Mockito.mock(LambdaQueryWrapper.class);
        when(taskAccessService.requireOwnedTask(3001L)).thenReturn(existing);
        when(taskAccessService.ownedQuery()).thenReturn(ownedQueryWrapper);
        when(taskAccessService.ownedTaskStatsBaseQuery(0L)).thenReturn(new QueryWrapper<>());
        when(taskAccessService.ownedProjectProgressUpdate(2001L, 0L)).thenReturn(new UpdateWrapper<>());
        when(ownedQueryWrapper.eq(any(), any())).thenReturn(ownedQueryWrapper);
        when(taskMapper.delete(eq(ownedQueryWrapper))).thenReturn(1);
        when(projectMapper.update(eq(null), any(UpdateWrapper.class))).thenReturn(1);
        when(taskMapper.selectCount(any())).thenReturn(0L);

        taskService.delete(3001L);

        verify(taskAccessService).requireOwnedTask(3001L);
        verify(taskAccessService).ownedQuery();
        verify(taskMapper).delete(eq(ownedQueryWrapper));
    }
}

