package com.spt.learningmanage.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.spt.learningmanage.model.entity.Project;
import com.spt.learningmanage.model.entity.Task;
import com.spt.learningmanage.model.entity.WeeklyReview;

/**
 * 周报访问控制入口。
 * 当前阶段基于 tenant + owner（weekly_review.user_id）进行访问校验。
 */
public interface WeeklyReviewAccessService {

    /**
     * 获取当前登录用户ID，不存在则抛未登录异常。
     */
    Long requireCurrentUserId();

    /**
     * 获取当前租户ID。
     */
    Long requireCurrentTenantId();

    /**
     * 创建带 owner 访问域的周报查询条件（tenant + user）。
     */
    LambdaQueryWrapper<WeeklyReview> ownedReviewQuery();

    /**
     * 按 owner 访问域查询指定周报。
     */
    WeeklyReview findOwnedReviewById(Long reviewId);

    /**
     * 按 owner 访问域查询指定年周周报。
     */
    WeeklyReview findOwnedReviewByYearWeek(Integer year, Integer weekNo);

    /**
     * 创建带 owner 访问域的任务查询条件（tenant + user）。
     */
    LambdaQueryWrapper<Task> ownedTaskQuery();

    /**
     * 创建带 owner 访问域的任务统计查询条件（tenant + user）。
     */
    QueryWrapper<Task> ownedTaskStatsQuery();

    /**
     * 创建带 owner 访问域的项目查询条件（tenant + user）。
     */
    LambdaQueryWrapper<Project> ownedProjectQuery();
}

