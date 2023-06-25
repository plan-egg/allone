package io.github.planegg.allone.starter.common.constant;

import io.github.planegg.allone.starter.service.lock.IDistributedLockKeyDti;

import java.util.concurrent.TimeUnit;

/**
 * 杂项服务的分布式锁key
 */
public enum DistributedLockKeyAlloneE implements IDistributedLockKeyDti {
    /**
     * id生成批次起始值
     * 用于生成id时的起始值
     */
    serial_get_batch_$(30L)
    ;
    /**
     * key分组
     * 一般用系统/模块标识作为分组名
     */
    private String keyGroup = "STL";
    /**
     * 取锁等待时间值
     */
    private Long waitTime;
    /**
     * 取锁等待时间单位
     */
    private TimeUnit waitTimeUnit;
    /**
     * 缓存过期时间值
     */
    private Long expTime;
    /**
     * 缓存过期时间单位
     */
    private TimeUnit expTimeUnit;



    DistributedLockKeyAlloneE(Long waitTime) {
        this.waitTime = waitTime;
        this.waitTimeUnit = TimeUnit.SECONDS;
    }

    DistributedLockKeyAlloneE(Long waitTime, TimeUnit waitTimeUnit) {
        this.waitTime = waitTime;
        this.waitTimeUnit = waitTimeUnit;
    }
    DistributedLockKeyAlloneE(Long waitTime, TimeUnit waitTimeUnit, Long expTime, TimeUnit expTimeUnit) {
        this.waitTime = waitTime;
        this.waitTimeUnit = waitTimeUnit;
        this.expTime = expTime;
        this.expTimeUnit = expTimeUnit;
    }

    @Override
    public String getKeyGroup() {
        return keyGroup;
    }

    @Override
    public Long getWaitTime() {
        return waitTime;
    }

    @Override
    public TimeUnit getWaitTimeUnit() {
        return waitTimeUnit;
    }
    @Override
    public Long getExpTime() {
        return expTime;
    }

    @Override
    public TimeUnit getExpTimeUnit() {
        return expTimeUnit;
    }

}