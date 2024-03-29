package io.github.planegg.allone.starter.service.user;

import io.github.planegg.allone.starter.common.constant.SysInitOrderC;
import org.aopalliance.intercept.MethodInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * 用户上下文切面
 */
@Configuration
@ConditionalOnProperty(name = "allone.aop.pointcut.usr-ctx")
@ConditionalOnBean(name = "currentUserContextService")
@Order(SysInitOrderC.ORDER_NUM_AOP)
public class CurrentUserCtxAop {

    private final static Logger logger = LoggerFactory.getLogger(CurrentUserCtxAop.class);

    @Value("${allone.aop.pointcut.usr-ctx}")
    private String usrCtxPointcut;

    @Autowired
    @Qualifier("currentUserContextService")
    private MethodInterceptor currentUserContextService;

    @Bean
    public DefaultPointcutAdvisor getUsrCtxPointcutAdvisor() {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(usrCtxPointcut);
        // 配置增强类advisor
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor();
        advisor.setPointcut(pointcut);
        advisor.setAdvice(currentUserContextService);
        logger.info("AOP[CurrentUserCtxAop] init done! pointcut={} , service={}",usrCtxPointcut,currentUserContextService.getClass().getName());
        return advisor;
    }


}
