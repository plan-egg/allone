package io.github.planegg.allone.starter.service.log;

import org.aopalliance.intercept.MethodInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 接口日志切面
 */
@Configuration
@ConditionalOnProperty(name = "allone.aop.pointcut.service-log")
public class ServiceLogAop {

    private final static Logger logger = LoggerFactory.getLogger(ServiceLogAop.class);

    @Value("${allone.aop.pointcut.service-log}")
    private String serviceLogPointcut;


    @Bean
    public DefaultPointcutAdvisor getServiceLogPointcutAdvisor() {
        MethodInterceptor interceptor = new ApiLogServiceImpl();
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(serviceLogPointcut);
        // 配置增强类advisor
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor();
        advisor.setPointcut(pointcut);
        advisor.setAdvice(interceptor);
        logger.info("AOP[ServiceLog] init done!");
        return advisor;
    }


}
