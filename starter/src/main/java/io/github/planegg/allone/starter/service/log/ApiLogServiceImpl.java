package io.github.planegg.allone.starter.service.log;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*@Service("apiLogService")
@ConditionalOnMissingBean*/
public class ApiLogServiceImpl implements MethodInterceptor {

    private final static Logger logger = LoggerFactory.getLogger(ApiLogServiceImpl.class);

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        String className = invocation.getThis().getClass().getName();
        String methodName = invocation.getMethod().getName();
        Object[] param =invocation.getArguments();

        ObjectMapper mapper = new ObjectMapper();
        try {
            logger.info("调用前：{}#{}传递的参数:{}",className,methodName, mapper.writeValueAsString(param));
        }catch (Exception e){
            logger.error("调用后：{}#{}传递的参数转换类型时出错",className,methodName);
        }

        // 环绕通知最重要：定义整个目标方法都要执行
        Object object = invocation.proceed();

        //TODO 灯笼：避免返回结果过大
        try {
            logger.info("调用后：{}#{}的返回:{}",className,methodName, mapper.writeValueAsString(object));
        }catch (Exception e){
            logger.error("调用后：{}#{}的返回转换类型时出错",className,methodName);
        }
        return object;
    }
}
