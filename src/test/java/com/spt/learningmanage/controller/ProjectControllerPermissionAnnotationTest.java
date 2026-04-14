package com.spt.learningmanage.controller;

import com.spt.learningmanage.annotation.RequirePermission;
import com.spt.learningmanage.constant.PermissionConstants;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class ProjectControllerPermissionAnnotationTest {

    @Test
    void shouldKeepPilotAnnotationsOnCreateReadDeleteOnly() throws NoSuchMethodException {
        Method addMethod = ProjectController.class.getMethod("addProject",
                com.spt.learningmanage.model.dto.project.ProjectCreateRequest.class);
        Method getMethod = ProjectController.class.getMethod("getProjectById", Long.class);
        Method deleteMethod = ProjectController.class.getMethod("deleteProject", Long.class);

        Method listMethod = ProjectController.class.getMethod("listProject", Long.class, Long.class, Integer.class, String.class);
        Method updateMethod = ProjectController.class.getMethod("updateProject",
                com.spt.learningmanage.model.dto.project.ProjectUpdateRequest.class);
        Method reorderMethod = ProjectController.class.getMethod("reorderProject", java.util.List.class);
        Method archiveMethod = ProjectController.class.getMethod("archiveProject", java.util.List.class);
        Method recoverMethod = ProjectController.class.getMethod("recoverProject", Long.class);

        RequirePermission addPermission = addMethod.getAnnotation(RequirePermission.class);
        RequirePermission getPermission = getMethod.getAnnotation(RequirePermission.class);
        RequirePermission deletePermission = deleteMethod.getAnnotation(RequirePermission.class);

        assertNotNull(addPermission);
        assertNotNull(getPermission);
        assertNotNull(deletePermission);
        assertEquals(PermissionConstants.PROJECT_CREATE, addPermission.value());
        assertEquals(PermissionConstants.PROJECT_VIEW, getPermission.value());
        assertEquals(PermissionConstants.PROJECT_DELETE, deletePermission.value());

        assertNull(listMethod.getAnnotation(RequirePermission.class));
        assertNull(updateMethod.getAnnotation(RequirePermission.class));
        assertNull(reorderMethod.getAnnotation(RequirePermission.class));
        assertNull(archiveMethod.getAnnotation(RequirePermission.class));
        assertNull(recoverMethod.getAnnotation(RequirePermission.class));
    }
}

