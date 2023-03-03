package io.github.planegg.allone.starter.service.lock;

import java.util.concurrent.TimeUnit;

public interface IDistributedLockKeyDti {
    /**
     * 获取key分组
     * 一般用系统/模块标识作为分组名
     * @return
     */
    String getKeyGroup();

    /**
     * 获取取锁等待时间值（默认秒）
     * @return
     */
    Long getWaitTime();
    /**
     * 获取取锁等待时间单位（默认秒）
     * @return
     */
    TimeUnit getWaitTimeUnit();
    /**
     * 获取锁过期时间值（默认秒）
     * @return
     */
    Long getExpTime();
    /**
     * 获取锁过期时间单位（默认秒）
     * @return
     */
    TimeUnit getExpTimeUnit();
}
