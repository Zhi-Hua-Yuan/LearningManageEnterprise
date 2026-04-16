package com.spt.learningmanage.service;

import com.spt.learningmanage.model.entity.Project;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;

import java.util.Collection;
import java.util.List;

/**
 * 项目访问控制入口。
 * 当前阶段基于 tenant + owner（project.user_id）进行访问校验。
 */
public interface ProjectAccessService {

    /**
     * 获取当前登录用户ID，不存在则抛未登录异常。
     */
    Long requireCurrentUserId();

    /**
     * 获取当前租户ID。
     */
    Long requireCurrentTenantId();

    /**
     * 校验并返回当前租户下、当前 owner 可访问的项目。
     */
    Project requireOwnedProject(Long projectId);

    /**
     * 校验并返回当前租户下当前用户可访问的项目。
     * 当前阶段默认等价于 owner 可访问；预留协作成员可见扩展点。
     */
    Project requireAccessibleProject(Long projectId);

    /**
     * 创建带 owner 访问域的查询条件（tenant + user）。
     */
    LambdaQueryWrapper<Project> ownedQuery();

    /**
     * 创建带 owner 访问域的更新条件（tenant + user）。
     */
    LambdaUpdateWrapper<Project> ownedUpdate();

    /**
     * 按当前 owner 访问域批量查询项目。
     */
    List<Project> listOwnedProjectsByIds(Collection<Long> projectIds);
}

