package com.spt.learningmanage.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.spt.learningmanage.model.entity.Milestone;
import com.spt.learningmanage.model.entity.Project;

/**
 * 里程碑访问控制入口。
 * 当前阶段基于 tenant + owner（milestone.user_id）进行访问校验。
 */
public interface MilestoneAccessService {

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
    LambdaQueryWrapper<Milestone> ownedQuery();

    /**
     * 创建带 owner 访问域的更新条件（tenant + user）。
     */
    LambdaUpdateWrapper<Milestone> ownedUpdate();

    /**
     * 校验并返回当前租户下、当前 owner 可访问的里程碑。
     */
    Milestone requireOwnedMilestone(Long milestoneId);

    /**
     * 校验里程碑操作时目标项目归属（tenant + owner，且项目未删除）。
     */
    Project requireOwnedProjectForMilestone(Long projectId);
}

