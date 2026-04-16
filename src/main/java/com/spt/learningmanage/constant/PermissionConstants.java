package com.spt.learningmanage.constant;

/**
 * RBAC 权限编码常量。
 */
public final class PermissionConstants {

    private PermissionConstants() {
    }

    public static final String PROJECT_CREATE = "project:create";
    public static final String PROJECT_VIEW = "project:view";
    public static final String PROJECT_UPDATE = "project:update";
    public static final String PROJECT_DELETE = "project:delete";
    public static final String PROJECT_ARCHIVE = "project:archive";

    public static final String TASK_CREATE = "task:create";
    public static final String TASK_VIEW = "task:view";
    public static final String TASK_UPDATE = "task:update";
    public static final String TASK_DELETE = "task:delete";
    public static final String TASK_COMPLETE = "task:complete";

    /**
     * 协作阶段权限预留：仅定义编码，不代表已实现团队业务与接口接入。
     */
    public static final String TEAM_CREATE = "team:create";
    public static final String TEAM_MANAGE_MEMBER = "team:manage_member";
    public static final String PROJECT_ASSIGN = "project:assign";
    public static final String TASK_ASSIGN = "task:assign";
    public static final String PROJECT_VIEW_TEAM = "project:view_team";
}

