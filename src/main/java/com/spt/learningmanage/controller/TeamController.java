package com.spt.learningmanage.controller;

import com.spt.learningmanage.annotation.RequirePermission;
import com.spt.learningmanage.common.BaseResponse;
import com.spt.learningmanage.common.ResultUtils;
import com.spt.learningmanage.constant.PermissionConstants;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 团队协作入口占位控制器。
 * 当前仅用于 RBAC 权限注解落点，不包含真实团队业务实现。
 */
@RestController
@RequestMapping("/team")
public class TeamController {

    @RequirePermission(PermissionConstants.TEAM_CREATE)
    @PostMapping("/create")
    public BaseResponse<Boolean> createTeam() {
        return ResultUtils.ok(true);
    }

    @RequirePermission(PermissionConstants.TEAM_MANAGE_MEMBER)
    @PostMapping("/member/manage")
    public BaseResponse<Boolean> manageMember() {
        return ResultUtils.ok(true);
    }
}

