package com.spt.learningmanage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spt.learningmanage.constant.DeleteSourceConstant;
import com.spt.learningmanage.constant.ProjectConstant;
import com.spt.learningmanage.exception.BusinessException;
import com.spt.learningmanage.exception.ErrorCode;
import com.spt.learningmanage.mapper.MilestoneMapper;
import com.spt.learningmanage.mapper.ProjectMapper;
import com.spt.learningmanage.mapper.TaskMapper;
import com.spt.learningmanage.model.dto.project.ProjectCreateRequest;
import com.spt.learningmanage.model.dto.project.ProjectQueryRequest;
import com.spt.learningmanage.model.dto.project.ProjectReorderRequest;
import com.spt.learningmanage.model.dto.project.ProjectUpdateRequest;
import com.spt.learningmanage.model.entity.Milestone;
import com.spt.learningmanage.model.entity.Project;
import com.spt.learningmanage.model.entity.Task;
import com.spt.learningmanage.model.vo.project.ProjectVo;
import com.spt.learningmanage.service.ProjectAccessService;
import com.spt.learningmanage.service.ProjectService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 项目领域服务。
 * 当前阶段 user_id 统一按创建者（owner）语义理解，并与 tenant_id 共同参与访问过滤。
 */
@Slf4j
@Service
public class ProjectServiceImpl implements ProjectService {

    @Resource
    private ProjectMapper projectMapper;


    @Resource
    private ProjectAccessService projectAccessService;

    @Resource
    private TaskMapper taskMapper;

    @Resource
    private MilestoneMapper milestoneMapper;

    /**
     * 创建项目，返回项目ID。
     */
    @Override
    public Long create(ProjectCreateRequest projectCreateRequest) {
        Long userId = projectAccessService.requireCurrentUserId();
        Long tenantId = projectAccessService.requireCurrentTenantId();
        if (projectCreateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不能为空");
        }
        validateName(projectCreateRequest.getName());
        validateIcon(projectCreateRequest.getIcon());
        validateColor(projectCreateRequest.getColor());
        validateDateRange(projectCreateRequest.getStartDate(), projectCreateRequest.getEndDate());

        Project project = new Project();
        project.setName(projectCreateRequest.getName().trim());
        project.setIcon(projectCreateRequest.getIcon());
        project.setColor(projectCreateRequest.getColor());
        project.setGoal(projectCreateRequest.getGoal());
        project.setStartDate(projectCreateRequest.getStartDate());
        project.setEndDate(projectCreateRequest.getEndDate());
        project.setStatus(ProjectConstant.STATUS_ACTIVE);
        project.setIsDelete(0);
        project.setTenantId(tenantId);
        project.setUserId(userId);
        project.setOrderNo(getNextOrderNo());

        int rows = projectMapper.insert(project);
        if (rows != 1 || project.getId() == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建项目失败");
        }
        return project.getId();
    }

