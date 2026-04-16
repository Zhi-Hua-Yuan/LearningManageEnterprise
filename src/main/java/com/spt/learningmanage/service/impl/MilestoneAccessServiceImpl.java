package com.spt.learningmanage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.spt.learningmanage.exception.BusinessException;
import com.spt.learningmanage.exception.ErrorCode;
import com.spt.learningmanage.mapper.MilestoneMapper;
import com.spt.learningmanage.mapper.ProjectMapper;
import com.spt.learningmanage.model.entity.Milestone;
import com.spt.learningmanage.model.entity.Project;
import com.spt.learningmanage.service.MilestoneAccessService;
import com.spt.learningmanage.service.TenantService;
import com.spt.learningmanage.utils.UserHolder;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * 里程碑访问校验实现。
 * 当前仅采用 tenant_id + milestone.user_id（owner）模式，不引入协作新字段。
 */
@Service
public class MilestoneAccessServiceImpl implements MilestoneAccessService {

    @Resource
    private MilestoneMapper milestoneMapper;

    @Resource
    private ProjectMapper projectMapper;

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
    public LambdaQueryWrapper<Milestone> ownedQuery() {
        return new LambdaQueryWrapper<Milestone>()
                .eq(Milestone::getTenantId, requireCurrentTenantId())
                .eq(Milestone::getUserId, requireCurrentUserId());
    }

    @Override
    public LambdaUpdateWrapper<Milestone> ownedUpdate() {
        return new LambdaUpdateWrapper<Milestone>()
                .eq(Milestone::getTenantId, requireCurrentTenantId())
                .eq(Milestone::getUserId, requireCurrentUserId());
    }

    @Override
    public Milestone requireOwnedMilestone(Long milestoneId) {
        requireCurrentUserId();
        if (milestoneId == null || milestoneId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "里程碑 ID 不合法");
        }
        LambdaQueryWrapper<Milestone> wrapper = ownedQuery();
        wrapper.eq(Milestone::getId, milestoneId);
        Milestone milestone = milestoneMapper.selectOne(wrapper);
        if (milestone == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "里程碑不存在");
        }
        return milestone;
    }

    @Override
    public Project requireOwnedProjectForMilestone(Long projectId) {
        Long userId = requireCurrentUserId();
        if (projectId == null || projectId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "项目 ID 不合法");
        }
        Long tenantId = requireCurrentTenantId();
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Project::getId, projectId)
                .eq(Project::getTenantId, tenantId)
                .eq(Project::getUserId, userId)
                .isNull(Project::getDeletedAt);
        Project project = projectMapper.selectOne(wrapper);
        if (project == null) {
            throw new BusinessException(ErrorCode.PROJECT_NOT_FOUND);
        }
        return project;
    }
}

