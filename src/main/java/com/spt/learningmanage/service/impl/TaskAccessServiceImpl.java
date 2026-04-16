package com.spt.learningmanage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.spt.learningmanage.mapper.MilestoneMapper;
import com.spt.learningmanage.exception.BusinessException;
import com.spt.learningmanage.exception.ErrorCode;
import com.spt.learningmanage.mapper.TaskMapper;
import com.spt.learningmanage.model.entity.Milestone;
import com.spt.learningmanage.model.entity.Project;
import com.spt.learningmanage.model.entity.Task;
import com.spt.learningmanage.service.ProjectAccessService;
import com.spt.learningmanage.service.TaskAccessService;
import com.spt.learningmanage.service.TenantService;
import com.spt.learningmanage.utils.UserHolder;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * 任务访问校验实现。
 * 当前仅采用 tenant_id + task.user_id（owner）模式，不引入 assignee 或协作新字段。
 */
@Service
public class TaskAccessServiceImpl implements TaskAccessService {

    @Resource
    private TaskMapper taskMapper;

    @Resource
    private MilestoneMapper milestoneMapper;

    @Resource
    private ProjectAccessService projectAccessService;

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
    public LambdaQueryWrapper<Task> ownedQuery() {
        return new LambdaQueryWrapper<Task>()
                .eq(Task::getTenantId, requireCurrentTenantId())
                .eq(Task::getUserId, requireCurrentUserId());
    }

    @Override
    public LambdaUpdateWrapper<Task> ownedUpdate() {
        return new LambdaUpdateWrapper<Task>()
                .eq(Task::getTenantId, requireCurrentTenantId())
                .eq(Task::getUserId, requireCurrentUserId());
    }

    @Override
    public Task requireOwnedTask(Long taskId) {
        requireCurrentUserId();
        if (taskId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "任务 ID 不能为空");
        }
        LambdaQueryWrapper<Task> wrapper = ownedQuery();
        wrapper.eq(Task::getId, taskId);
        Task task = taskMapper.selectOne(wrapper);
        if (task == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "任务不存在");
        }
        return task;
    }

    @Override
    public Project requireAccessibleProjectForTaskCreate(Long projectId) {
        if (projectId == null || projectId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "项目 ID 不合法");
        }
        return projectAccessService.requireAccessibleProject(projectId);
    }

    @Override
    public QueryWrapper<Task> ownedTaskStatsBaseQuery(Long tenantId) {
        Long activeTenantId = tenantId == null ? requireCurrentTenantId() : tenantId;
        return new QueryWrapper<Task>()
                .eq("tenant_id", activeTenantId)
                .eq("user_id", requireCurrentUserId());
    }

    @Override
    public UpdateWrapper<Project> ownedProjectProgressUpdate(Long projectId, Long tenantId) {
        if (projectId == null || projectId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "项目 ID 不合法");
        }
        Long activeTenantId = tenantId == null ? requireCurrentTenantId() : tenantId;
        return new UpdateWrapper<Project>()
                .eq("id", projectId)
                .eq("tenant_id", activeTenantId)
                .eq("user_id", requireCurrentUserId());
    }

    @Override
    public UpdateWrapper<Milestone> ownedMilestoneProgressUpdate(Long milestoneId, Long tenantId) {
        if (milestoneId == null || milestoneId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "里程碑 ID 不合法");
        }
        Long activeTenantId = tenantId == null ? requireCurrentTenantId() : tenantId;
        return new UpdateWrapper<Milestone>()
                .eq("id", milestoneId)
                .eq("tenant_id", activeTenantId)
                .eq("user_id", requireCurrentUserId());
    }

    @Override
    public void requireOwnedMilestoneInProject(Long milestoneId, Long tenantId, Long projectId) {
        if (milestoneId == null || milestoneId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "里程碑 ID 不合法");
        }
        if (projectId == null || projectId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "项目 ID 不合法");
        }
        Long activeTenantId = tenantId == null ? requireCurrentTenantId() : tenantId;
        LambdaQueryWrapper<Milestone> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Milestone::getId, milestoneId)
                .eq(Milestone::getTenantId, activeTenantId)
                .eq(Milestone::getProjectId, projectId)
                .eq(Milestone::getUserId, requireCurrentUserId());
        Milestone milestone = milestoneMapper.selectOne(wrapper);
        if (milestone == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "里程碑不存在或不属于当前项目");
        }
    }
}

