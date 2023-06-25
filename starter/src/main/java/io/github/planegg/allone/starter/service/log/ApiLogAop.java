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
@ConditionalOnProperty(name = "allone.aop.pointcut.api")
@ConditionalOnBean(name = "logService")
@Order(SysInitOrderC.ORDER_NUM_AOP)
public class ApiLogAop {

    private final Logger logger = LoggerFactory.getLogger(ApiLogAop.class);

    @Value("${allone.aop.pointcut.api}")
    private String apiPointcut;

    @Autowired
    @Qualifier("logService")
    private MethodInterceptor apiLogService;

    @Bean
    public DefaultPointcutAdvisor getApiLogPointcutAdvisor() {

        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(apiPointcut);
        // 配置增强类advisor
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor();
        advisor.setPointcut(pointcut);
        advisor.setAdvice(apiLogService);
        logger.info("AOP[ApiLog] init done! pointcut={} , service={}",apiPointcut,apiLogService.getClass().getName());
        return advisor;
    }


}
