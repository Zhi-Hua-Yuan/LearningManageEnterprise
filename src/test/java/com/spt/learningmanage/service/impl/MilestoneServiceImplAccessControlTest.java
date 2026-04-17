package com.spt.learningmanage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.spt.learningmanage.mapper.MilestoneMapper;
import com.spt.learningmanage.model.dto.milestone.MilestoneQueryRequest;
import com.spt.learningmanage.model.dto.milestone.MilestoneUpdateRequest;
import com.spt.learningmanage.model.entity.Milestone;
import com.spt.learningmanage.model.entity.Project;
import com.spt.learningmanage.model.vo.milestone.MilestoneVo;
import com.spt.learningmanage.service.MilestoneAccessService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MilestoneServiceImplAccessControlTest {

    @Mock
    private MilestoneMapper milestoneMapper;

    @Mock
    private MilestoneAccessService milestoneAccessService;

    @InjectMocks
    private MilestoneServiceImpl milestoneService;

    @Test
    void list_shouldUseOwnedScopeFromAccessService() {
        @SuppressWarnings("unchecked")
        LambdaQueryWrapper<Milestone> ownedQueryWrapper = org.mockito.Mockito.mock(LambdaQueryWrapper.class);
        MilestoneQueryRequest request = new MilestoneQueryRequest();
        request.setProjectId(2001L);
        request.setKeyword("m1");

        Milestone milestone = new Milestone();
        milestone.setId(3001L);

        when(milestoneAccessService.requireOwnedProjectForMilestone(2001L)).thenReturn(new Project());
        when(milestoneAccessService.ownedQuery()).thenReturn(ownedQueryWrapper);
        when(ownedQueryWrapper.eq(any(), any())).thenReturn(ownedQueryWrapper);
        when(ownedQueryWrapper.like(any(), any())).thenReturn(ownedQueryWrapper);
        when(ownedQueryWrapper.orderByAsc(org.mockito.Mockito.<SFunction<Milestone, ?>>any())).thenReturn(ownedQueryWrapper);
        when(milestoneMapper.selectList(eq(ownedQueryWrapper))).thenReturn(List.of(milestone));

        List<MilestoneVo> result = milestoneService.list(request);

        assertEquals(1, result.size());
        verify(milestoneAccessService).requireOwnedProjectForMilestone(2001L);
        verify(milestoneAccessService).ownedQuery();
        verify(milestoneMapper).selectList(eq(ownedQueryWrapper));
    }

    @Test
    void update_shouldUseOwnedScopeFromAccessService() {
        @SuppressWarnings("unchecked")
        LambdaUpdateWrapper<Milestone> ownedUpdateWrapper = org.mockito.Mockito.mock(LambdaUpdateWrapper.class);
        Milestone existing = new Milestone();
        existing.setId(3001L);
        existing.setProjectId(2001L);

        MilestoneUpdateRequest request = new MilestoneUpdateRequest();
        request.setId(3001L);
        request.setName("milestone-updated");

        when(milestoneAccessService.requireOwnedMilestone(3001L)).thenReturn(existing);
        when(milestoneAccessService.ownedUpdate()).thenReturn(ownedUpdateWrapper);
        when(ownedUpdateWrapper.eq(any(), any())).thenReturn(ownedUpdateWrapper);
        when(ownedUpdateWrapper.set(any(), any())).thenReturn(ownedUpdateWrapper);
        when(milestoneMapper.update(eq(null), eq(ownedUpdateWrapper))).thenReturn(1);

        milestoneService.update(request);

        verify(milestoneAccessService).requireOwnedMilestone(3001L);
        verify(milestoneAccessService).ownedUpdate();
        verify(milestoneMapper).update(eq(null), eq(ownedUpdateWrapper));
    }

    @Test
    void delete_shouldUseOwnedScopeFromAccessService() {
        when(milestoneAccessService.requireOwnedMilestone(3001L)).thenReturn(new Milestone());
        when(milestoneMapper.softDeleteOwnedMilestone(any(), any(), eq(3001L), any(), any())).thenReturn(1);

        milestoneService.delete(3001L);

        verify(milestoneAccessService).requireOwnedMilestone(3001L);
        verify(milestoneMapper).softDeleteOwnedMilestone(any(), any(), eq(3001L), any(), any());
    }
}

