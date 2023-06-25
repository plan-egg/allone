package io.github.planegg.allone.starter.service.user;

import io.github.planegg.allone.starter.dto.CurrentUserCtx;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用户信息上下文保存服务
 */
public abstract class ACurrentUserContextService implements ICurrentUserContextService , MethodInterceptor {

    private final static Logger logger = LoggerFactory.getLogger(ACurrentUserContextService.class);


    @Override
    public void initCurrentUserCtx(CurrentUserCtx currentUserCtx) {
        CurrentUserContextHolder.set(currentUserCtx);
    }

    @Override
    public void cleanCurrentUserCtx() {
        CurrentUserContextHolder.remove();
    }

    /**
     * 保存登录用户信息到ThreadLocal
     * @param invocation
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        CurrentUserCtx userCtx = createUserCtx();
        try {
            initCurrentUserCtx(userCtx);
            Object result = invocation.proceed();
            return result;
        }finally {
            cleanCurrentUserCtx();
        }
    }

    /**
     *
     * @return
     */
    protected abstract CurrentUserCtx createUserCtx();
}
