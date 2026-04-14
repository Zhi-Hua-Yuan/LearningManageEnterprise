package com.spt.learningmanage.service.impl;

import com.spt.learningmanage.exception.BusinessException;
import com.spt.learningmanage.exception.ErrorCode;
import com.spt.learningmanage.service.CurrentUserProvider;
import com.spt.learningmanage.utils.UserHolder;
import org.springframework.stereotype.Component;

/**
 * 基于 ThreadLocal(UserHolder) 的当前用户提供器。
 */
@Component
public class ThreadLocalCurrentUserProvider implements CurrentUserProvider {

    @Override
    public Long getRequiredCurrentUserId() {
        Long userId = UserHolder.get();
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return userId;
    }
}

