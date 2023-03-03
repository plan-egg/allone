package io.github.planegg.allone.starter.service.log;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Service类日志切面
 */
public class ServiceLogServiceImpl implements MethodInterceptor {


    @Before("serviceLog()")
    public void doBefore(JoinPoint point) throws Throwable {


        String className = point.getTarget().getClass().toString();
        String methodName = point.getSignature().getName();
        Object[] param = point.getArgs();

        ObjectMapper mapper = new ObjectMapper();

        try {
            logger.info("调用前：{}#{}传递的参数:{}",className,methodName, mapper.writeValueAsString(param));
        }catch (Exception e){
            logger.error("调用前：{}#{}传递的参数转换类型时出错",className,methodName);
        }

    }

    private final static Logger logger = LoggerFactory.getLogger(ServiceLogServiceImpl.class);

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        String className = invocation.getThis().getClass().getName();
        String methodName = invocation.getMethod().getName();
        Object[] param =invocation.getArguments();

        ObjectMapper mapper = new ObjectMapper();
        try {
            logger.info("调用前：{}#{}传递的参数:{}",className,methodName, mapper.writeValueAsString(param));
        }catch (Exception e){
            logger.error("调用前：{}#{}传递的参数转换类型时出错",className,methodName);
        }

        // 环绕通知最重要：定义整个目标方法都要执行
        Object object = invocation.proceed();

        return object;
    }

}
