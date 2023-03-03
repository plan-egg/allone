package io.github.planegg.allone.starter.service.user;

import io.github.planegg.allone.starter.dto.CurrentUserCtx;

/**
 * 当前用户信息存取服务
 */
public interface ICurrentUserContextService<T extends CurrentUserCtx> {
    /**
     * 初始化用户信息到上下文
     * @param currentUserCtx
     */
    void initCurrentUserCtx(T currentUserCtx);

    /**
     * 清理上下文的用户信息
     */
    void cleanCurrentUserCtx();
}
