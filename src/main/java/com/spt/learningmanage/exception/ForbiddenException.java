package com.spt.learningmanage.exception;

/**
 * 权限不足异常，统一映射为 403。
 */
public class ForbiddenException extends BusinessException {

    public ForbiddenException() {
        super(ErrorCode.FORBIDDEN_ERROR);
    }

    public ForbiddenException(String message) {
        super(ErrorCode.FORBIDDEN_ERROR, message);
    }
}