    /**
     * 根据ID查询项目详情。
     */
    @Override
    public ProjectVo getById(Long id) {
        if (id == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "项目 ID 不能为空");
        }
        Project project = projectAccessService.requireOwnedActiveProject(id);
        if (project == null) {
            throw new BusinessException(ErrorCode.PROJECT_NOT_FOUND);
        }
        return toVo(project);
    }

    /**
     * 分页查询项目列表。
     */
    @Override
    public Page<ProjectVo> list(ProjectQueryRequest projectQueryRequest) {
        ProjectQueryRequest validProjectQueryRequest =
                projectQueryRequest == null ? new ProjectQueryRequest() : projectQueryRequest;
        long pageNum = safePageNum(validProjectQueryRequest.getPageNum());
        long pageSize = safePageSize(validProjectQueryRequest.getPageSize());

        LambdaQueryWrapper<Project> wrapper = projectAccessService.ownedActiveQuery();
        if (validProjectQueryRequest.getStatus() != null) {
            wrapper.eq(Project::getStatus, validProjectQueryRequest.getStatus());
        }
        if (StringUtils.hasText(validProjectQueryRequest.getKeyword())) {
            wrapper.like(Project::getName, validProjectQueryRequest.getKeyword());
        }
        wrapper.orderByAsc(Project::getOrderNo).orderByDesc(Project::getCreateTime);

        Page<Project> page = new Page<>(pageNum, pageSize);
        Page<Project> resultPage = projectMapper.selectPage(page, wrapper);
        Page<ProjectVo> voPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        voPage.setRecords(resultPage.getRecords().stream().map(this::toVo).toList());
        return voPage;
    }

    /**
     * 更新项目信息。
     */
    @Override
    public void update(ProjectUpdateRequest projectUpdateRequest) {
        if (projectUpdateRequest == null || projectUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "项目 ID 不能为空");
        }
        Project existing = projectAccessService.requireOwnedActiveProject(projectUpdateRequest.getId());

        String newName = projectUpdateRequest.getName() != null
                ? projectUpdateRequest.getName().trim() : existing.getName();
        validateName(newName);

        String newIcon = projectUpdateRequest.getIcon() != null
                ? projectUpdateRequest.getIcon() : existing.getIcon();
        validateIcon(newIcon);

        String newColor = projectUpdateRequest.getColor() != null
                ? projectUpdateRequest.getColor() : existing.getColor();
        validateColor(newColor);

        String newGoal = projectUpdateRequest.getGoal() != null
                ? projectUpdateRequest.getGoal() : existing.getGoal();
        Integer newStatus = projectUpdateRequest.getStatus() != null
                ? projectUpdateRequest.getStatus() : existing.getStatus();
        validateStatus(newStatus);

        LocalDate newStartDate = projectUpdateRequest.getStartDate() != null
                ? projectUpdateRequest.getStartDate() : existing.getStartDate();
        LocalDate newEndDate = projectUpdateRequest.getEndDate() != null
                ? projectUpdateRequest.getEndDate() : existing.getEndDate();
        validateDateRange(newStartDate, newEndDate);

        Project update = new Project();
        update.setId(projectUpdateRequest.getId());
        update.setName(newName);
        update.setIcon(newIcon);
        update.setColor(newColor);
        update.setGoal(newGoal);
        update.setStatus(newStatus);
        update.setStartDate(newStartDate);
        update.setEndDate(newEndDate);
        update.setTenantId(existing.getTenantId());
        update.setUserId(existing.getUserId());

        int rows = projectMapper.updateById(update);
        if (rows != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新项目失败");
        }
    }

    /**
     * 调整项目排序。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void reorder(List<ProjectReorderRequest> reorderRequests) {
        if (reorderRequests == null || reorderRequests.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "排序列表不能为空");
        }

        Set<Long> idSet = new HashSet<>();
        Set<Integer> orderNoSet = new HashSet<>();
        for (ProjectReorderRequest reorderRequest : reorderRequests) {
            if (reorderRequest == null || reorderRequest.getId() == null || reorderRequest.getId() <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "项目 ID 不合法");
            }
            if (reorderRequest.getOrderNo() == null || reorderRequest.getOrderNo() < 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "排序序号不合法");
            }
            if (!idSet.add(reorderRequest.getId())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "项目列表中存在重复 ID");
            }
            if (!orderNoSet.add(reorderRequest.getOrderNo())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "排序序号不能重复");
            }
        }

        List<Project> existingProjects = projectAccessService.listOwnedActiveProjectsByIds(idSet);
        if (existingProjects.size() != reorderRequests.size()) {
            throw new BusinessException(ErrorCode.PROJECT_NOT_FOUND, "存在无权限或不存在的项目");
        }

        Map<Long, Project> projectById = new HashMap<>();
        for (Project existingProject : existingProjects) {
            projectById.put(existingProject.getId(), existingProject);
        }

        for (ProjectReorderRequest reorderRequest : reorderRequests) {
            Project existingProject = projectById.get(reorderRequest.getId());
            if (existingProject == null) {
                throw new BusinessException(ErrorCode.PROJECT_NOT_FOUND, "存在无权限或不存在的项目");
            }
            Project update = new Project();
            update.setId(reorderRequest.getId());
            update.setTenantId(existingProject.getTenantId());
            update.setUserId(existingProject.getUserId());
            update.setOrderNo(reorderRequest.getOrderNo());
            int rows = projectMapper.updateById(update);
            if (rows != 1) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新项目排序失败");
            }
        }
    }

    /**
     * 归档项目。
     */
    @Override
    public void archive(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "项目 ID 列表不能为空");
        }
        for (Long id : ids) {
            if (id == null || id <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "项目 ID 不合法");
            }
        }

        // 检查所有项目是否存在
        List<Project> existingProjects = projectAccessService.listOwnedActiveProjectsByIds(ids);
        if (existingProjects.size() != ids.size()) {
            throw new BusinessException(ErrorCode.PROJECT_NOT_FOUND);
        }

        // 检查项目是否已经归档
        for (Project project : existingProjects) {
            if (Objects.equals(project.getStatus(), ProjectConstant.STATUS_ARCHIVED)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "项目 " + project.getId() + " 已经归档，无法再次归档");
            }
        }

        // 批量更新状态为归档
        LambdaUpdateWrapper<Project> updateWrapper = projectAccessService.ownedUpdate();
        updateWrapper.in(Project::getId, ids)
                .set(Project::getStatus, ProjectConstant.STATUS_ARCHIVED);

        int rows = projectMapper.update(null, updateWrapper);
        if (rows < ids.size()) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "归档项目失败");
        }
    }

    /**
     * 删除项目。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        if (id == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "项目 ID 不能为空");
        }
        Project existing = projectAccessService.requireOwnedProjectIncludingDeleted(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.PROJECT_NOT_FOUND);
        }
        if (existing.getDeletedAt() != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "项目已删除，请勿重复操作");
        }

        LocalDateTime deleteTime = LocalDateTime.now();

        UpdateWrapper<Task> taskDeleteWrapper = new UpdateWrapper<>();
        taskDeleteWrapper.eq("tenant_id", existing.getTenantId())
                .eq("user_id", existing.getUserId())
                .eq("project_id", id)
                .eq("is_delete", 0)
                .set("is_delete", 1)
                .set("delete_source", DeleteSourceConstant.PROJECT_CASCADE)
                .set("deleted_at", deleteTime);
        if (taskMapper != null) {
            taskMapper.update(null, taskDeleteWrapper);
        }

        UpdateWrapper<Milestone> milestoneDeleteWrapper = new UpdateWrapper<>();
        milestoneDeleteWrapper.eq("tenant_id", existing.getTenantId())
                .eq("user_id", existing.getUserId())
                .eq("project_id", id)
                .eq("is_delete", 0)
                .set("is_delete", 1)
                .set("delete_source", DeleteSourceConstant.PROJECT_CASCADE)
                .set("deleted_at", deleteTime);
        if (milestoneMapper != null) {
            milestoneMapper.update(null, milestoneDeleteWrapper);
        }

        // 软删除：设置 deletedAt
        Project update = new Project();
        update.setId(id);
        update.setTenantId(existing.getTenantId());
        update.setUserId(existing.getUserId());
        update.setDeletedAt(deleteTime);

        int rows = projectMapper.updateById(update);
        if (rows != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除项目失败");
        }
    }

    /**
     * 恢复项目。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recover(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "项目 ID 不能为空");
        }
        Project existing = projectAccessService.requireOwnedProjectIncludingDeleted(id);
        if (existing.getDeletedAt() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "项目未被删除，无法恢复");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime recoverAfter = now.minusDays(30);
        int rows = projectMapper.recoverOwnedProject(existing.getTenantId(), existing.getUserId(), id, recoverAfter);
        if (rows != 1) {
            log.warn("recover project rejected, projectId={}, tenantId={}, userId={}, deletedAt={}, recoverAfter={}",
                    id, existing.getTenantId(), existing.getUserId(), existing.getDeletedAt(), recoverAfter);
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "项目删除超过30天或状态已变化，无法恢复");
        }

        int recoveredTaskCount = taskMapper.recoverByProjectId(existing.getTenantId(), existing.getUserId(), id);
        int recoveredMilestoneCount = milestoneMapper.recoverByProjectId(existing.getTenantId(), existing.getUserId(), id);

        log.info("recover project success, projectId={}, tenantId={}, userId={}, recoveredTaskCount={}, recoveredMilestoneCount={}",
                id, existing.getTenantId(), existing.getUserId(), recoveredTaskCount, recoveredMilestoneCount);
    }


    /**
     * 将实体转换为VO。
     */
    private ProjectVo toVo(Project project) {
        ProjectVo vo = new ProjectVo();
        BeanUtils.copyProperties(project, vo);
        return vo;
    }

    /**
     * 校验项目名称。
     */
    private void validateName(String name) {
        if (!StringUtils.hasText(name)) {
            throw new BusinessException(ErrorCode.PROJECT_NAME_EMPTY);
        }
        if (name.length() > 100) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "项目名称长度不能超过100");
        }
    }

    /**
     * 校验日期区间。
     */
    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "结束日期不能早于开始日期");
        }
    }

    /**
     * 校验项目状态。
     */
    private void validateStatus(Integer status) {
        if (!Objects.equals(status, ProjectConstant.STATUS_ACTIVE)
                && !Objects.equals(status, ProjectConstant.STATUS_ARCHIVED)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "项目状态不合法");
        }
    }

    /**
     * 校验项目图标。
     */
    private void validateIcon(String icon) {
        if (icon != null && icon.length() > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "图标长度不能超过50");
        }
    }

    /**
     * 校验项目颜色。
     */
    private void validateColor(String color) {
        if (color != null && !color.matches("^#[0-9A-Fa-f]{6}$")) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "颜色格式必须为 #RRGGBB");
        }
    }

    /**
     * 规范化页码。
     */
    private long safePageNum(Long pageNum) {
        if (pageNum == null || pageNum < 1) {
            return 1L;
        }
        return pageNum;
    }

    /**
     * 规范化每页条数。
     */
    private long safePageSize(Long pageSize) {
        if (pageSize == null || pageSize < 1) {
            return 10L;
        }
        return Math.min(pageSize, 100L);
    }

    /**
     * 获取当前用户下新的排序号。
     */
    private Integer getNextOrderNo() {
        LambdaQueryWrapper<Project> wrapper = projectAccessService.ownedActiveQuery();
        wrapper.orderByDesc(Project::getOrderNo)
                .last("LIMIT 1");
        Project lastProject = projectMapper.selectOne(wrapper);
        if (lastProject == null || lastProject.getOrderNo() == null) {
            return 0;
        }
        return lastProject.getOrderNo() + 1;
    }

}