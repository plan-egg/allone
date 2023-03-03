package io.github.planegg.allone.starter.service.user;

import io.github.planegg.allone.starter.dto.CurrentUserCtx;
import io.github.planegg.allone.starter.exception.BizHandleException;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 *
 */
public class CurrentUserContextServiceImpl implements ICurrentUserContextService , MethodInterceptor {

    private final static Logger logger = LoggerFactory.getLogger(CurrentUserContextServiceImpl.class);


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

        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        //TODO 灯笼：登录功能接入后，调用用户获取方式
        String userId = request.getHeader("userId");
        if (userId == null){
            throw new BizHandleException("请在Header里添加参数userId！");
        }
        CurrentUserCtx currentUserCtx = new CurrentUserCtx();
        currentUserCtx.setUserId(Long.valueOf(userId));

        try {
            initCurrentUserCtx(currentUserCtx);
            Object result = invocation.proceed();
            return result;
        }finally {
            cleanCurrentUserCtx();
        }
    }
}
