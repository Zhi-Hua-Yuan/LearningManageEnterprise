package com.spt.learningmanage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.spt.learningmanage.exception.BusinessException;
import com.spt.learningmanage.exception.ErrorCode;
import com.spt.learningmanage.constant.PermissionConstants;
import com.spt.learningmanage.mapper.ProjectMapper;
import com.spt.learningmanage.model.entity.Project;
import com.spt.learningmanage.service.ProjectAccessService;
import com.spt.learningmanage.service.RbacService;
import com.spt.learningmanage.service.TenantService;
import com.spt.learningmanage.utils.UserHolder;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 项目访问校验实现。
 * 当前仅采用 tenant_id + project.user_id（owner）模式，不引入协作新字段。
 */
@Service
public class ProjectAccessServiceImpl implements ProjectAccessService {

    @Resource
    private ProjectMapper projectMapper;

    @Resource
    private TenantService tenantService;

    @Resource
    private RbacService rbacService;

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
    public Project requireOwnedActiveProject(Long projectId) {
        requireCurrentUserId();
        if (projectId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "项目 ID 不能为空");
        }
        LambdaQueryWrapper<Project> wrapper = ownedActiveQuery();
        wrapper.eq(Project::getId, projectId);
        Project project = projectMapper.selectOne(wrapper);
        if (project == null) {
            throw new BusinessException(ErrorCode.PROJECT_NOT_FOUND);
        }
        return project;
    }

    @Override
    public Project requireOwnedProjectIncludingDeleted(Long projectId) {
        requireCurrentUserId();
        if (projectId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "项目 ID 不能为空");
        }
        LambdaQueryWrapper<Project> wrapper = ownedQuery();
        wrapper.eq(Project::getId, projectId);
        Project project = projectMapper.selectOne(wrapper);
        if (project == null) {
            throw new BusinessException(ErrorCode.PROJECT_NOT_FOUND);
        }
        return project;
    }

    @Deprecated
    @Override
    public Project requireOwnedProject(Long projectId) {
        return requireOwnedActiveProject(projectId);
    }

    @Override
    public Project requireAccessibleProject(Long projectId) {
        Long userId = requireCurrentUserId();
        if (projectId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "项目 ID 不能为空");
        }
        Long tenantId = requireCurrentTenantId();

        // owner 路径优先，保持当前行为兼容。
        LambdaQueryWrapper<Project> ownerWrapper = ownedQuery();
        ownerWrapper.eq(Project::getId, projectId)
                .isNull(Project::getDeletedAt);
        Project ownerProject = projectMapper.selectOne(ownerWrapper);
        if (ownerProject != null) {
            return ownerProject;
        }

        // 协作预留：具备团队项目可见权限时，允许访问同租户项目。
        boolean hasTeamProjectView = rbacService.hasPermission(userId, tenantId, PermissionConstants.PROJECT_VIEW_TEAM);
        if (hasTeamProjectView) {
            LambdaQueryWrapper<Project> teamWrapper = new LambdaQueryWrapper<>();
            teamWrapper.eq(Project::getId, projectId)
                    .eq(Project::getTenantId, tenantId)
                    .isNull(Project::getDeletedAt);
            Project teamProject = projectMapper.selectOne(teamWrapper);
            if (teamProject != null) {
                return teamProject;
            }
        }

        throw new BusinessException(ErrorCode.PROJECT_NOT_FOUND);
    }

    @Override
    public LambdaQueryWrapper<Project> ownedQuery() {
        return new LambdaQueryWrapper<Project>()
                .eq(Project::getTenantId, requireCurrentTenantId())
                .eq(Project::getUserId, requireCurrentUserId());
    }

    @Override
    public LambdaQueryWrapper<Project> ownedActiveQuery() {
        return ownedQuery().isNull(Project::getDeletedAt);
    }

    @Override
    public LambdaUpdateWrapper<Project> ownedUpdate() {
        return new LambdaUpdateWrapper<Project>()
                .eq(Project::getTenantId, requireCurrentTenantId())
                .eq(Project::getUserId, requireCurrentUserId());
    }

    @Deprecated
    @Override
    public List<Project> listOwnedProjectsByIds(Collection<Long> projectIds) {
        if (projectIds == null || projectIds.isEmpty()) {
            return List.of();
        }
        LambdaQueryWrapper<Project> wrapper = ownedQuery();
        wrapper.in(Project::getId, projectIds);
        return projectMapper.selectList(wrapper);
    }

    @Override
    public List<Project> listOwnedActiveProjectsByIds(Collection<Long> projectIds) {
        if (projectIds == null || projectIds.isEmpty()) {
            return List.of();
        }
        LambdaQueryWrapper<Project> wrapper = ownedActiveQuery();
        wrapper.in(Project::getId, projectIds);
        return projectMapper.selectList(wrapper);
    }
}

