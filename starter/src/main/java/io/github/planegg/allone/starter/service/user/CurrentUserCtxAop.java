package io.github.planegg.allone.starter.service.user;

import io.github.planegg.allone.starter.service.log.ApiLogServiceImpl;
import org.aopalliance.intercept.MethodInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 用户上下文切面
 */
@Configuration
//@ConditionalOnProperty(name = "allone.aop.pointcut.usr-ctx")
public class CurrentUserCtxAop {

    private final static Logger logger = LoggerFactory.getLogger(CurrentUserCtxAop.class);

    @Value("${allone.aop.pointcut.usr-ctx}")
    private String usrCtxPointcut;


    @Bean
    public DefaultPointcutAdvisor getUsrCtxPointcutAdvisor() {
        MethodInterceptor interceptor = new CurrentUserContextServiceImpl();
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(usrCtxPointcut);
        // 配置增强类advisor
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor();
        advisor.setPointcut(pointcut);
        advisor.setAdvice(interceptor);
        logger.debug("AOP[UsrCtx] init done!");
        return advisor;
    }


}
