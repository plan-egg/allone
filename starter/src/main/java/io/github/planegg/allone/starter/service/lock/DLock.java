package io.github.planegg.allone.starter.service.lock;

import java.lang.annotation.*;

/**
 * 分布式锁
 * 用于标识方法执行前，先获取分布式锁。完成后或异常时，会释放锁
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface DLock {


    /**
     * 上锁Key的值
     * @return
     */
    String lockKey();

    /**
     * 上锁Key的扩展参数
     * @return
     */
    String[] lockKeyExt();

    /**
     * 定义key的枚举类
     * @return
     */
    Class lockKeyClz();



}
