package com.spt.learningmanage.service;

/**
 * 当前请求用户来源抽象。
 */
public interface CurrentUserProvider {

    /**
     * 获取当前用户ID，不存在时抛出统一未登录异常。
     */
    Long getRequiredCurrentUserId();
}

