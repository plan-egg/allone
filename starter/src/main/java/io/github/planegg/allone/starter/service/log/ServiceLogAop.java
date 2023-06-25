package io.github.planegg.allone.starter.service.log;

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
 * 接口日志切面
 */
@Configuration
@ConditionalOnProperty(name = "allone.aop.pointcut.service")
@ConditionalOnBean(name = "logService")
@Order(SysInitOrderC.ORDER_NUM_AOP)
public class ServiceLogAop {

    private final static Logger logger = LoggerFactory.getLogger(ServiceLogAop.class);

    @Value("${allone.aop.pointcut.service}")
    private String serviceLogPointcut;
    @Autowired
    @Qualifier("logService")
    private MethodInterceptor serviceLogService;

    @Bean
    public DefaultPointcutAdvisor getServiceLogPointcutAdvisor() {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(serviceLogPointcut);
        // 配置增强类advisor
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor();
        advisor.setPointcut(pointcut);
        advisor.setAdvice(serviceLogService);
        logger.info("AOP[ServiceLog] init done! pointcut={} , service={}",serviceLogPointcut,serviceLogService.getClass().getName());
        return advisor;
    }


}
