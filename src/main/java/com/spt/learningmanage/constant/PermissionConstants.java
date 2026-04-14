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
}

