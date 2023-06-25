package io.github.planegg.allone.starter.common.constant;

import io.github.planegg.allone.starter.service.cache.ICacheKeyDti;

import java.util.concurrent.TimeUnit;

/**
 * 杂项服务的rediskey
 */
public enum CacheKeyAlloneE implements ICacheKeyDti {
    /**
     * id生成批次起始值
     * 用于生成id时的起始值
     */
    serial_batch_str_num_$(String.class,7,TimeUnit.DAYS)
    /**
     * id生成批次结束值
     * 用于生成id时，当要生成的id大于此值时，就需要从数据库获重新获取下一批次
     */
    , serial_batch_end_num_$(String.class,7,TimeUnit.DAYS)
    /**
     * id生成增长值
     * 用于生成id时的增加值，serial = serial_batch_str_num + serial_batch_count_num
     */
    , serial_batch_count_num(String.class,7,TimeUnit.DAYS)
    ;
    /**
     * key分组
     * 一般用系统/模块标识作为分组名
     */
    private String keyGroup = "ST";
    /**
     * 缓存值类型
     */
    private Class valClzType ;
    /**
     * 缓存过期时间值
     */
    private int expTime;
    /**
     * 缓存过期时间单位
     */
    private TimeUnit expTimeUnit;

    CacheKeyAlloneE(int expTime) {
        this.valClzType = String.class;
        this.expTime = expTime;
        this.expTimeUnit = TimeUnit.SECONDS;
    }

    CacheKeyAlloneE(Class valClzType, int expTime) {
        this.valClzType = valClzType;
        this.expTime = expTime;
        this.expTimeUnit = TimeUnit.SECONDS;
    }

    CacheKeyAlloneE(Class valClzType, int expTime, TimeUnit expTimeUnit) {
        this.valClzType = valClzType;
        this.expTime = expTime;
        this.expTimeUnit = expTimeUnit;
    }

    @Override
    public String getKeyGroup() {
        return keyGroup;
    }

    @Override
    public Class getValClzType() {
        return valClzType;
    }

    @Override
    public int getExpTime() {
        return expTime;
    }

    @Override
    public TimeUnit getExpTimeUnit() {
        return expTimeUnit;
    }
}