package io.github.planegg.allone.starter.service.log;

import com.alibaba.nacos.shaded.com.google.gson.Gson;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.planegg.allone.starter.common.constant.SysInitOrderC;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("logService")
//@ConditionalOnMissingBean(name = "logService")
@Order(SysInitOrderC.ORDER_NUM_BEAN)
public class LogServiceImpl implements MethodInterceptor {

    private final static Logger logger = LoggerFactory.getLogger(LogServiceImpl.class);

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        String className = invocation.getThis().getClass().getName();
        String methodName = invocation.getMethod().getName();
        Object[] param =invocation.getArguments();

        ObjectMapper mapper = new ObjectMapper();
        try {
            logger.info("调用前：{}#{}传递的参数:{}",className,methodName, /*mapper.writeValueAsString(param)*/ new Gson().toJson(param));
        }catch (Exception e){
            logger.error("调用前：{}#{}传递的参数转换类型时出错",className,methodName,e);
        }

        // 环绕通知最重要：定义整个目标方法都要执行
        Object object = invocation.proceed();

        //TODO 灯笼：避免返回结果过大
        try {
//            if (object instanceof List )
            logger.info("调用后：{}#{}的返回:{}",className,methodName, new Gson().toJson(object));
        }catch (Exception e){
            logger.error("调用后：{}#{}的返回转换类型时出错",className,methodName);
        }
        return object;
    }
}
