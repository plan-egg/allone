package io.github.planegg.allone.starter.service.lock;

import io.github.planegg.allone.starter.common.constant.SysInitOrderC;
import io.github.planegg.allone.starter.exception.BizHandleException;
import io.github.planegg.allone.starter.exception.ItHandleException;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;


@Service("dLockService")
//@ConditionalOnMissingBean(name = "dLockService")
@Order(SysInitOrderC.ORDER_NUM_BEAN)
public class DistributedLockAdvice <T extends Enum & IDistributedLockKeyDti> implements MethodInterceptor {

    private Logger logger = LoggerFactory.getLogger(DistributedLockAdvice.class);

    @Autowired
    private IDistributedLockService  distributedLockService;


    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        Object rs = null;

        DLock dLock = invocation.getClass().getAnnotation(DLock.class);
        String lockKey = dLock.lockKey();
        String[] lockKeyExt = dLock.lockKeyExt();


        if (! dLock.lockKeyClz().isEnum()){
            throw new ItHandleException("lockKeyClz 必须是一个枚举类，当前类：{}", dLock.lockKeyClz().getName());
        }
        if (! IDistributedLockKeyDti.class.isAssignableFrom(dLock.lockKeyClz())){
            throw new ItHandleException("lockKeyClz 必须是类IDistributedLockKeyDti的实现类，当前类：{}", dLock.lockKeyClz().getName());
        }
        T lockKeyDti = null;

        Object[] lockKeyArr = dLock.lockKeyClz().getEnumConstants();
        for (Object lockKeyObj : lockKeyArr) {
            T lockKeyDtiItem = (T) lockKeyObj;
            if (lockKey.equals(lockKeyDtiItem.name())){
                lockKeyDti = lockKeyDtiItem;
                break;
            }
        }

        if (lockKeyDti == null){
            throw new ItHandleException("给定的lockKey没有在指定的类中找到！lockKey={}，当前类：{}",lockKey,dLock.lockKeyClz().getName());
        }

        rs = distributedLockService.getWithTryLock((String s) ->{
            // 环绕通知最重要：定义整个目标方法都要执行
            Object object = null;
            try {
                object = invocation.proceed();
            } catch (Throwable throwable) {
                throw new BizHandleException("使用分布式锁执行业务方法时出错！lockKey={}，keyExt={},执行方法={}，执行参数={}"
                        ,lockKey, JSONObject.wrap(lockKeyExt),invocation.getMethod().getName(),JSONObject.wrap(invocation.getArguments())
                ,throwable);
            }
            return object;
        },null,String.class,Object.class,lockKeyDti,lockKeyExt);

        return rs;
    }

}
