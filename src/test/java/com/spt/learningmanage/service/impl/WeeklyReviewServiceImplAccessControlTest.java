package com.spt.learningmanage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.spt.learningmanage.mapper.ProjectMapper;
import com.spt.learningmanage.mapper.TaskMapper;
import com.spt.learningmanage.mapper.WeeklyReviewMapper;
import com.spt.learningmanage.model.entity.WeeklyReview;
import com.spt.learningmanage.service.WeeklyReviewAccessService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WeeklyReviewServiceImplAccessControlTest {

    @Mock
    private WeeklyReviewMapper weeklyReviewMapper;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private WeeklyReviewAccessService weeklyReviewAccessService;

    @InjectMocks
    private WeeklyReviewServiceImpl weeklyReviewService;

    @Test
    void getReviewById_shouldUseAccessServiceScopeQuery() {
        WeeklyReview review = new WeeklyReview();
        review.setId(7001L);
        when(weeklyReviewAccessService.findOwnedReviewById(7001L)).thenReturn(review);

        WeeklyReview result = weeklyReviewService.getReviewById(7001L);

        assertSame(review, result);
        verify(weeklyReviewAccessService).findOwnedReviewById(7001L);
    }

    @Test
    void deleteReview_shouldUseAccessServiceOwnedQuery() {
        @SuppressWarnings("unchecked")
        LambdaQueryWrapper<WeeklyReview> ownedReviewWrapper = org.mockito.Mockito.mock(LambdaQueryWrapper.class);
        WeeklyReview review = new WeeklyReview();
        review.setId(7001L);

        when(weeklyReviewAccessService.findOwnedReviewById(7001L)).thenReturn(review);
        when(weeklyReviewAccessService.ownedReviewQuery()).thenReturn(ownedReviewWrapper);
        when(ownedReviewWrapper.eq(any(), any())).thenReturn(ownedReviewWrapper);
        when(weeklyReviewMapper.delete(eq(ownedReviewWrapper))).thenReturn(1);

        weeklyReviewService.deleteReview(7001L);

        verify(weeklyReviewAccessService).findOwnedReviewById(7001L);
        verify(weeklyReviewAccessService).ownedReviewQuery();
        verify(weeklyReviewMapper).delete(eq(ownedReviewWrapper));
    }

    @Test
    void listHistory_shouldUseAccessServiceOwnedQuery() {
        @SuppressWarnings("unchecked")
        LambdaQueryWrapper<WeeklyReview> ownedReviewWrapper = org.mockito.Mockito.mock(LambdaQueryWrapper.class);
        when(weeklyReviewAccessService.ownedReviewQuery()).thenReturn(ownedReviewWrapper);
        when(ownedReviewWrapper.orderByDesc(org.mockito.Mockito.<SFunction<WeeklyReview, ?>>any())).thenReturn(ownedReviewWrapper);
        when(weeklyReviewMapper.selectList(eq(ownedReviewWrapper))).thenReturn(List.of(new WeeklyReview()));

        List<WeeklyReview> result = weeklyReviewService.listHistory();

        assertEquals(1, result.size());
        verify(weeklyReviewAccessService).ownedReviewQuery();
        verify(weeklyReviewMapper).selectList(eq(ownedReviewWrapper));
    }
}

