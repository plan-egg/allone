package io.github.planegg.allone.starter.service.lock;

import io.github.planegg.allone.starter.exception.DistributedLockException;
import io.github.planegg.allone.starter.exception.ItHandleException;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * redisson实现的分布式锁服务
 */

public class RedissonServiceImpl implements IDistributedLockService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private RedissonClient redissonClient;

    private RedissonServiceImpl() {
    }

    public RedissonServiceImpl(RedissonClient redissonClient){
        this.redissonClient = redissonClient;
    }

    private final static String KEY_SEPARATOR = ":";

    public <T extends Enum & IDistributedLockKeyDti> String getKeyStr(T keyE, String... keyExt){
        String keyStr =  keyE.getKeyGroup() + KEY_SEPARATOR + keyE.name();
        if (keyExt != null && keyExt.length > 0){
            keyStr = keyStr.replaceAll("\\_\\$",KEY_SEPARATOR + "%s");
            keyStr = String.format(keyStr , keyExt);
        }
        return keyStr;
    }

    @Override
    public <T extends Enum & IDistributedLockKeyDti> boolean excuteWithTryLock(Function<Object, Void> bizService
            , Object bizServiceParm,T keyE, String... keyExt ){
        String keyStr = getKeyStr(keyE,keyExt);
        RLock lock = redissonClient.getLock(keyStr);
        boolean isLockOk = lock.tryLock();
        if (!isLockOk){
            return false;
        }
        try {
            bizService.apply(bizServiceParm);
        }catch (Exception e){
            logger.error("方法执行失败！方法={}",bizService.getClass(),e);
            throw new ItHandleException("方法执行失败！方法={}",bizService.getClass(),e);
        }finally {
            lock.unlock();
        }
        return true;
    }

    @Override
    public <T extends Enum & IDistributedLockKeyDti> boolean excuteWithTryLock(Function<Object, Void>  bizService
            , Object bizServiceParm,T keyE){
        return excuteWithTryLock(bizService,bizServiceParm,keyE, null);
    }

    @Override
    public <T extends Enum & IDistributedLockKeyDti,F1,F2> F2 getWithTryLock(Function<F1, F2>  bizService, F1 bizServiceParm
            ,Class<F1> f1, Class<F2> f2,T keyE, String... keyExt)  throws DistributedLockException {
        F2 rs = null;

        String keyStr = getKeyStr(keyE,keyExt);
        RLock lock = redissonClient.getLock(keyStr);
        Long waitTime = keyE.getWaitTime();
        TimeUnit waitTimeUnit = keyE.getWaitTimeUnit();
        boolean isLockOk ;
        try {
            if (waitTime != null && waitTime > 0 && waitTimeUnit != null) {
                isLockOk = lock.tryLock(waitTime, waitTimeUnit);
            } else {
                isLockOk = lock.tryLock();
            }
            if (!isLockOk) {
                throw new  DistributedLockException("获取锁失败，key={}", keyStr);
            }
            rs = bizService.apply(bizServiceParm);
        }catch (InterruptedException e){
            logger.error("获取锁失败！方法={}",bizService.getClass(),e);
            throw new ItHandleException("方法执行失败！方法={}",bizService.getClass().getName(),e);
        }catch (Exception e){
            logger.error("方法执行失败！方法={}",bizService.getClass(),e);
            throw new ItHandleException("方法执行失败！上锁key={},方法={}",keyStr,bizService.getClass().getName(),e);
        }finally {
            lock.unlock();
        }
        return rs;
    }

    @Override
    public <T extends Enum & IDistributedLockKeyDti,F1,F2> F2 getWithTryLock(Function<F1, F2>  bizService , F1 bizServiceParm
            , Class<F1> f1, Class<F2> f2,T keyE){
        return getWithTryLock(bizService,bizServiceParm,f1,f2,keyE, null );
    }

    @Override
    public void shutdown() {
        redissonClient.shutdown();
    }
}
