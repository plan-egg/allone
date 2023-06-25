package io.github.planegg.allone.starter.service.lock;

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
@ConditionalOnBean(name = "dLockService")
@Order(SysInitOrderC.ORDER_NUM_AOP)
public class DistributedLockAop {

    private final static Logger logger = LoggerFactory.getLogger(DistributedLockAop.class);

    @Value("${allone.aop.pointcut.service}")
    private String servicePointcut;

    @Autowired
    @Qualifier("dLockService")
    private MethodInterceptor dLockService;

    @Bean
    public DefaultPointcutAdvisor getDistributedLockAopAdvisor() {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        String lockPointcut = servicePointcut + " && @annotation(io.github.planegg.allone.starter.service.lock.DLock)";
        pointcut.setExpression(lockPointcut);
        // 配置增强类advisor
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor();
        advisor.setPointcut(pointcut);
        advisor.setAdvice(dLockService);
        logger.info("AOP[DLock] init done!pointcut={} , service={}",servicePointcut,dLockService.getClass().getName());
        return advisor;
    }


}
