package io.github.planegg.allone.starter.service.lock;

import io.github.planegg.allone.starter.exception.DistributedLockException;

import java.util.function.Function;

/**
 * 分布式锁服务
 */
public interface IDistributedLockService {


    /**
     * 获取分布式锁（可动态传入扩展key）
     * @param keyE 锁的key，枚举类，且必须实现 IDistributedLockKeyService 接口
     * @param keyExt 扩展key值，用于动态接收key值，如key  【misc:orderNO:230101】 ，这里的 230101 就是动态传入的。
     * @param <T> 锁的key的类型
     */
    <T extends Enum & IDistributedLockKeyDti> boolean excuteWithTryLock(Function<Object, Void> bizService , Object bizServiceParm
            ,T keyE, String... keyExt ) ;
    /**
     * 获取分布式锁
     * @param keyE 缓存的key，枚举类，且必须实现 IDistributedLockKeyService 接口
     * @param <T> 锁的key的类型
     */
    <T extends Enum & IDistributedLockKeyDti> boolean excuteWithTryLock(Function<Object, Void>  bizService , Object bizServiceParm
            ,T keyE );
    /**
     * 获取分布式锁（可动态传入扩展key）
     * @param keyE 锁的key，枚举类，且必须实现 IDistributedLockKeyService 接口
     * @param keyExt 扩展key值，用于动态接收key值，如key  【misc:orderNO:230101】 ，这里的 230101 就是动态传入的。
     * @param <T> 锁的key的类型
     */
    <T extends Enum & IDistributedLockKeyDti,F1,F2> F2 getWithTryLock( Function<F1, F2>  bizService, F1 bizServiceParm
            , Class<F1> f1, Class<F2> f2,T keyE, String... keyExt)  throws DistributedLockException;
    /**
     * 获取分布式锁
     * @param keyE 缓存的key，枚举类，且必须实现 IDistributedLockKeyService 接口
     * @param <T> 锁的key的类型
     */
    <T extends Enum & IDistributedLockKeyDti,F1,F2> F2 getWithTryLock(Function<F1, F2>  bizService, F1 bizServiceParm
            , Class<F1> f1, Class<F2> f2,T keyE)  throws DistributedLockException;

    /**
     * 释放资源
     */
    void shutdown();
}
