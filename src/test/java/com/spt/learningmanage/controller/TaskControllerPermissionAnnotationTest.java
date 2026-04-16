package com.spt.learningmanage.controller;

import com.spt.learningmanage.annotation.RequirePermission;
import com.spt.learningmanage.constant.PermissionConstants;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TaskControllerPermissionAnnotationTest {

    @Test
    void shouldRequireTaskAssignPermissionOnAssignEndpoint() throws NoSuchMethodException {
        Method assignMethod = TaskController.class.getMethod("assignTask");
        RequirePermission assignPermission = assignMethod.getAnnotation(RequirePermission.class);

        assertNotNull(assignPermission);
        assertEquals(PermissionConstants.TASK_ASSIGN, assignPermission.value());
    }
}

