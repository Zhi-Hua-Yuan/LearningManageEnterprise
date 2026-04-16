package com.spt.learningmanage.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.spt.learningmanage.model.entity.Milestone;
import com.spt.learningmanage.model.entity.Project;
import com.spt.learningmanage.model.entity.Task;

/**
 * 任务访问控制入口。
 * 当前阶段基于 tenant + owner（task.user_id）进行访问校验。
 */
public interface TaskAccessService {

    /**
     * 获取当前登录用户ID，不存在则抛未登录异常。
     */
    Long requireCurrentUserId();

    /**
     * 获取当前租户ID。
     */
    Long requireCurrentTenantId();

    /**
     * 创建带 owner 访问域的查询条件（tenant + user）。
     */
    LambdaQueryWrapper<Task> ownedQuery();

    /**
     * 创建带 owner 访问域的更新条件（tenant + user）。
     */
    LambdaUpdateWrapper<Task> ownedUpdate();

    /**
     * 校验并返回当前租户下、当前 owner 可访问的任务。
     */
    Task requireOwnedTask(Long taskId);

    /**
     * 校验任务创建时目标项目归属（tenant + owner，且项目未删除）。
     */
    Project requireAccessibleProjectForTaskCreate(Long projectId);

    /**
     * 创建任务统计查询的 owner 访问域条件（tenant + user）。
     */
    QueryWrapper<Task> ownedTaskStatsBaseQuery(Long tenantId);

    /**
     * 创建项目进度更新的 owner 访问域条件（tenant + user）。
     */
    UpdateWrapper<Project> ownedProjectProgressUpdate(Long projectId, Long tenantId);

    /**
     * 创建里程碑进度更新的 owner 访问域条件（tenant + user）。
     */
    UpdateWrapper<Milestone> ownedMilestoneProgressUpdate(Long milestoneId, Long tenantId);

    /**
     * 校验里程碑是否属于当前 owner 且挂在指定项目下。
     */
    void requireOwnedMilestoneInProject(Long milestoneId, Long tenantId, Long projectId);
}

