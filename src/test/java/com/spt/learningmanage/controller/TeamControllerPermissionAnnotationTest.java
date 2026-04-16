package com.spt.learningmanage.controller;

import com.spt.learningmanage.annotation.RequirePermission;
import com.spt.learningmanage.constant.PermissionConstants;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TeamControllerPermissionAnnotationTest {

    @Test
    void shouldRequirePermissionsOnTeamCollaborationEntryPoints() throws NoSuchMethodException {
        Method createTeamMethod = TeamController.class.getMethod("createTeam");
        Method manageMemberMethod = TeamController.class.getMethod("manageMember");

        RequirePermission createPermission = createTeamMethod.getAnnotation(RequirePermission.class);
        RequirePermission manageMemberPermission = manageMemberMethod.getAnnotation(RequirePermission.class);

        assertNotNull(createPermission);
        assertNotNull(manageMemberPermission);
        assertEquals(PermissionConstants.TEAM_CREATE, createPermission.value());
        assertEquals(PermissionConstants.TEAM_MANAGE_MEMBER, manageMemberPermission.value());
    }
}

