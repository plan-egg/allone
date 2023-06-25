package io.github.planegg.allone.starter.service.user;

import com.alibaba.ttl.TransmittableThreadLocal;
import io.github.planegg.allone.starter.dto.CurrentUserCtx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用户上下文操作方法
 */
public class CurrentUserContextHolder {

    private final static Logger logger = LoggerFactory.getLogger(CurrentUserContextHolder.class);

    private final static ThreadLocal<CurrentUserCtx> userInfoContext = new TransmittableThreadLocal<>();



    public static void set(CurrentUserCtx currentUserCtx){
        userInfoContext.set(currentUserCtx);
    }

    public static CurrentUserCtx get(){
        return userInfoContext.get();
    }

    public static void remove(){
        CurrentUserCtx userCtx = userInfoContext.get();
        if (userCtx == null){
            logger.error("线程【{}】清除用户信息时已为空！");
            return;
        }
        Long userId = userCtx.getUserId();
        logger.debug("线程【{}】开始清除用户信息【id：{}】",Thread.currentThread().getId(),userId);
        userInfoContext.remove();
        logger.debug("线程【{}】清除用户信息【id：{}】后，结果是：{}",Thread.currentThread().getId(),userId,userInfoContext.get());
    }
}
