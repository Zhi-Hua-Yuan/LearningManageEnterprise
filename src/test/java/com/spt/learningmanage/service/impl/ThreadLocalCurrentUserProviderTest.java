package com.spt.learningmanage.service.impl;

import com.spt.learningmanage.exception.BusinessException;
import com.spt.learningmanage.exception.ErrorCode;
import com.spt.learningmanage.utils.UserHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ThreadLocalCurrentUserProviderTest {

    private final ThreadLocalCurrentUserProvider currentUserProvider = new ThreadLocalCurrentUserProvider();

    @AfterEach
    void cleanup() {
        UserHolder.remove();
    }

    @Test
    void getRequiredCurrentUserId_shouldReturnUserId_whenUserExists() {
        UserHolder.set(1001L);

        Long userId = currentUserProvider.getRequiredCurrentUserId();

        assertEquals(1001L, userId);
    }

    @Test
    void getRequiredCurrentUserId_shouldThrowNotLogin_whenUserMissing() {
        BusinessException exception = assertThrows(BusinessException.class, currentUserProvider::getRequiredCurrentUserId);

        assertEquals(ErrorCode.NOT_LOGIN_ERROR, exception.getErrorCode());
    }
}

