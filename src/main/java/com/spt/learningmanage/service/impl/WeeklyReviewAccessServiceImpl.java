package com.spt.learningmanage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.spt.learningmanage.model.entity.Project;
import com.spt.learningmanage.model.entity.Task;
import com.spt.learningmanage.model.entity.WeeklyReview;
import com.spt.learningmanage.service.WeeklyReviewAccessService;
import com.spt.learningmanage.service.TenantService;
import com.spt.learningmanage.utils.UserHolder;
import com.spt.learningmanage.mapper.WeeklyReviewMapper;
import com.spt.learningmanage.exception.BusinessException;
import com.spt.learningmanage.exception.ErrorCode;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * 周报访问校验实现。
 * 当前仅采用 tenant_id + weekly_review.user_id（owner）模式，不引入协作新字段。
 */
@Service
public class WeeklyReviewAccessServiceImpl implements WeeklyReviewAccessService {

    @Resource
    private WeeklyReviewMapper weeklyReviewMapper;

    @Resource
    private TenantService tenantService;

    @Override
    public Long requireCurrentUserId() {
        Long userId = UserHolder.get();
        if (userId == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return userId;
    }

    @Override
    public Long requireCurrentTenantId() {
        return tenantService.resolveCurrentTenantId();
    }

    @Override
    public LambdaQueryWrapper<WeeklyReview> ownedReviewQuery() {
        return new LambdaQueryWrapper<WeeklyReview>()
                .eq(WeeklyReview::getTenantId, requireCurrentTenantId())
                .eq(WeeklyReview::getUserId, requireCurrentUserId());
    }

    @Override
    public WeeklyReview findOwnedReviewById(Long reviewId) {
        if (reviewId == null || reviewId <= 0) {
            return null;
        }
        LambdaQueryWrapper<WeeklyReview> wrapper = ownedReviewQuery();
        wrapper.eq(WeeklyReview::getId, reviewId).last("limit 1");
        return weeklyReviewMapper.selectOne(wrapper);
    }

    @Override
    public WeeklyReview findOwnedReviewByYearWeek(Integer year, Integer weekNo) {
        LambdaQueryWrapper<WeeklyReview> wrapper = ownedReviewQuery();
        wrapper.eq(WeeklyReview::getYear, year)
                .eq(WeeklyReview::getWeekNo, weekNo)
                .last("limit 1");
        return weeklyReviewMapper.selectOne(wrapper);
    }

    @Override
    public LambdaQueryWrapper<Task> ownedTaskQuery() {
        return new LambdaQueryWrapper<Task>()
                .eq(Task::getTenantId, requireCurrentTenantId())
                .eq(Task::getUserId, requireCurrentUserId());
    }

    @Override
    public QueryWrapper<Task> ownedTaskStatsQuery() {
        return new QueryWrapper<Task>()
                .eq("tenant_id", requireCurrentTenantId())
                .eq("user_id", requireCurrentUserId());
    }

    @Override
    public LambdaQueryWrapper<Project> ownedProjectQuery() {
        return new LambdaQueryWrapper<Project>()
                .eq(Project::getTenantId, requireCurrentTenantId())
                .eq(Project::getUserId, requireCurrentUserId());
    }
}

